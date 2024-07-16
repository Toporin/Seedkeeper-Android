package org.satochip.seedkeeper.services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.satochip.android.NFCCardManager
import org.satochip.client.ApplicationStatus
import org.satochip.client.SatochipCommandSet
import org.satochip.client.SatochipParser
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.client.seedkeeper.SeedkeeperSecretOrigin
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.io.APDUResponse
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode

private const val TAG = "NFCCardService"

@SuppressLint("StaticFieldLeak")
object NFCCardService {
    lateinit var context: Context
    var activity: Activity? = null

    lateinit var cmdSet: SatochipCommandSet
    private var parser: SatochipParser? = null

    //CARD STATE
    var isSetupNeeded = MutableLiveData(false)
    var isReadyForPinCode = MutableLiveData(false)
    var isCardDataAvailable = MutableLiveData(false)
    var secretHeaders = MutableLiveData<List<SeedkeeperSecretHeader>>()
    var pinString: String? = null
    var currentSecretObject = MutableLiveData<SeedkeeperSecretObject?>()
    var currentSecretId: Int? = null

    //GENERATE SECRET
    var passwordData: GeneratePasswordData? = null

    // NFC
    var resultCodeLive = MutableLiveData<NfcResultCode>(NfcResultCode.BUSY)

    var authenticityStatus = MutableLiveData<AuthenticityStatus>(AuthenticityStatus.UNKNOWN)
    var certificateList = MutableLiveData<MutableList<String>>()

    private lateinit var cardStatus: ApplicationStatus


    var actionType: NfcActionType = NfcActionType.DO_NOTHING
    val isConnected =
        MutableLiveData(false) // the app is connected to a card, value updated in SeedkeeperCardListener

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

            NfcActionType.GET_SECRETS_LIST -> {
                getSecretsList()
            }

            NfcActionType.GENERATE_A_SECRET -> {
                generateASecret()
            }

            NfcActionType.SETUP_CARD -> {
                cardSetup()
            }

            NfcActionType.GET_SECRET -> {
                getSecret()
            }

