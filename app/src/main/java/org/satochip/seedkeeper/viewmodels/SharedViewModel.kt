package org.satochip.seedkeeper.viewmodels

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.ApplicationStatus
import org.satochip.client.seedkeeper.SeedkeeperLog
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.client.seedkeeper.SeedkeeperStatus
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.StringConstants
import org.satochip.seedkeeper.services.NFCCardService
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.utils.bytesToHex
import org.satochip.seedkeeper.utils.isFrench
import java.io.BufferedReader
import java.io.InputStreamReader

private const val TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {

    var secretHeaders = mutableStateListOf<SeedkeeperSecretHeader?>()
    var isCardConnected by mutableStateOf(false)
    var isCardDataAvailable by mutableStateOf(false)
    var currentSecretObject by mutableStateOf<SeedkeeperSecretObject?>(null)
    var authenticityStatus by mutableStateOf(AuthenticityStatus.UNKNOWN)
    var currentSecretHeader by mutableStateOf<SeedkeeperSecretHeader?>(null)
    var resultCodeLive by mutableStateOf(NfcResultCode.BUSY)
    var cardLabel by mutableStateOf("")
    private var updateSecretsJob: Job? = null

    // backup
    var backupImportProgress by mutableFloatStateOf(0f) //mutableFloatStateOf(0f)
    var backupExportProgress by mutableFloatStateOf(0f)

    init {
        NFCCardService.isConnected.observeForever {
            isCardConnected = it
        }
        NFCCardService.resultCodeLive.observeForever {
            resultCodeLive = it
        }
        NFCCardService.isCardDataAvailable.observeForever {
            isCardDataAvailable = it
        }
        NFCCardService.currentSecretObject.observeForever {
            currentSecretObject = it
        }
        NFCCardService.currentSecretHeader.observeForever {
            currentSecretHeader = it
        }
        NFCCardService.cardLabel.observeForever {
            cardLabel = it
        }
        NFCCardService.authenticityStatus.observeForever {
            authenticityStatus = it
        }
        NFCCardService.secretHeaders.observeForever{
            updateSecretsJob?.cancel()
            updateSecretsJob = viewModelScope.launch {
                secretHeaders.clear()
                secretHeaders.addAll(it)
            }
        }
        NFCCardService.backupImportProgress.observeForever{
            backupImportProgress = it
        }
        NFCCardService.backupExportProgress.observeForever{
            backupExportProgress = it
        }
    }

    fun setPinStringForCard(pinString: String, isBackupCard: Boolean = false) {
        if (isBackupCard){
            NFCCardService.backupPinString = pinString
        } else {
            NFCCardService.pinString = pinString
        }
    }

    fun changePinStringForCard(pinString: String) {
        NFCCardService.newPinString = pinString
    }

    fun setResultCodeLiveTo(nfcResultCode: NfcResultCode = NfcResultCode.NONE) {
        NFCCardService.resultCodeLive.postValue(nfcResultCode)
    }

    fun getSeedkeeperStatus(): SeedkeeperStatus? {
        return NFCCardService.seedkeeperStatus
    }

    fun getCardStatus(): ApplicationStatus? {
        return NFCCardService.cardStatus
    }

    fun getAuthentikeyDescription() : String {
        NFCCardService.authentikey?.let { authentikeyBytes ->
            val authentikeySecretBytes = ByteArray(authentikeyBytes.size + 1)
            authentikeySecretBytes[0] = authentikeyBytes.size.toByte()
            System.arraycopy(authentikeyBytes, 0, authentikeySecretBytes, 1, authentikeyBytes.size)
            // compute fingerprint for secret
            val authentikeyFingerprintBytes =
                SeedkeeperSecretHeader.getFingerprintBytes(authentikeySecretBytes)
            // create string
            val authentikeyString = "#${bytesToHex(authentikeyFingerprintBytes)}:${bytesToHex(authentikeyBytes)}"
            return authentikeyString
        } ?: run{
            return ""
        }
    }

    fun getCardLogs(): List<SeedkeeperLog> {
        return NFCCardService.cardLogs
    }

    fun getCertificates(): List<String> {
        return NFCCardService.certificateList
    }

    fun setNewCardLabel(cardLabel: String) {
        NFCCardService.cardLabel.postValue(cardLabel)
    }

    fun setPasswordData(passwordData: SecretData) {// TODO rename to secretData/secretPayload
        NFCCardService.passwordData = passwordData
    }

    fun updateCurrentSecretHeader(secretHeader: SeedkeeperSecretHeader){
        NFCCardService.currentSecretHeader.postValue(secretHeader)
    }

    fun resetCurrentSecretObject() {
        NFCCardService.currentSecretObject.postValue(null)
    }

    fun getAppletVersionString(): String {
        NFCCardService.cardStatus?.let { status ->
            return status.cardVersionString
        } ?: run {
            return "unknown"
        }
    }
    fun getProtocolVersionInt(isMasterCard: Boolean = true): Int {
        if (isMasterCard) {
            NFCCardService.cardStatus?.let { status ->
                return status.protocolVersion
            } ?: run {
                return 0
            }
        } else {
            NFCCardService.backupCardStatus?.let { status ->
                return status.protocolVersion
            } ?: run {
                return 0
            }
        }
    }

    fun scanCardForAction(activity: Activity, nfcActionType: NfcActionType) {
        SatoLog.d(TAG, "scanCardForAction START action: ${nfcActionType.name}")
        //NFCCardService.resultCodeLive.postValue(NfcResultCode.BUSY)
        NFCCardService.actionType = nfcActionType
        viewModelScope.launch {
            NFCCardService.scanCardForAction(activity)
            SatoLog.d(TAG, "scanCardForAction END")
        }
    }

    // TODO: move to Utils?
    fun generatePassword(options: PasswordOptions): String {
        var characterSet = ""
        val password = StringBuilder()

        if (options.isLowercaseSelected)
            characterSet += StringConstants.LOWERCASE.value

        if (options.isUppercaseSelected)
            characterSet += StringConstants.UPPERCASE.value

        if (options.isNumbersSelected)
            characterSet += StringConstants.NUMBERS.value

        if (options.isSymbolsSelected)
            characterSet += StringConstants.SYMBOLS.value

        if (characterSet.isEmpty())
            return ""

        for (i in 0 until options.passwordLength) {
            val randomIndex = (characterSet.indices).random()
            password.append(characterSet[randomIndex])
        }

        return password.toString()
    }

    // TODO: move to Utils?
    fun generateMemorablePassword(options: PasswordOptions, context: Context): String {
        val password = StringBuilder()
        val wordList = getWordList(context)

        for (i in 0 until options.passwordLength) {
            var randomMnemonic = wordList.random()

            if (options.isUppercaseSelected) {
                randomMnemonic = randomMnemonic.capitalize()
            }
            password.append(randomMnemonic)

            if (i != options.passwordLength - 1) {

                if (options.isNumbersSelected)
                    password.append(StringConstants.NUMBERS.value.random())

                if (options.isSymbolsSelected)
                    password.append(StringConstants.SYMBOLS.value.random())

                if (!options.isNumbersSelected && !options.isSymbolsSelected)
                    password.append("-")
            }
        }

        return password.toString()
    }

    // TODO: move to Utils?
    fun isMnemonicValid(mnemonic: String): Boolean {
        val mnemonicList = mnemonic.split(" ")

        if (mnemonicList.size !in listOf(12, 18, 24)) {
            return false
        }

        if (mnemonicList.any { word ->
                MnemonicCode.INSTANCE.wordList.indexOf(word) == -1
            }) {
            return false
        }
        return try {
            MnemonicCode.INSTANCE.check(mnemonicList)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun generateMnemonic(mnemonicSize: Int): String {
        val entropyBits = when (mnemonicSize) {
            12 -> 128
            18 -> 192
            24 -> 256
            else -> throw IllegalArgumentException("Invalid mnemonic size. Must be 12, 18, or 24 words.")
        }
        val entropy = ByteArray(entropyBits / 8)
        java.security.SecureRandom().nextBytes(entropy)
        val mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy)

        return mnemonic.joinToString(" ")
    }

    private fun getWordList(context: Context): List<String> {
        val wordList = mutableListOf<String>()
        val fileName = if (isFrench()) "password-replacement-fr.txt" else "password-replacement.txt"
        context.assets.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.forEachLine { line ->
                    wordList.add(line)
                }
            }
        }
        return wordList
    }
}