package org.satochip.seedkeeper.services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
import org.satochip.io.APDUResponse
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode

private const val TAG = "NFCCardService"

@SuppressLint("StaticFieldLeak")
object NFCCardService {
    lateinit var context: Context
    private var activity: Activity? = null

    private lateinit var cmdSet: SatochipCommandSet
    private var parser: SatochipParser? = null

    //CARD STATE
    var isSetupNeeded = MutableLiveData(false)
    var cardLabel = MutableLiveData("")
    var isReadyForPinCode = MutableLiveData(false)
    var isCardDataAvailable = MutableLiveData(false)
    var secretHeaders = MutableLiveData<List<SeedkeeperSecretHeader>>()
    var currentSecretObject = MutableLiveData<SeedkeeperSecretObject?>()
    var currentSecretId = MutableLiveData<Int?>()
    var pinString: String? = null
    var oldPinString: String? = null
    var seedkeeperStatus: SeedkeeperStatus? = null
    var cardLogs: MutableList<SeedkeeperLog> = mutableListOf()
    var authenticityStatus = MutableLiveData(AuthenticityStatus.UNKNOWN)
    var certificateList: MutableList<String> = mutableListOf()
    var cardAppletVersion: String = "undefined"
    private lateinit var cardStatus: ApplicationStatus
    private var authentikey: ByteArray?  = null

    //BACKUP
    private var backupPinString: String? = null
    private var secretsList: MutableList<SeedkeeperSecretHeader> = mutableListOf()
    private var masterSecretObjects: MutableList<SeedkeeperSecretObject> = mutableListOf()
    var backupStatus = MutableLiveData(BackupStatus.DEFAULT)

    //GENERATE SECRET
    var passwordData: GeneratePasswordData? = null

