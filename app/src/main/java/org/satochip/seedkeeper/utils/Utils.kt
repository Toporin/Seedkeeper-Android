package org.satochip.seedkeeper.utils

import android.util.Patterns
import androidx.compose.runtime.MutableState
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.services.SatoLog

const val TAG = "Utlis"

//Generate view
fun isClickable(
    secret: MutableState<String>,
    curValueLogin: MutableState<String>,
    curValueLabel: MutableState<String>
): Boolean {
    return secret.value.isNotEmpty() && curValueLabel.value.isNotEmpty() &&
            (curValueLogin.value.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(curValueLogin.value)
                .matches())
}

fun isEmailCorrect(
    curValueLogin: MutableState<String>
): Boolean {
    return curValueLogin.value.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(curValueLogin.value).matches()
}

fun parseMnemonicCardData(bytes: ByteArray): GeneratePasswordData? {
    var index = 0

    val mnemonicSize = bytes[index].toInt()
    index += 1
    if (index + mnemonicSize > bytes.size) {
        SatoLog.e(TAG, "Invalid mnemonic size")
        return null
    }
    val mnemonicBytes = bytes.copyOfRange(index, index + mnemonicSize)
    index += mnemonicSize
    val mnemonic = String(mnemonicBytes, Charsets.UTF_8)
    if (mnemonic.isEmpty()) {
        SatoLog.e(TAG, "Mnemonic bytes conversion to string failed")
        return null
    }

    var passphrase: String? = null
    if (index < bytes.size) {
        val passphraseSize = bytes[index].toInt()
        index += 1
        if (passphraseSize > 0 && index + passphraseSize <= bytes.size) {
            val passphraseBytes = bytes.copyOfRange(index, index + passphraseSize)
            index += passphraseSize
            passphrase = String(passphraseBytes, Charsets.UTF_8)
        }
    }

    return  GeneratePasswordData(
        password = "$passphrase",
        mnemonic = mnemonic,
        size = countWords(mnemonic),
        label = "",
        type = SeedkeeperSecretType.BIP39_MNEMONIC
    )
}

fun parsePasswordCardData(bytes: ByteArray): GeneratePasswordData? {
    var index = 0

    val passwordSize = bytes[index].toInt()
    index += 1
    if (index + passwordSize > bytes.size) {
        SatoLog.e(TAG, "Invalid password size")
        return null
    }
    val passwordBytes = bytes.copyOfRange(index, index + passwordSize)
    index += passwordSize
    val password = String(passwordBytes, Charsets.UTF_8)
    if (password.isEmpty()) {
        SatoLog.e(TAG, "Password bytes conversion to string failed")
        return null
    }

    var login: String? = null
    if (index < bytes.size) {
        val loginSize = bytes[index].toInt()
        index += 1
        if (loginSize > 0 && index + loginSize <= bytes.size) {
            val loginBytes = bytes.copyOfRange(index, index + loginSize)
            index += loginSize
            login = String(loginBytes, Charsets.UTF_8)
        }
    }

    var url: String? = null
    if (index < bytes.size) {
        val urlSize = bytes[index].toInt()
        index += 1
        if (urlSize > 0 && index + urlSize <= bytes.size) {
            val urlBytes = bytes.copyOfRange(index, index + urlSize)
            index += urlSize
            url = String(urlBytes, Charsets.UTF_8)
        }
    }

    return GeneratePasswordData(
        password = password,
        login = login ?: "",
        url = url ?: "",
        label = "",
        type = SeedkeeperSecretType.PASSWORD,
        size = 0
    )
}

fun getType(
    generateStatus: GenerateStatus
): SeedkeeperSecretType {
    return if (generateStatus == GenerateStatus.LOGIN_PASSWORD) SeedkeeperSecretType.PASSWORD else SeedkeeperSecretType.BIP39_MNEMONIC
}

fun countWords(mnemonic: String): Int {
    return mnemonic.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
}