            NfcActionType.DELETE_SECRET -> {
                deleteSecret()
            }
        }

    }

    fun getCardVersionInt(cardStatus: ApplicationStatus): Int {
        return cardStatus.getCardVersionInt()
    }

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

    fun readCard() {
        SatoLog.d(TAG, "readCard Start")
        try {
            isCardDataAvailable.postValue(false)
            cmdSet.cardSelect("seedkeeper").checkOK()
            val rapduStatus = cmdSet.cardGetStatus()
            cardStatus = cmdSet.applicationStatus ?: return
            cardStatus = ApplicationStatus(rapduStatus)
            SatoLog.d(TAG, "card status: $cardStatus")
            SatoLog.d(TAG, "is setup done: ${cardStatus.isSetupDone}")

            if (!cardStatus.isSetupDone) {
                SatoLog.d(TAG, "CardVersionInt: ${getCardVersionInt(cardStatus)}, setup not done")
                isSetupNeeded.postValue(true)
                resultCodeLive.postValue(NfcResultCode.REQUIRE_SETUP)
                return
            } else {
                isReadyForPinCode.postValue(true)
            }
        } catch (e: Exception) {
            SatoLog.e(TAG, "readCard exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun cardSetup() {
        SatoLog.d(TAG, "Card setup start")

        cmdSet.applicationStatus ?: return
        var respApdu = APDUResponse(ByteArray(0), 0x00, 0x00)
        val pinBytes = pinString?.toByteArray(Charsets.UTF_8)
        try {
            respApdu = cmdSet.cardSetup(5, pinBytes) ?: respApdu
        } catch (error: Exception) {
            SatoLog.e(TAG, "Couldn't setup card, error: $error")
        }
        // verify PIN
        cmdSet.setPin0(pinBytes)
        cmdSet.cardVerifyPIN()
        isSetupNeeded.postValue(false)
        isCardDataAvailable.postValue(true)

        SatoLog.d(TAG, "Card setup successful")
    }

    fun verifyPin(
        shouldUpdateDataState: Boolean = true
    ) {
        SatoLog.d(TAG, "Card verification start")

        var respApdu = APDUResponse(ByteArray(0), 0x00, 0x00)
        val pinBytes = pinString?.toByteArray(Charsets.UTF_8)

        cmdSet.cardSelect("seedkeeper").checkOK()
        cmdSet.setPin0(pinBytes)
        cmdSet.cardVerifyPIN()

        getSecretsList()

        isReadyForPinCode.postValue(false)
        if (shouldUpdateDataState) {
            isCardDataAvailable.postValue(true)
        }
        SatoLog.d(TAG, "Card verification successful")
    }

    fun getSecretsList() {
        SatoLog.d(TAG, "Get secret headers")
        try {
            secretHeaders.postValue(cmdSet.seedkeeperListSecretHeaders())
            SatoLog.d(TAG, "Get secret headers successful")
        } catch (e: Exception) {
            SatoLog.e(TAG, "generate a secret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun getSecretBytes(): ByteArray {
        val secretBytes = mutableListOf<Byte>()

        passwordData?.let { data ->
            if (data.type == SeedkeeperSecretType.BIP39_MNEMONIC) {
                data.mnemonic?.let { mnemonic ->
                    val mnemonicBytes = mnemonic.toByteArray(Charsets.UTF_8)
                    val mnemonicSize = mnemonicBytes.size.toByte()
                    secretBytes.add(mnemonicSize)
                    secretBytes.addAll(mnemonicBytes.toList())
                    if (data.password.isNotEmpty()) {
                        val passphraseBytes = data.password.toByteArray(Charsets.UTF_8)
                        val passphraseSize = passphraseBytes.size.toByte()
                        secretBytes.add(passphraseSize)
                        secretBytes.addAll(passphraseBytes.toList())
                    }
                }
            } else {
                val passwordBytes = data.password.toByteArray(Charsets.UTF_8)
                val passwordSize = passwordBytes.size.toByte()
                secretBytes.add(passwordSize)
                secretBytes.addAll(passwordBytes.toList())
                data.login?.let {
                    val loginBytes = it.toByteArray(Charsets.UTF_8)
                    val loginSize = loginBytes.size.toByte()
                    secretBytes.add(loginSize)
                    secretBytes.addAll(loginBytes.toList())
                }
                data.url?.let {
                    val urlBytes = it.toByteArray(Charsets.UTF_8)
                    val urlSize = urlBytes.size.toByte()
                    secretBytes.add(urlSize)
                    secretBytes.addAll(urlBytes.toList())
                }
            }
        }

        return secretBytes.toByteArray()
    }


    fun generateASecret() {
        SatoLog.d(TAG, "generate a secret start")
        try {
            verifyPin(false)
            val secretBytes = getSecretBytes()
            val secretFingerprintBytes = SeedkeeperSecretHeader.getFingerprintBytes(secretBytes)


            passwordData?.let { data ->
                val subType =
                    if (data.type == SeedkeeperSecretType.BIP39_MNEMONIC) 0x00.toByte() else 0x01.toByte() //else is for password
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
                val secretObject = SeedkeeperSecretObject(
                    secretBytes,
                    secretHeader,
                    false,
                    null
                )
                cmdSet.seedkeeperImportSecret(secretObject)
                SatoLog.d(TAG, "import secret success?")
                getSecretsList()
                SatoLog.d(TAG, "new secret list")
            }
            SatoLog.d(TAG, "generate a secret end")
        } catch (e: Exception) {
            SatoLog.e(TAG, "generate a secret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun getSecret() {
        SatoLog.d(TAG, "get secret start")
        try {
            currentSecretObject.postValue(null)
            verifyPin(false)
            currentSecretId?.let { sid ->
                val secretObject = cmdSet.seedkeeperExportSecret(sid, null)
                currentSecretObject.postValue(secretObject)
            }
        } catch (e: Exception) {
            SatoLog.e(TAG, "get secret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }

    fun deleteSecret() {
        SatoLog.d(TAG, "delete secret start")
        try {
            verifyPin(false)
            currentSecretId?.let { sid ->
                cmdSet.seedkeeperResetSecret(sid)
                getSecretsList()
                SatoLog.d(TAG, "new secret list")
                currentSecretObject.postValue(null)
                isCardDataAvailable.postValue(false)
                isCardDataAvailable.postValue(true)
            }
        } catch (e: Exception) {
            SatoLog.e(TAG, "delete secret exception: $e")
            SatoLog.e(TAG, Log.getStackTraceString(e))
        }
    }
}