    // NFC
    var resultCodeLive = MutableLiveData(NfcResultCode.BUSY)
    var actionType: NfcActionType = NfcActionType.DO_NOTHING
    val isConnected =
        MutableLiveData(false) // the app is connected to a card, value updated in SeedkeeperCardListener

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
                readCard()
            }
            NfcActionType.VERIFY_PIN -> {
                verifyPin()
            }
            NfcActionType.CHANGE_PIN -> {
                changePin()
            }
            NfcActionType.GET_SECRETS_LIST -> {
                getSecretHeaderList()
            }
            NfcActionType.GENERATE_A_SECRET -> {
                passwordData?.let { data ->
                    importSecret(data = data)
                }
            }
            NfcActionType.SETUP_CARD -> {
                cardSetup()
            }
            NfcActionType.GET_SECRET -> {
                currentSecretId.value?.let { sid ->
                    currentSecretObject.postValue(getSecret(sid))
                }
            }
            NfcActionType.DELETE_SECRET -> {
                currentSecretId.value?.let { sid ->
                    deleteSecret(sid)
                }
            }
            NfcActionType.EDIT_CARD_LABEL -> {
                cardLabel.value?.let { cardLabel ->
                    editCardLabel(cardLabel)
                }
            }
            NfcActionType.SCAN_BACKUP_CARD -> {
                backupProcessCardScan(
                    isBackupCard = true
                )
            }
            NfcActionType.SCAN_MASTER_CARD -> {
                backupProcessCardScan(
                    isBackupCard = false
                )
            }
            NfcActionType.TRANSFER_TO_BACKUP -> {
                backupCardImportNewSecrets()
            }
        }
    }

    /**
     * Initiates scanning for NFC cards and sets up the necessary NFC reader mode for the specified activity.
     *
     * This method performs the following steps:
     * 1. Logs the start of the card scanning process.
     * 2. Sets the provided activity to the class property this.activity.
     * 3. Creates an instance of NFCCardManager to manage NFC card interactions.
     * 4. Configures the NFCCardManager to use SatochipCardListenerForAction as the card listener.
     * 5. Starts the NFCCardManager.
     * 6. Posts a `BUSY` status to `resultCodeLive` to indicate that the NFC scanning process is ongoing.
     * 7. Retrieves the default `NfcAdapter` for the given activity.
     * 8. Enables the NFC reader mode on the `NfcAdapter` with the following flags:
     *    - `FLAG_READER_NFC_A` and `FLAG_READER_NFC_B`.
     *    - `FLAG_READER_SKIP_NDEF_CHECK` to skip the NDEF tag check and process all NFC tags.
     * 9. Logs the end of the method execution for debugging purposes.
     *
     * @param activity The activity where NFC scanning should be enabled.
     */
    fun scanCardForAction(activity: Activity) {
        SatoLog.d(TAG, "scanCardForAction thread START")
        this.activity = activity
        val cardManager = NFCCardManager()
        cardManager.setCardListener(SatochipCardListenerForAction)
        cardManager.start()

        resultCodeLive.postValue(NfcResultCode.BUSY)

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
     *
     * This method performs the following steps:
     * 1. Logs the start of the disabling card reading process.
     * 2. Checks if the `activity` is not null.
     * 3. Checks if the `activity` is finishing. If it is, logs an error and exits the method to avoid performing operations on a finishing activity.
     * 4. Retrieves the default `NfcAdapter` for the current activity.
     * 5. Disables the NFC reader mode using `disableReaderMode` on the `NfcAdapter` to stop NFC tag scanning.
     * 6. Logs a message indicating that NFC reader mode has been disabled.
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
     * Extracts and formats the card version information from the given APDU response.
     *
     * This method performs the following steps:
     * 1. Retrieves the version data from the `rapdu` (APDUResponse) object.
     * 2. Extracts the major and minor version numbers for both the protocol and the applet:
     *    - `protocolMajorVersion`: The major version number of the protocol.
     *    - `protocolMinorVersion`: The minor version number of the protocol.
     *    - `appletMajorVersion`: The major version number of the applet.
     *    - `appletMinorVersion`: The minor version number of the applet.
     * 3. Constructs a version string in the format "protocolMajorVersion.protocolMinorVersion-appletMajorVersion.appletMinorVersion".
     * 4. Updates the `cardAppletVersion` field with a formatted version string prefixed by "Seedkeeper v".
     *
     * @param rapdu The APDU response object containing the version information.
     */
    private fun getCardVersionString(
        rapdu: APDUResponse
    ) {
        val data = rapdu.data
        val protocolMajorVersion = data[0]
        val protocolMinorVersion = data[1]
        val appletMajorVersion = data[2]
        val appletMinorVersion = data[3]
        val versionString =
            "$protocolMajorVersion.$protocolMinorVersion-$appletMajorVersion.$appletMinorVersion"
        cardAppletVersion = "Seedkeeper v${versionString}"
    }

    /**
     * Reads and processes the data from the NFC card to determine its setup status and version.
     *
     * This method performs the following steps:
     * 1. Logs the start of the card reading process.
     * 2. Sets isCardDataAvailable to false indicating that card data is not available at the moment.
     * 3. Selects the "seedkeeper" card and checks for successful selection.
     * 4. Updates cardStatus using cardGetStatus() method.
     * 5. Calls getCardVersionString(rapduStatus) method to process and obtain the card version string from the status.
     * 6. Sets seedkeeperStatus to null.
     * 7. Logs the card status and whether the setup is completed.
     * 8. Checks if the card setup is completed:
     *    - If the setup is not done:
     *      - Logs the card version and indicates that setup is required.
     *      - Sets `isSetupNeeded` to `true`.
     *      - Posts `NfcResultCode.REQUIRE_SETUP` to `resultCodeLive`.
     *      - Exits the method.
     *    - If the setup is done:
     *      - Sets `isReadyForPinCode` to `true` indicating that the PIN code is required.
     *      - Posts `NfcResultCode.OK` to `resultCodeLive` indicating successful card reading.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun readCard() {
        try {
            SatoLog.d(TAG, "readCard Start")
            isCardDataAvailable.postValue(false)
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()
            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)
            getCardVersionString(rapduStatus)
            seedkeeperStatus = null
            SatoLog.d(TAG, "card status: $cardStatus")
            SatoLog.d(TAG, "is setup done: ${cardStatus.isSetupDone}")

            if (!cardStatus.isSetupDone) {
                SatoLog.d(TAG, "CardVersionInt: ${cardStatus.getCardVersionInt()}, setup not done")
                isSetupNeeded.postValue(true)
                resultCodeLive.postValue(NfcResultCode.REQUIRE_SETUP)
                return
            } else {
                isReadyForPinCode.postValue(true)
            }
            resultCodeLive.postValue(NfcResultCode.OK)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "readCard exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Verifies the authenticity of the NFC card and updates the authenticity status.
     *
     * This method performs the following steps:
     * 1. Logs the start of the card authenticity verification process.
     * 2. Obtains the authentication results from the card through cardVerifyAuthenticity() method.
     * 3. If authentication results are obtained:
     *    - Checks the first result of the authentication response:
     *      - If the result is "OK", updates authenticityStatus to AuthenticityStatus.AUTHENTIC.
     *      - Otherwise, updates authenticityStatus to AuthenticityStatus.NOT_AUTHENTIC and logs an error message indicating authentication failure.
     *    - Clears the certificateList and adds new auth results to it.
     *
     * If any step fails, an error is logged and authenticityStatus is updated to AuthenticityStatus.UNKNOWN.
     */
    private fun getCardAuthenticty() {
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
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            authenticityStatus.postValue(AuthenticityStatus.UNKNOWN)
            SatoLog.e(TAG, "Failed to authenticate card with error: $e")
        }
    }

    /**
     * Sets up the NFC card with the necessary configuration and PIN verification.
     *
     * This method performs the following steps:
     * 1. Selects the "seedkeeper" application on the NFC card.
     * 2. Retrieves and updates the card's application status.
     * 3. Converts the provided PIN string into a byte array.
     * 4. Attempts to configure the card with the given parameters (e.g., retry limit, PIN).
     * 5. Verifies the PIN by setting it and performing a PIN verification check.
     * 6. Updates the setup status and result codes based on the outcome of the operations.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun cardSetup() {
        SatoLog.d(TAG, "cardSetup start")
        try {
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()

            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)

            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)

            try {
                cmdSet.cardSetup(5, pinBytes)
            } catch (error: Exception) {
                SatoLog.e(TAG, "cardSetup: Error: $error")
            }
            verifyPin()
            isSetupNeeded.postValue(false)
            resultCodeLive.postValue(NfcResultCode.CARD_SETUP_SUCCESSFUL)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.WRONG_PIN)
            SatoLog.e(TAG, "verifyPin exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        SatoLog.d(TAG, "Card setup successful")
    }

    /**
     * Verifies the PIN entered by the user against the NFC card's set PIN and updates the application's state accordingly.
     *
     * This method performs the following steps:
     * 1. Converts the provided PIN string into a byte array.
     * 2. Selects the "seedkeeper" application on the NFC card and sets the PIN for verification.
     * 3. Verifies the PIN by sending it to the card and analyzing the response status word (SW).
     *
     * Based on the response:
     * - If the PIN is incorrect and the card is not yet blocked, updates the number of attempts left and posts an appropriate result code.
     * - If the card is blocked due to too many incorrect attempts, posts a `CARD_BLOCKED`(Too many incorrect PIN attempts! Your card has been blocked.) result code.
     * - If the PIN is correct optionally retrieves card secrets, gets cards authenticity, sets new card logs and gets cards authentikey
     *
     * @param shouldUpdateDataState Flag to determine whether to update the card label.
     * @param shouldUpdateResultCodeLive Flag to determine whether to update the result code live and show nfc message.
     * @param shouldGetCardData Flag to determine whether to fetch and update card data after successful PIN verification.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun verifyPin(
        shouldUpdateDataState: Boolean = true,
        shouldUpdateResultCodeLive: Boolean = true,
        shouldGetCardData: Boolean = true
    ) {
        try {
            SatoLog.d(TAG, "verifyPin start")
            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)

            cmdSet.cardSelect("seedkeeper").checkOK()
            cmdSet.setPin0(pinBytes)
            val rapdu = cmdSet.cardVerifyPIN()

            when (rapdu.sw) {
                in 0x63C1..0x63CF -> {
                    val lastDigit = rapdu.sw and 0x000F
                    val nfcCode = NfcResultCode.WRONG_PIN
                    nfcCode.triesLeft = lastDigit
                    resultCodeLive.postValue(nfcCode)
                    SatoLog.d(TAG, "verifyPin failed")
                }
                0x9C0C, 0x63C0  -> {
                    resultCodeLive.postValue(NfcResultCode.CARD_BLOCKED)
                    SatoLog.d(TAG, "verifyPin failed, card blocked!")
                }
                else -> {
                    // TODO: we should perform these operations outside of verifyPIN()!!
                    if (shouldGetCardData) {
                        runBlocking {
                            getSecretHeaderList(shouldUpdateResultCodeLive)
                            getCardAuthenticty()
                            getCardLogs()
                            if (cardStatus.protocolVersion == 2) {
                                seedkeeperStatus = cmdSet.seedkeeperGetStatus()
                            }
                        }
                    }
                    isReadyForPinCode.postValue(false)
                    if (shouldUpdateDataState) {
                        cardLabel.postValue(cmdSet.cardLabel)
                        isCardDataAvailable.postValue(true)
                    }
                    if (shouldUpdateResultCodeLive) {
                        resultCodeLive.postValue(NfcResultCode.PIN_VERIFIED)
                    }
                    authentikey = cmdSet.cardGetAuthentikey()
                    SatoLog.d(TAG, "verifyPin successful")
                }
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "verifyPin exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Retrieves and sets the logs from the NFC card.
     *
     * This method performs the following steps:
     * 1. Clears any existing logs stored in the `cardLogs` list.
     * 2. Fetches the latest logs from the card using the `seedkeeperPrintLogs` method.
     * 3. Adds the fetched logs to the `cardLogs` list.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun getCardLogs() {
        try {
            SatoLog.d(TAG, "getCardLogs start")
            cardLogs.clear()
            cardLogs.addAll(cmdSet.seedkeeperPrintLogs(true))
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getCardLogs exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Changes the PIN on the NFC card to a new one provided by the user.
     *
     * This method performs the following steps:
     * 1. Selects the "seedkeeper" application on the NFC card.
     * 2. Retrieves the card's current status and updates the `cardStatus` variable.
     * 3. Converts both the old and new PIN strings into byte arrays.
     * 4. Changes pin using changeCardPin method
     * 5. Verifies the new PIN to ensure the change was successful.
     * 6. Updates the result code to indicate the PIN change was successful.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun changePin() {
        try {
            SatoLog.d(TAG, "changePin start")
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()
            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)
            val oldPinBytes = oldPinString?.toByteArray(Charsets.UTF_8)
            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)
            cmdSet.changeCardPin(oldPinBytes, pinBytes)
            verifyPin(shouldUpdateResultCodeLive = false)
            resultCodeLive.postValue(NfcResultCode.PIN_CHANGED)
            SatoLog.d(TAG, "changePin successful")
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "changePin exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Retrieves and sets the list of secret headers from the NFC card.
     *
     * This method performs the following steps:
     * 1. Clears the existing `secretsList`.
     * 2. Fetches the list of secret headers from the card using the `seedkeeperListSecretHeaders` method.
     * 3. Adds the fetched secret headers to the `secretsList`.
     * 4. Posts the updated `secretsList` to the `secretHeaders` LiveData.
     *
     * @param shouldUpdateResultCodeLive Flag to determine whether to update the result code live data.
     *
     * If any step fails, an empty list is posted to `secretHeaders`, an error is logged, and the appropriate result code is posted.
     */
    private fun getSecretHeaderList(
        shouldUpdateResultCodeLive: Boolean = true
    ) {
        try {
            SatoLog.d(TAG, "getSecretHeaderList start")
            secretsList.clear()
            secretsList.addAll(cmdSet.seedkeeperListSecretHeaders())
            secretHeaders.postValue(secretsList)
            if (shouldUpdateResultCodeLive) {
                resultCodeLive.postValue(NfcResultCode.SECRET_HEADER_LIST_SET)
            }
            SatoLog.d(TAG, "getSecretHeaderList successful")
        } catch (e: Exception) {
            secretHeaders.postValue(emptyList())
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getSecretHeaderList exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Creates a `SeedkeeperSecretObject` based on the provided secret data and metadata.
     *
     * This method performs the following steps:
     * 1. Determines the subtype of the secret based on the `type` of the provided `GeneratePasswordData`.
     *    - If the type is `BIP39_MNEMONIC`, the subtype is set to `0x00`.
     *    - Otherwise, the subtype is set to `0x01` (password or masterseed).
     * 2. Constructs a `SeedkeeperSecretHeader` object with the provided data.
     *    - The header includes the secret type, subtype, origin, export rights, and the secret's fingerprint and label.
     * 3. Returns a new `SeedkeeperSecretObject` that encapsulates the secret bytes, header, and other optional parameters.
     *
     * @param secretBytes byte array representing the secret to be stored (e.g., mnemonic or password).
     * @param secretFingerprintBytes byte array representing the fingerprint of the secret for identification.
     * @param data GeneratePasswordData object containing additional data like the secret type and label.
     *
     * @return SeedkeeperSecretObject
     */
    private fun createSecretObject(
        secretBytes: ByteArray,
        data: GeneratePasswordData
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
     *
     * This method performs the following steps:
     * 1. Verifies the user's PIN without updating the data state or result code.
     * 2. Retrieves the secret bytes from the provided GeneratePasswordData object.
     * 3. Fetches the fingerprint bytes for the secret using the SeedkeeperSecretHeader method getFingerprintBytes.
     * 4. Creates a `SeedkeeperSecretObject` with the secret bytes, fingerprint, and metadata.
     * 5. Imports the secret into the NFC card using the `seedkeeperImportSecret` command.
     * 6. Adds the new secret header to the `secretsList`.
     * 7. Updates the `resultCodeLive` to indicate that the secret was successfully imported.
     *
     * @param data The `GeneratePasswordData` object containing the secret data to be imported.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun importSecret(
        data: GeneratePasswordData
    ) {
        try {
            SatoLog.d(TAG, "importSecret start")
            verifyPin(
                shouldGetCardData = false,
                shouldUpdateDataState = false,
                shouldUpdateResultCodeLive = false,
            )
            val secretBytes = data.getSecretBytes()
            val secretObject = createSecretObject(secretBytes, data)
            val newSecretHeader = cmdSet.seedkeeperImportSecret(secretObject)
            secretsList.add(0, newSecretHeader)
            secretHeaders.postValue(secretsList)
            resultCodeLive.postValue(NfcResultCode.SECRET_IMPORTED_SUCCESSFULLY)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "importSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Retrieves a secret from the NFC card based on the provided secret ID (sid).
     *
     * This method performs the following steps:
     * 1. Verifies the user's PIN without updating the data state, result code or getting card data.
     * 2. Attempts to export the secret from the NFC card using the provided secret ID (`sid`).
     * 3. If successful, updates the `resultCodeLive` to indicate the secret was successfully fetched.
     * 4. Returns the `SeedkeeperSecretObject` containing the secret data.
     *
     * @param sid The secret ID of the secret to be retrieved from the NFC card.
     * @return The `SeedkeeperSecretObject` containing the secret, or `null` if the operation fails.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted and null is returned to indicate the operation failed.
     */
    private fun getSecret(
        sid: Int
    ): SeedkeeperSecretObject? {
        try {
            SatoLog.d(TAG, "getSecret start")
            verifyPin(
                shouldUpdateDataState = false,
                shouldUpdateResultCodeLive = false,
                shouldGetCardData = false
            )
            val exportedSecret = cmdSet.seedkeeperExportSecret(sid, null)
            resultCodeLive.postValue(NfcResultCode.SECRET_FETCHED_SUCCESSFULLY)
            return exportedSecret
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    /**
     * Deletes a secret from the NFC card and updates the application's state accordingly.
     *
     * This method performs the following steps:
     * 1. Verifies the user's PIN without updating the data state, result code or getting card data.
     * 2. Resets (deletes) the secret from the NFC card using the provided secret ID (`sid`).
     * 3. Removes the corresponding secret header from the secretsList.
     * 4. Updates the secretHeaders LiveData to reflect the removal of the secret.
     * 5. Clears the currentSecretObject and currentSecretId LiveData to indicate no secret is currently selected.
     * 6. Updates the resultCodeLive to indicate that the secret was successfully deleted.
     *
     * @param sid The secret ID of the secret to be deleted from the NFC card.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun deleteSecret(
        sid: Int
    ) {
        try {
            SatoLog.d(TAG, "deleteSecret start")
            verifyPin(
                shouldUpdateDataState = false,
                shouldUpdateResultCodeLive = false,
                shouldGetCardData = false
            )
            cmdSet.seedkeeperResetSecret(sid)
            secretsList.removeAll { it.sid == sid }
            secretHeaders.postValue(secretsList)
            currentSecretObject.postValue(null)
            currentSecretId.postValue(null)
            resultCodeLive.postValue(NfcResultCode.SECRET_DELETED)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "deleteSecret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Updates the label of the NFC card with the provided value.
     *
     * This method performs the following steps:
     * 1. Verifies the user's PIN without updating the data state, result code or getting card data.
     * 2. Sets the new label on the NFC card using the setCardLabel method with the provided cardLabel.
     * 3. Updates the resultCodeLive to indicate that the card label was successfully changed.
     *
     * @param cardLabel The new label to be set on the NFC card.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun editCardLabel(
        cardLabel: String,
    ) {
        try {
            SatoLog.d(TAG, "editCardLabel start")
            verifyPin(
                shouldUpdateDataState = false,
                shouldUpdateResultCodeLive = false,
                shouldGetCardData = false
            )
            cmdSet.setCardLabel(cardLabel)
            resultCodeLive.postValue(NfcResultCode.CARD_LABEL_CHANGED_SUCCESSFULLY)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "editCardLabel exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun getXpub(path: String, xtype: Long = 0x0488b21e) {
        // default xtype used: standard
        try {
            SatoLog.d(TAG, "getXpub start")
            currentSecretId.value?.let { sid ->
                val xpubString = cmdSet.cardBip32GetXpub(path, xtype, sid)
            }
            resultCodeLive.postValue(NfcResultCode.OK)
        } catch (e: Exception) {
            SatoLog.e(TAG, "getXpub exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Imports new secrets into the backup NFC card and updates the backup status.
     *
     * This method performs the following steps:
     * 1. Logs the start of the backup process.
     * 2. Swaps the current PIN with the old PIN for backup purposes, and updates `backupPinString` accordingly.
     * 3. Stores the current authentication key (`masterAuthentikey`) and verifies the PIN without updating the result code or data state.
     * 4. Filters out any new secrets that are already present in the backup card based on their fingerprint.
     * 5. Imports the master authentication key.
     * 6. Updates the `secretHeaders` with the headers from the `masterCardObjects`.
     * 7. For each unique new secret:
     *    - Sets the secret as encrypted and updates its encryption parameters with the master card's authentikey secret sid.
     *    - Imports the encrypted secret into the NFC card.
     * 8. Clears the masterSecretObjects list after successful import.
     * 9. Updates resultCodeLive to indicate a successful backup and sets the backupStatus to the fifth step.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun backupCardImportNewSecrets() {
        try {
            SatoLog.d(TAG, "backupCardImportNewSecrets start")

            // todo: improve this code?
            backupPinString = pinString
            pinString = oldPinString
            oldPinString = backupPinString

            val masterAuthentikey = authentikey
            verifyPin(
                shouldUpdateResultCodeLive = false,
                shouldUpdateDataState = false
            )
            val uniqueNewSecrets = masterSecretObjects.toList().filterNot { newSecret ->
                secretsList.any {
                    it.fingerprintBytes.contentEquals(newSecret.fingerprintFromSecret)
                }
            }
            val masterAuthentikeySecret = masterAuthentikey?.let {
                importAuthentikey(it)
            }
            secretHeaders.postValue(masterSecretObjects.toList().map { it.secretHeader })
            masterAuthentikeySecret?.sid?.let { masterSid ->
                for (item in uniqueNewSecrets) {
                    item.isEncrypted = true
                    item.secretEncryptedParams.sidPubkey = masterSid
                    cmdSet.seedkeeperImportSecret(item)
                }
                masterSecretObjects.clear()
            }
            resultCodeLive.postValue(NfcResultCode.CARD_SUCCESSFULLY_BACKED_UP)
            backupStatus.postValue(BackupStatus.FIFTH_STEP)
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "backupCardImportNewSecrets exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Imports an authentication key (authentikey) as a secret object into the NFC card.
     *
     * This method performs the following steps:
     * 1. Logs the start of the import process.
     * 2. Creates a new byte array, authentikeySecretBytes, to hold the authentikeyBytes data.
     * The first byte of this array is set to the size of the authentikeyBytes array, followed by copying the actual authentikeyBytes data.
     * 3. Computes the authentikeyFingerprintBytes using getFingerprintBytes method.
     * 4. Creates a label for the authentication key.
     * 5. Constructs a SeedkeeperSecretHeader object
     * 6. Creates a SeedkeeperSecretObject with the authentikey secret bytes and SeedkeeperSecretHeader.
     * 7. Imports the secret object into the NFC card using the seedkeeperImportSecret method.
     * 8. Returns the secret header for the imported secret object.
     *
     * @param authentikeyBytes The byte array containing the authentikey bytes to be imported.
     *
     * @return The SeedkeeperSecretHeader for the imported authentikey, or `null` if an error occurs.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun importAuthentikey(
        authentikeyBytes: ByteArray
    ): SeedkeeperSecretHeader? {
        try {
            SatoLog.d(TAG, "Start importAuthentikey")
            val authentikeySecretBytes = ByteArray(authentikeyBytes.size + 1)
            authentikeySecretBytes[0] = authentikeyBytes.size.toByte()
            System.arraycopy(
                authentikeyBytes,
                0,
                authentikeySecretBytes,
                1,
                authentikeyBytes.size
            )
            val authentikeyFingerprintBytes =
                SeedkeeperSecretHeader.getFingerprintBytes(authentikeySecretBytes)
            val authentikeyLabel = "Backup Seedkeeper authentikey"
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
            return seedkeeperSecretHeader
        }catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "importAuthentikey exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    /**
     * Handles the scanning process of NFC card based on its status and whether it's a backup card or master card.
     *
     * This method performs the following steps:
     * 1. Sets the `isCardDataAvailable` flag to `false` to indicate that card data is not currently available.
     * 2. Selects the "seedkeeper" application on the card and retrieves its status.
     * 3. Updates the `cardStatus` with the application status and logs it.
     * 4. Checks if the card is already set up:
     *    - If not set up and the card is a backup card (`isBackupCard` is `true`), it saves the current PIN as oldPinString and proceeds with the backup card setup.
     *    - If not set up and the card is a master card (`isBackupCard` is `false`), it throws an exception indicating that the card should be set up.
     * 5. If the card is set up:
     *    - For backup card, it sets the `isReadyForPinCode` flag to `true` and updates `oldPinString` with the current PIN.
     *    - For master card, it swaps the current PIN with the old PIN, verifies the PIN, and updates the backup authentication key before new verification.
     *    It then sets the backup card secrets.
     * 6. Updates the `resultCodeLive` to indicate that the card was successfully scanned.
     * 7. Updates the `backupStatus` to reflect the current step based on whether the card is a backup or master.
     *
     * @param isBackupCard Boolean indicating whether the card being processed is a backup or master card.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun backupProcessCardScan(
        isBackupCard: Boolean
    ) {
        try {
            SatoLog.d(TAG, "backupProcessCardScan start")
            isCardDataAvailable.postValue(false)
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()
            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)
            SatoLog.d(TAG, "card status: $cardStatus")
            SatoLog.d(TAG, "is setup done: ${cardStatus.isSetupDone}")

            if (!cardStatus.isSetupDone) {
                if (isBackupCard) {
                    oldPinString = pinString
                    SatoLog.d(TAG, "CardVersionInt: ${cardStatus.cardVersionInt}, setup not done")
                    backupCardSetup()
                } else {
                    throw Exception("Card should've been already setup")
                }
            } else {
                if (isBackupCard) {
                    oldPinString = pinString
                    isReadyForPinCode.postValue(true)
                } else {
                    val backupPin = pinString
                    pinString = oldPinString
                    oldPinString = backupPin
                    val backupAuthentikey = authentikey
                    verifyPin(
                        shouldUpdateDataState = false,
                        shouldUpdateResultCodeLive = false
                    )
                    val backupAuthentikeySecret = backupAuthentikey?.let {
                        importAuthentikey(it)
                    }
                    getMasterCardSecrets(backupAuthentikeySecret?.sid)
                }
            }
            resultCodeLive.postValue(NfcResultCode.CARD_SUCCESSFULLY_SCANNED)

            if (isBackupCard) {
                backupStatus.postValue(BackupStatus.SECOND_STEP)
            } else {
                backupStatus.postValue(BackupStatus.THIRD_STEP)
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "backupProcessCardScan exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Sets up the NFC card specifically for backup purposes.
     *
     * This method performs the following steps:
     * 1. Selects the "seedkeeper" application on the NFC card and retrieves its status.
     * 2. Updates the cardStatus with the application status and logs it.
     * 3. Converts the current PIN to a byte array.
     * 4. Attempts to set up the card using the cardSetup method with the master card PIN. Logs any errors encountered during this process.
     * 5. Verifies the PIN to ensure it has been set up correctly, without updating the result code.
     * 6. Logs a success message if the setup completes successfully.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun backupCardSetup() {
        try {
            SatoLog.d(TAG, "backupCardSetup start")
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()
            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)
            val pinBytes = pinString?.toByteArray(Charsets.UTF_8)

            try {
                cmdSet.cardSetup(5, pinBytes)
            } catch (error: Exception) {
                SatoLog.e(TAG, "backupCardSetup: Error: $error")
            }
            verifyPin(
                shouldUpdateResultCodeLive = false
            )
            SatoLog.d(TAG, "backupCardSetup successful")
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.WRONG_PIN)
            SatoLog.e(TAG, "backupCardSetup exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    /**
     * Retrieves and stores secret objects from the NFC card based on the current secrets list.
     *
     * This method performs the following steps:
     * 1. Clears the masterSecretObjects list to prepare for new data.
     * 2. Checks if the secretsList contains any items.
     * 3. If the secretsList is not empty, iterates over each SeedkeeperSecretHeader in the list and:
     *    - Uses the seedkeeperExportSecret method to export the secret object for each `sid` in the list.
     *    Optionally includes a pubSid(authentikey sid for encryption) if provided.
     *    - Adds the retrieved secret object to the masterSecretObjects list.
     *
     * @param pubSid An optional secret id from authentikey secret for encrypted export. If pubSid equals null secrets are exported without encryption.
     *
     * If any step fails, an error is logged, and the appropriate result code is posted.
     */
    private fun getMasterCardSecrets(pubSid: Int? = null) {
        try {
            masterSecretObjects.clear()
            if (secretsList.isNotEmpty()) {
                for (item in secretsList) {
                    val secretObject = cmdSet.seedkeeperExportSecret(item.sid, pubSid)
                    masterSecretObjects.add(secretObject)
                }
            }
        } catch (e: Exception) {
            resultCodeLive.postValue(NfcResultCode.NFC_ERROR)
            SatoLog.e(TAG, "getMasterCardSecrets exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }
}