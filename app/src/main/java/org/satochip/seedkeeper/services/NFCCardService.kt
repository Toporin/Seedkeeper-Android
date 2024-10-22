package org.satochip.seedkeeper.services

import android.annotation.SuppressLint
import android.app.Activity
import android.nfc.NfcAdapter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.satochip.android.NFCCardManager
import org.satochip.client.ApplicationStatus
import org.satochip.client.SatochipCommandSet
import org.satochip.client.SatochipParser
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperLog
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.client.seedkeeper.SeedkeeperSecretOrigin
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.client.seedkeeper.SeedkeeperStatus
import org.satochip.io.APDUException
import org.satochip.io.WrongPINException
import org.satochip.io.BlockedPINException
import org.satochip.io.ResetToFactoryException
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.BackupErrorData
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.utils.CardMismatchException
import org.satochip.seedkeeper.utils.bytesToHex

private const val TAG = "NFCCardService"

@SuppressLint("StaticFieldLeak")
object NFCCardService {
    private var activity: Activity? = null

    private lateinit var cmdSet: SatochipCommandSet
    private var parser: SatochipParser? = null

    // MASTER CARD STATE
    var cardLabel = MutableLiveData("")
    var isCardDataAvailable = MutableLiveData(false)
    var secretHeaders = MutableLiveData<List<SeedkeeperSecretHeader>>()
    var currentSecretObject = MutableLiveData<SeedkeeperSecretObject?>()
    var currentSecretHeader = MutableLiveData<SeedkeeperSecretHeader?>()
    var pinString: String? = null
    var newPinString: String? = null
    var seedkeeperStatus: SeedkeeperStatus? = null
    var cardLogs: MutableList<SeedkeeperLog> = mutableListOf()
    var authenticityStatus = MutableLiveData(AuthenticityStatus.UNKNOWN)
    var certificateList: MutableList<String> = mutableListOf()
    var cardStatus: ApplicationStatus? = null
    var authentikey: ByteArray?  = null

    //BACKUP CARD STATE
    var backupCardStatus: ApplicationStatus? = null
    var backupAuthentikey: ByteArray?  = null
    var backupPinString: String? = null
    var backupSecretHeaders: MutableList<SeedkeeperSecretHeader> = mutableListOf()
    var secretHeadersForBackup: MutableList<SeedkeeperSecretHeader> = mutableListOf()
    var secretObjectsForBackup: MutableList<SeedkeeperSecretObject> = mutableListOf()
    var backupErrors:  MutableList<BackupErrorData> = mutableListOf()
    var backupImportProgress = MutableLiveData(0f)
    var backupExportProgress = MutableLiveData(0f)
    var backupNumberOfSecretsImported = 0

    //GENERATE SECRET
    var passwordData: SecretData? = null // TODO rename

    // NFC
    var resultCodeLive = MutableLiveData(NfcResultCode.BUSY)
    var actionType: NfcActionType = NfcActionType.DO_NOTHING
    val isConnected = MutableLiveData(false) // the app is connected to a card, value updated in SeedkeeperCardListener

    /**
     * Initializes the NFC card service with the provided command set and performs the appropriate action based on `actionType`.
     *
     * This method sets up the command set and parser for NFC operations, then performs an action based on the specified
     * `NfcActionType`. The action types cover a range of operations such as scanning cards, verifying or changing PINs,
     * managing secrets, and handling backup or master cards.
     *
     * @param cmdSet The `SatochipCommandSet` instance used for NFC commands and parsing.
     *
     * @see NfcActionType
     */
    fun initialize(cmdSet: SatochipCommandSet) {
        SatoLog.d(TAG, "initialize Start")
        NFCCardService.cmdSet = cmdSet
        parser = cmdSet.parser
        SatoLog.d(TAG, "initialized")
        resultCodeLive.postValue(NfcResultCode.BUSY)

        when (actionType) {
            NfcActionType.DO_NOTHING -> {}
            NfcActionType.SCAN_CARD -> {
                readCard(isMasterCard = true)
            }
            NfcActionType.CHANGE_PIN -> {
                SatoLog.d(TAG, "initialize NfcActionType.CHANGE_PIN")
                changePin()
            }
            NfcActionType.IMPORT_SECRET -> {
                passwordData?.let { data ->
                    importSecret(data = data)
                }
            }
            NfcActionType.SETUP_CARD -> {
                cardSetup(isMasterCard = true)
            }
            NfcActionType.SETUP_CARD_FOR_BACKUP -> {
                cardSetup(isMasterCard = false)
            }
            NfcActionType.EXPORT_SECRET -> {
                currentSecretHeader.value?.let{ secretHeader ->
                    currentSecretObject.postValue(exportSecret(secretHeader.sid))
                }
            }
            NfcActionType.DELETE_SECRET -> {
                currentSecretHeader.value?.let{ secretHeader ->
                    deleteSecret(secretHeader.sid)
                }
            }
            NfcActionType.EDIT_CARD_LABEL -> {
                cardLabel.value?.let { cardLabel ->
                    editCardLabel(cardLabel)
                }
            }
            NfcActionType.CARD_LOGS -> {
                getCardLogs()
            }
            NfcActionType.SCAN_BACKUP_CARD -> {
                readCard(isMasterCard = false)
            }
            NfcActionType.EXPORT_SECRETS_FROM_MASTER -> { // TODO rename EXPORT_SECRETS_FOR_BACKUP?
                exportSecretsFromMaster()
            }
            NfcActionType.TRANSFER_TO_BACKUP -> { // TODO rename IMPORT_SECRETS_TO_BACKUP
                importSecretsToBackup()
            }
            NfcActionType.RESET_CARD -> {
                requestFactoryReset()
            }
        }
    }

