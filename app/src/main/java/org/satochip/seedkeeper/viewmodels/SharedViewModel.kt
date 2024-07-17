package org.satochip.seedkeeper.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.StringConstants
import org.satochip.seedkeeper.services.NFCCardService
import java.io.BufferedReader
import java.io.InputStreamReader

private const val TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    var isSetupNeeded by mutableStateOf(false)
    var isReadyForPinCode by mutableStateOf(false)
    var secretHeaders = mutableStateListOf<SeedkeeperSecretHeader?>()
    var isCardConnected by mutableStateOf(false)
    var isCardDataAvailable by mutableStateOf(false)
    var currentSecretObject by mutableStateOf<SeedkeeperSecretObject?>(null)
    var resultCodeLive by mutableStateOf(NfcResultCode.BUSY)
    var updateSecretsJob: Job? = null

    init {
        NFCCardService.isSetupNeeded.observeForever {
            isSetupNeeded = it
        }
        NFCCardService.isReadyForPinCode.observeForever {
            isReadyForPinCode = it
        }
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
        NFCCardService.secretHeaders.observeForever{
            updateSecretsJob?.cancel()
            updateSecretsJob = viewModelScope.launch {
                secretHeaders.clear()
                secretHeaders.addAll(it)
            }
        }
    }

    fun setContext(context: Context) {
        this.context = context
        NFCCardService.context = context
    }

    fun setupNewPinString(pinString: String) {
        NFCCardService.pinString = pinString
    }

    fun setPasswordData(passwordData: GeneratePasswordData) {
        NFCCardService.passwordData = passwordData
    }

    fun setCurrentSecret(sid: Int) {
        NFCCardService.currentSecretId = sid
    }

    fun resetIsCardAvailable() {
        NFCCardService.isCardDataAvailable.postValue(false)
    }

    fun resetCurrentSecretObject() {
        NFCCardService.currentSecretObject.postValue(null)
    }

    fun scanCardForAction(activity: Activity, nfcActionType: NfcActionType) {
        Log.d(TAG, "scanCardForAction START")
        NFCCardService.actionType = nfcActionType
        viewModelScope.launch {
            resetIsCardAvailable()
            NFCCardService.scanCardForAction(activity)
            Log.d(TAG, "scanCardForAction END")
        }
    }

    fun generatePassword(options: PasswordOptions): String? {
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
            return null

        for (i in 0 until options.passwordLength) {
            val randomIndex = (characterSet.indices).random()
            password.append(characterSet[randomIndex])
        }

        return password.toString()
    }

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

    fun generateMnemonic(mnemonicSize: Int): String {
        val password = StringBuilder()

        for (i in 0 until mnemonicSize) {
            val randomMnemonic = MnemonicCode.INSTANCE.wordList.random()
            password.append(randomMnemonic)

            if (i != mnemonicSize - 1) {
                password.append(" ")
            }
        }

        return password.toString()
    }


    private fun getWordList(context: Context): List<String> {
        val wordList = mutableListOf<String>()
        context.assets.open("password-replacement.txt").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.forEachLine { line ->
                    wordList.add(line)
                }
            }
        }
        return wordList
    }
}