    /**
     * Initiates scanning for NFC cards and sets up the necessary NFC reader mode for the specified activity.
     *
     * @param activity The activity where NFC scanning should be enabled.
     */
    fun scanCardForAction(activity: Activity) {
        SatoLog.d(TAG, "scanCardForAction thread START")
        this.activity = activity
        val cardManager = NFCCardManager()
        cardManager.setCardListener(SatochipCardListenerForAction)
        cardManager.start()

        resultCodeLive.postValue(NfcResultCode.BUSY) // indicate that the NFC scanning process is ongoing.

        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity) //context)
        nfcAdapter?.enableReaderMode(
            activity,
            cardManager,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
        SatoLog.d(TAG, "scanCardForAction thread END")
    }

    /**
     * Disables the NFC reader mode for the current activity to stop scanning for NFC actions.
     */
    fun disableScanForAction() {
        SatoLog.d(TAG, "disableScanForAction Start")
        if (activity != null) {
            if (activity?.isFinishing() == true) {
                SatoLog.e(TAG, "NFCCardService disableScanForAction activity isFinishing()")
                return;
            }
            val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
            nfcAdapter?.disableReaderMode(activity)
            SatoLog.d(TAG, "disableScanForAction disableReaderMode!")
        }
    }

    /**
     * Reads and processes the data from the NFC card to determine its setup status and version.
     */
    private fun readCard(isMasterCard : Boolean = true) {//TODO rename to scanCard
        SatoLog.d(TAG, "readCard Start")
        try {
            isCardDataAvailable.postValue(false)
            cmdSet.cardSelect("seedkeeper").checkOK()

            // Get status
            val rapduStatus = cmdSet.cardGetStatus()
            val cardStatusLocal = ApplicationStatus(rapduStatus)
            if (isMasterCard){
                cardStatus = cardStatusLocal
            } else {
                backupCardStatus = cardStatusLocal
            }
            SatoLog.d(TAG, "readCard cardStatus: $cardStatusLocal")

            // check setup
            if (!cardStatusLocal.isSetupDone) {
                SatoLog.d(TAG, "readCard setup not done CardVersionInt: ${cardStatusLocal.cardVersionInt}")
                if (isMasterCard) {
                    resultCodeLive.postValue(NfcResultCode.REQUIRE_SETUP) // todo: deal backup or master card
                } else {
                    resultCodeLive.postValue(NfcResultCode.REQUIRE_SETUP_FOR_BACKUP)
                }
                return
            }

            // verify PIN
            if (!verifyPin(isMasterCard)){return}

            // get authentikey
            if (isMasterCard){
                authentikey = cmdSet.cardGetAuthentikey()
            }else {
                backupAuthentikey = cmdSet.cardGetAuthentikey()
            }

            // get list of secretHeaders
            // TODO: show progress bar
            try {
                val secretHeadersLocal = cmdSet.seedkeeperListSecretHeaders()
                if (isMasterCard){
                    secretHeaders.postValue(secretHeadersLocal)
                    SatoLog.d(TAG, "readCard fetched list of secret headers for master with size: ${secretHeadersLocal.size}")
                } else {
                    backupSecretHeaders.clear()
                    backupSecretHeaders.addAll(secretHeadersLocal)
                    SatoLog.d(TAG, "readCard fetched list of secret headers for backup with size: ${secretHeadersLocal.size}")
                }
            } catch (e: Exception) {
                secretHeaders.postValue(emptyList())
                resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
                SatoLog.e(TAG, "readCard exception: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            }

            // getCardLabel
            if (isMasterCard) cardLabel.postValue(cmdSet.cardLabel)

            // get Seedkeeper Status if available (master only)
            if (isMasterCard && cardStatusLocal.protocolVersion == 2) {
                seedkeeperStatus = cmdSet.seedkeeperGetStatus()
            }

            // check authenticity
            if (isMasterCard){
                getCardAuthenticty()
            }

            // for backup card, get list of secretHeaders to backup (diff between what's on master versus backup)
            if (!isMasterCard){
                // filter out any secret in master that is already in backup, or that is a pubkey (authentikey)
                secretHeadersForBackup = secretHeaders.value?.filterNot { secretHeader ->
                    backupSecretHeaders.any { backupSecretHeader ->
                        backupSecretHeader.fingerprintBytes.contentEquals(secretHeader.fingerprintBytes)
                    } || secretHeader.type == SeedkeeperSecretType.PUBKEY
                } as MutableList<SeedkeeperSecretHeader>
                SatoLog.d(TAG, "readCard generated list of secretsHeaders for backup with size: ${secretHeadersForBackup.size}")
            }

            // update card status
            if (isMasterCard){
                isCardDataAvailable.postValue(true)
                resultCodeLive.postValue(NfcResultCode.CARD_SCANNED_SUCCESSFULLY)
            } else {
                resultCodeLive.postValue(NfcResultCode.BACKUP_CARD_SCANNED_SUCCESSFULLY)
            }

            // TODO catch wrong pin
        } catch (e: Exception) {
            if (isMasterCard){
                secretHeaders.postValue(emptyList())
            } else {
                backupSecretHeaders.clear()
            }
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "readCard exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        SatoLog.d(TAG, "readCard finished successfully")
        return
    }

    /**
     * Verifies the authenticity of the NFC card and updates the authenticity status.
     */
    private fun getCardAuthenticty() {// todo rename ?
        try {
            SatoLog.d(TAG, "getCardAuthenticty start")
            val authResults = cmdSet.cardVerifyAuthenticity()
            if (authResults != null) {
                if (authResults[0].compareTo("OK") == 0) {
                    authenticityStatus.postValue(AuthenticityStatus.AUTHENTIC)
                } else {
                    authenticityStatus.postValue(AuthenticityStatus.NOT_AUTHENTIC)
                    SatoLog.e(TAG, "getCardAuthenticty failed to authenticate card!")
                }
                certificateList.clear()
                certificateList.addAll(authResults)
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR) // TODO necessary or remove?
            authenticityStatus.postValue(AuthenticityStatus.UNKNOWN)
            SatoLog.e(TAG, "Failed to authenticate card with error: $e")
        }
    }

    /**
     * Setup the NFC card with the necessary configuration and PIN
     */
    private fun cardSetup(isMasterCard: Boolean = true) { // todo setup for backup card?
        SatoLog.d(TAG, "cardSetup start")
        try {
            cmdSet.cardSelect("seedkeeper").checkOK()

            // TODO check authentikey?
            //checkAuthentikey(isMasterCard = true)

            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)
            try {
                cmdSet.cardSetup(5, pinBytes)
            } catch (error: Exception) {
                SatoLog.e(TAG, "cardSetup: Error: $error")
            }

            if (isMasterCard) {
                resultCodeLive.postValue(NfcResultCode.CARD_SETUP_SUCCESSFUL)
            } else {
                resultCodeLive.postValue(NfcResultCode.CARD_SETUP_FOR_BACKUP_SUCCESSFUL)
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "cardSetup exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        SatoLog.d(TAG, "cardSetup successful")
    }

    /**
     * Verifies the PIN entered by the user against the NFC card's set PIN.
     *
     * Handles the response from the card:
     *    - If the PIN is incorrect and the card is not yet blocked, updates the number of attempts left and posts an appropriate result code.
     *    - If the status word is 0x9C0C or 0x63C0, it indicates that the card is blocked, and posts an appropriate result code.
     *    - Otherwise, it indicates a successful PIN verification, updates the isReadyForPinCode status, and returns true.
     *
     * @return true if the PIN is successfully verified, false otherwise.
     */
    private fun verifyPin(isMasterCard : Boolean = true): Boolean { // todo return pinStatus?
        //TODO: remove try/catch + throw for wrong pin/pin blocked
        try {
            SatoLog.d(TAG, "verifyPin start")
            val pinBytes = if (isMasterCard) pinString?.toByteArray(Charsets.UTF_8) else backupPinString?.toByteArray(Charsets.UTF_8)
            cmdSet.setPin0(pinBytes)
            val rapdu = cmdSet.cardVerifyPIN()
            SatoLog.d(TAG, "verifyPin successful")
            return true
        } catch (e: WrongPINException) {
            // reset cached pin
            if (isMasterCard) pinString = null else backupPinString = null
            // return code
            val lastDigit = e.retryAttempts
            val nfcCode = NfcResultCode.WRONG_PIN
            nfcCode.triesLeft = lastDigit
            resultCodeLive.postValue(nfcCode)
            SatoLog.d(TAG, "verifyPin wrong PIN, ${lastDigit} tries remaining")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: BlockedPINException) {
            // reset cached pin
            if (isMasterCard) pinString = null else backupPinString = null
            // return code
            resultCodeLive.postValue(NfcResultCode.CARD_BLOCKED)
            SatoLog.d(TAG, "verifyPin PIN blocked")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "verifyPin exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        return false
    }


    /**
     * Checks if card being scanned is the same card used before by comparing authentikeys:
     *      - the cached authentikey
     *      - a list of potential candidates recovered from card during the cmdSet.cardInitiateSecureChannel
     *
     * The check can be performed before PIN is verified (avoid to waste one PIN try)
     * If check fails, throws an exception indicating that different card is being used.
     *
     * @param isMasterCard Flag to specify the card context: used as master card or backup
     */
    private fun checkAuthentikey(isMasterCard: Boolean = true) {
        // get a list of potential authentikeys from card (this does not require PIN!)
        val authentikeyList = cmdSet.cardInitiateSecureChannel()

        // check if cached authentikey match one of the potential candidates
        var isCardMatch = false
        if (isMasterCard){
            isCardMatch = authentikeyList.any { it.contentEquals(authentikey) }
        } else {
            isCardMatch = authentikeyList.any { it.contentEquals(backupAuthentikey) }
        }
        if (!isCardMatch) {
            throw CardMismatchException("authentikeys do not match!")
        }
    }

    /**
     * Retrieves and sets the logs from the NFC card.
     */
    private fun getCardLogs() {
        SatoLog.d(TAG, "getCardLogs start")
        try {
            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey?
            checkAuthentikey(isMasterCard = true)

            // verify PIN
            if (!verifyPin(isMasterCard = true)){return}

            // get logs
            cardLogs.clear()
            cardLogs.addAll(cmdSet.seedkeeperPrintLogs(true))

            resultCodeLive.postValue(NfcResultCode.CARD_LOGS_FETCHED_SUCCESSFULLY)
            SatoLog.d(TAG, "getCardLogs successful")
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getCardLogs exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Changes the PIN on the NFC card to a new one provided by the user.
     */
    private fun changePin() {
        SatoLog.d(TAG, "changePin start")
        try {
            cmdSet.cardSelect("seedkeeper").checkOK()
            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)
            //val oldPinBytes = oldPinString?.toByteArray(Charsets.UTF_8) // todo rename newPin?
            val newPinBytes = newPinString?.toByteArray(Charsets.UTF_8) // todo rename newPin?

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // change PIN
            //cmdSet.changeCardPin(oldPinBytes, pinBytes)
            val rapdu = cmdSet.cardChangePin(pinBytes, newPinBytes)
            // update pin
            pinString = newPinString

            // verify PIN required after change pin?
            if (!verifyPin(isMasterCard = true)){return}

            resultCodeLive.postValue(NfcResultCode.PIN_CHANGED)
            SatoLog.d(TAG, "changePin successful")

        } catch (e: WrongPINException) {
            pinString = null
            newPinString = null
            val lastDigit = e.retryAttempts
            val nfcCode = NfcResultCode.WRONG_PIN
            nfcCode.triesLeft = lastDigit
            resultCodeLive.postValue(nfcCode)
            SatoLog.d(TAG, "changePin wrong PIN, ${lastDigit} tries remaining")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: BlockedPINException) {
            pinString = null
            newPinString = null
            resultCodeLive.postValue(NfcResultCode.CARD_BLOCKED)
            SatoLog.d(TAG, "changePin PIN blocked")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "changePin card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "changePin changePin exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }

    }

    /**
     * Creates a `SeedkeeperSecretObject` based on the provided secret data and metadata.
     * This object can then be imported on the card for storage
     *
     * @param secretBytes byte array representing the secret to be stored (e.g., mnemonic or password).
     * @param data SecretData object containing additional data like the secret type and label.
     *
     * @return SeedkeeperSecretObject
     */
    private fun createSecretObject(
        secretBytes: ByteArray,
        data: SecretData
    ): SeedkeeperSecretObject {
        val secretFingerprintBytes = SeedkeeperSecretHeader.getFingerprintBytes(secretBytes)
        val subType =
            if (data.type == SeedkeeperSecretType.MASTERSEED) 0x01.toByte() else 0x00.toByte()
        val secretHeader = SeedkeeperSecretHeader(
            0,
            data.type,
            subType,
            SeedkeeperSecretOrigin.PLAIN_IMPORT,
            SeedkeeperExportRights.EXPORT_PLAINTEXT_ALLOWED,
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            secretFingerprintBytes,
            data.label
        )
        return SeedkeeperSecretObject(
            secretBytes,
            secretHeader,
            false,
            null
        )
    }

    /**
     * Imports a secret into the NFC card and updates the application's state.
     * In this context, the card is used as Master card.
     *
     * @param data The `SecretData` object containing the secret data to be imported.
     */
    private fun importSecret(data: SecretData) {
        try {
            SatoLog.d(TAG, "importSecret start")

            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // verify PIN or stop
            if (!verifyPin(isMasterCard = true)){return}

            // create secret object for import
            val secretBytes = data.getSecretBytes() // todo integrate into createSecretObject
            val secretObject = createSecretObject(secretBytes, data)
            val newSecretHeader = cmdSet.seedkeeperImportSecret(secretObject)
            val newSecretHeaders =(secretHeaders.value ?: emptyList()).plus(newSecretHeader)
            secretHeaders.postValue(newSecretHeaders)
            resultCodeLive.postValue(NfcResultCode.SECRET_IMPORTED_SUCCESSFULLY)
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: APDUException) {
            val sw = e.sw
            if (sw == 0x9C01){
                resultCodeLive.postValue(NfcResultCode.NO_MEMORY_LEFT)
                SatoLog.e(TAG, "No memory available for import: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            } else if (sw == 0x9C32){
                resultCodeLive.postValue(NfcResultCode.SECRET_TOO_LONG)
                SatoLog.e(TAG, "Secret too long for import: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            }
            else {
                resultCodeLive.postValue(NfcResultCode.CARD_ERROR)
                SatoLog.e(TAG, "importSecret exception: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "importSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Retrieves a secret from the NFC card based on the provided secret ID (sid).
     *
     * @param sid The secret ID of the secret to be retrieved from the NFC card.
     * @return The `SeedkeeperSecretObject` containing the secret, or `null` if the operation fails.
     */
    private fun exportSecret(sid: Int): SeedkeeperSecretObject? {
        try {
            SatoLog.d(TAG, "exportSecret start")

            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // verify PIN
            if (!verifyPin(isMasterCard = true)){return null}

            // export secret in clear
            val exportedSecret = cmdSet.seedkeeperExportSecret(sid, null)

            resultCodeLive.postValue(NfcResultCode.SECRET_EXPORTED_SUCCESSFULLY)
            return exportedSecret
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    /**
     * Deletes a secret from the NFC card and updates the application state accordingly.
     * This is only supported by v2 Seedkeeper cards and higher.
     *
     * @param sid The secret ID of the secret to be deleted from the NFC card.
     */
    private fun deleteSecret(sid: Int) {
        try {
            SatoLog.d(TAG, "deleteSecret start")

            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // todo check card version? (already done in MySecretView)

            // verify PIN
            if (!verifyPin(isMasterCard = true)){return}

            cmdSet.seedkeeperResetSecret(sid)

            // update list
            var newSecretHeaders =(secretHeaders.value ?: emptyList()).toMutableList()
            newSecretHeaders.removeAll{ it.sid == sid }
            secretHeaders.postValue(newSecretHeaders)

            // update object
            currentSecretObject.postValue(null)
            currentSecretHeader.postValue(null)
            resultCodeLive.postValue(NfcResultCode.SECRET_DELETED)
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "deleteSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Updates the label of the NFC card with the provided value.
     *
     * @param cardLabel The new label to be set on the NFC card.
     */
    private fun editCardLabel(cardLabel: String) {
        try {
            SatoLog.d(TAG, "editCardLabel start")

            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // verify PIN
            if (!verifyPin(isMasterCard = true)){return}

            // Change label
            cmdSet.setCardLabel(cardLabel)
            resultCodeLive.postValue(NfcResultCode.CARD_LABEL_CHANGED_SUCCESSFULLY)
            SatoLog.e(TAG, "editCardLabel label set to: $cardLabel")
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "editCardLabel exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }


    /**
     * Backup flow:
     * Imports new secrets into the backup NFC card and updates the backup status.
     *
     * The list of secrets to import is generated during the initial backup card scan.
     * Secrets that are already present in backup or authentikeys are filtered out.
     * For each secret to import from the list:
     *    - Sets the secret as encrypted and updates its encryption parameters with the master card authentikey secret sid.
     *    - Imports the encrypted secret into the NFC card.
     */
    private fun importSecretsToBackup() {
        try {
            SatoLog.d(TAG, "importSecretsToBackup start")

            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = false)

            // verify PIN
            if (!verifyPin(isMasterCard = false)){return}

            //  check if master authentikey is already stored in backup card, else import  it
            val masterAuthentikeySecretHeader = authentikey?.let { authentikey ->
                getImportedAuthentikey(authentikey, isMasterCard = false) ?: run {
                    importAuthentikey(authentikey)
                }
            }

            // clear import logs
            backupNumberOfSecretsImported = 0
            backupErrors.clear()

            // import encrypted secrets into backup card
            masterAuthentikeySecretHeader?.sid?.let { masterSid ->
                for ((index, item) in secretObjectsForBackup.withIndex()) {

                    SatoLog.d(TAG, "importSecretsToBackup backupImportProgress: ${backupImportProgress.value}")
                    item.isEncrypted = true
                    item.secretEncryptedParams.sidPubkey = masterSid
                    try {
                        cmdSet.seedkeeperImportSecret(item)
                        backupImportProgress.postValue(index.toFloat() / secretObjectsForBackup.size)
                        backupNumberOfSecretsImported +=1
                        SatoLog.d(TAG, "importSecretsToBackup imported secret with label ${item.secretHeader.label} and sid ${item.secretHeader.sid}")
                    } catch (e: APDUException) {
                        SatoLog.d(TAG, "importSecretsToBackup failed to import secret with label ${item.secretHeader.label} and sid ${item.secretHeader.sid}")
                        var nfcResultCode = NfcResultCode.NONE
                        when (e.sw) {
                            0x9C01 -> {
                                nfcResultCode = NfcResultCode.NO_MEMORY_LEFT
                                SatoLog.e(TAG, "No memory available for import: $e")
                                SatoLog.e(TAG, Log.getStackTraceString(e))
                            }
                            0x9C32 -> {
                                nfcResultCode = NfcResultCode.SECRET_TOO_LONG
                                SatoLog.e(TAG, "Secret too long for import: $e")
                                SatoLog.e(TAG, Log.getStackTraceString(e))
                            }
                            else -> {
                                nfcResultCode = NfcResultCode.CARD_ERROR
                                SatoLog.e(TAG, "importSecret exception: $e")
                                SatoLog.e(TAG, Log.getStackTraceString(e))
                            }
                        }
                        // log error
                        val backupError = BackupErrorData(
                            sid = item.secretHeader.sid,
                            label = item.secretHeader.label,
                            type = item.secretHeader.type,
                            subtype = item.secretHeader.subtype,
                            nfcResultCode = nfcResultCode,
                        )
                        backupErrors.add(backupError)
                    }
                }
                secretObjectsForBackup.clear()
            }
            resultCodeLive.postValue(NfcResultCode.CARD_SUCCESSFULLY_BACKED_UP)
            SatoLog.d(TAG, "importSecretsToBackup finished successfully!")
        } catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "importSecretsToBackup exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }  catch (e: APDUException) {
            val sw = e.sw
            if (sw == 0x9C01){
                resultCodeLive.postValue(NfcResultCode.NO_MEMORY_LEFT)
                SatoLog.e(TAG, "No memory available for import: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            } else if (sw == 0x9C32){
                resultCodeLive.postValue(NfcResultCode.SECRET_TOO_LONG)
                SatoLog.e(TAG, "Secret too long for import: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            }
            else {
                resultCodeLive.postValue(NfcResultCode.CARD_ERROR)
                SatoLog.e(TAG, "importSecret exception: $e")
                SatoLog.e(TAG, Log.getStackTraceString(e))
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "importSecretsToBackup exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }

    }

    /**
     * Backup flow sub-function:
     * Retrieves the SeedkeeperSecretHeader for a previously imported authentikey.
     *
     * In order to export or import an encrypted secret during backup, the authentikey of the other card must be stored into the card.
     *
     * @param authentikeyBytes The byte array containing the authentikey bytes to be matched against list of secret headers.
     * @param isMasterCard A flag that specify the context of the card scanned: master card or backup card.
     *
     * @return The SeedkeeperSecretHeader corresponding to the matched authentikeyFingerprintBytes, or `null` if no match is found.
     */
    private fun getImportedAuthentikey(authentikeyBytes: ByteArray, isMasterCard: Boolean = true): SeedkeeperSecretHeader? {

        SatoLog.d(TAG, "getImportedAuthentikey Start")

        // compute fingerprint for authentikey
        val authentikeySecretBytes = ByteArray(authentikeyBytes.size + 1)
        authentikeySecretBytes[0] = authentikeyBytes.size.toByte()
        System.arraycopy(authentikeyBytes, 0, authentikeySecretBytes, 1, authentikeyBytes.size)
        val authentikeyFingerprintBytes =
            SeedkeeperSecretHeader.getFingerprintBytes(authentikeySecretBytes)

        // look for authentikey in secretHeaders
        var authentikeyHeader: SeedkeeperSecretHeader? = null
        if (isMasterCard) {
            authentikeyHeader = secretHeaders.value?.find {
                it.fingerprintBytes.contentEquals(authentikeyFingerprintBytes)
            }
            authentikeyHeader?.let {
                SatoLog.d(TAG, "getImportedAuthentikey found backup authentikey in master card")
            } ?: run {
                SatoLog.d(TAG, "getImportedAuthentikey found no backup authentikey in master card")
            }

        } else {
            authentikeyHeader = backupSecretHeaders.find {
                it.fingerprintBytes.contentEquals(authentikeyFingerprintBytes)
            }
            authentikeyHeader?.let {
                SatoLog.d(TAG, "getImportedAuthentikey found master authentikey in backup card")
            } ?: run {
                SatoLog.d(TAG, "getImportedAuthentikey found no master authentikey in backup card")
            }
        }
        return authentikeyHeader
    }

    /**
     * Backup flow sub-function:
     * Imports an authentikey as a secret object into the NFC card.
     *
     * In order to export or import an encrypted secret during backup, the authentikey of the other card must be stored into the card.
     *
     * @param authentikeyBytes The byte array containing the authentikey bytes to be imported.
     *
     * @return The SeedkeeperSecretHeader for the imported authentikey, or `null` if an error occurs.
     */
    private fun importAuthentikey(authentikeyBytes: ByteArray): SeedkeeperSecretHeader? {

        SatoLog.d(TAG, "importAuthentikey start")
        try {
            // create secret bytes
            val authentikeySecretBytes = ByteArray(authentikeyBytes.size + 1)
            authentikeySecretBytes[0] = authentikeyBytes.size.toByte()
            System.arraycopy(authentikeyBytes, 0, authentikeySecretBytes, 1, authentikeyBytes.size)
            // compute fingerprint for secret
            val authentikeyFingerprintBytes =
                SeedkeeperSecretHeader.getFingerprintBytes(authentikeySecretBytes)
            // create label, header & secret object
            val authentikeyLabel = "Authentikey #${bytesToHex(authentikeyFingerprintBytes)}"
            val authentikeySecretHeader = SeedkeeperSecretHeader(
                0,
                SeedkeeperSecretType.PUBKEY,
                0x00.toByte(),
                SeedkeeperSecretOrigin.PLAIN_IMPORT,
                SeedkeeperExportRights.EXPORT_PLAINTEXT_ALLOWED,
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                authentikeyFingerprintBytes,
                authentikeyLabel
            )
            val authentikeySecretObject = SeedkeeperSecretObject(
                authentikeySecretBytes,
                authentikeySecretHeader,
                false,
                null
            )
            // Import secret
            val seedkeeperSecretHeader = cmdSet.seedkeeperImportSecret(authentikeySecretObject)
            SatoLog.d(TAG, "importAuthentikey import successful!")
            return seedkeeperSecretHeader
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "importAuthentikey exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    /**
     * Backup flow:
     * Export a list of encrypted secret objects from the master card.
     * This list of secret will be imported in the backup card in a subsequent step.
     */
    private fun exportSecretsFromMaster(){
        try {
            SatoLog.d(TAG, "exportSecretsFromMaster start")
            cmdSet.cardSelect("seedkeeper").checkOK()

            // check authentikey
            checkAuthentikey(isMasterCard = true)

            // verify PIN
            if (!verifyPin(isMasterCard = true)){return}

            //  check if backup authentikey is already stored in master card, else import  it
            val backupAuthentikeySecretHeader = backupAuthentikey?.let { backupAuthentikey ->
                getImportedAuthentikey(backupAuthentikey, isMasterCard = true) ?: run {
                    importAuthentikey(backupAuthentikey)
                }
            }

            // export secrets
            exportSecrets(backupAuthentikeySecretHeader?.sid)

            // return success code
            resultCodeLive.postValue(NfcResultCode.SECRETS_EXPORTED_SUCCESSFULLY_FROM_MASTER)
            SatoLog.d(TAG, "exportSecretsFromMaster finished successfully!")
        }catch (e: CardMismatchException) {
            resultCodeLive.postValue(NfcResultCode.CARD_MISMATCH)
            SatoLog.e(TAG, "exportSecretsFromMaster card mismatch exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "exportSecretsFromMaster exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }// TODO catch PIN exceptions,
    }

    /**
     * Backup flow sub-function:
     * Retrieves and stores secret objects from the NFC card based on the current secrets list.
     * The list of secret headers for export is generated during initial backup scan.
     *
     * @param pubSid An optional secret id from authentikey secret for encrypted export. If pubSid equals null secrets are exported without encryption.
     */
    private fun exportSecrets(pubSid: Int? = null) {
        try {
            SatoLog.d(TAG, "exportSecrets start")
            secretObjectsForBackup.clear()
            for ((index, item) in secretHeadersForBackup.withIndex()) {
                SatoLog.d(TAG, "exportSecrets backupExportProgress: ${backupExportProgress.value}")
                val secretObject = cmdSet.seedkeeperExportSecret(item.sid, pubSid)
                secretObjectsForBackup.add(secretObject)
                backupExportProgress.postValue(index.toFloat() / secretHeadersForBackup.size)
                SatoLog.d(TAG, "exportSecrets exported encrypted secret with label: ${secretObject.secretHeader.label} and sid: ${secretObject.secretHeader.sid}")
            }
            SatoLog.d(TAG, "exportSecrets exported successfully!")
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "exportSecrets exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Reset card to factory.
     * WARNING: all data will be erased!
     * This function supports v1 and v2 factory reset!
     */
    private fun requestFactoryReset() {
        try {
            SatoLog.d(TAG, "requestFactoryReset start")
            isCardDataAvailable.postValue(false)
            val selectApdu = cmdSet.cardSelect("seedkeeper").checkOK()
            if (selectApdu.data.size >= 4){
                // in Seedkeeper v2, status data is already provided during cardSelect()
                cardStatus = ApplicationStatus(selectApdu)
            }

            if (cardStatus == null){
                // that's for v1 only, sice v2 and higheer get the status in select response
                val statusApdu = cmdSet.cardGetStatus()
                cardStatus = ApplicationStatus(statusApdu)
            }
            val cardStatus = cardStatus ?: return

            if (!cardStatus.isSetupDone) {
                resultCodeLive.postValue(NfcResultCode.REQUIRE_SETUP)
                return
            }

            if (cardStatus.protocolVersion >= 2) {
                SatoLog.d(TAG, "requestFactoryReset block pin")
                val allowedChars = ('0'..'9')
                val randomString = (1..6).map { allowedChars.random() }.joinToString("")
                val pinBytes = randomString.toByteArray(Charsets.UTF_8)
                try {
                    cmdSet.setPin0(pinBytes)
                    val rapduReset = cmdSet.cardVerifyPIN() // should throw!
                    SatoLog.e(TAG, "requestFactoryReset unexpected response to reset command: ${rapduReset.sw}")
                    resultCodeLive.postValue(NfcResultCode.CARD_RESET_SENT)
                } catch (e: WrongPINException){
                    val triesLeft = e.retryAttempts
                    if (triesLeft>0){
                        val nfcCode = NfcResultCode.CARD_RESET_SENT
                        nfcCode.triesLeft = triesLeft
                        resultCodeLive.postValue(nfcCode)
                        return
                    } else {
                        // PIN blocked, now block puk
                        while (true) {
                            try {
                                cmdSet.cardUnblockPin(pinBytes)
                            } catch (e: WrongPINException) {
                            } catch (e: ResetToFactoryException) {
                                resultCodeLive.postValue(NfcResultCode.CARD_RESET)
                                return
                            }
                        }
                    }
                } catch (e: BlockedPINException) {
                    // PIN blocked, now block puk
                    while (true) {
                        try {
                            cmdSet.cardUnblockPin(pinBytes)
                        } catch (e: WrongPINException) {
                        } catch (e: ResetToFactoryException) {
                            resultCodeLive.postValue(NfcResultCode.CARD_RESET)
                            return
                        }
                    }
                }
            } else {
                // factory reset V1
                val rapduReset = cmdSet.cardSendResetCommand()
                if (rapduReset.sw == 0xFF00){
                    resultCodeLive.postValue(NfcResultCode.CARD_RESET)
                } else if (rapduReset.sw == 0xFFFF){
                    resultCodeLive.postValue(NfcResultCode.CARD_RESET_CANCELLED)
                } else if ((rapduReset.sw and 0xFF00) == 0xFF00) {
                    val lastDigit = (rapduReset.sw and 0x00FF)
                    val nfcCode = NfcResultCode.CARD_RESET_SENT
                    nfcCode.triesLeft = lastDigit
                    resultCodeLive.postValue(nfcCode)
                } else {
                    SatoLog.e(TAG, "requestFactoryReset-v1 unexpected response to reset command: ${rapduReset.sw}")
                    resultCodeLive.postValue(NfcResultCode.CARD_RESET_SENT)
                }
                return
            }

        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "requestFactoryReset exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }
}