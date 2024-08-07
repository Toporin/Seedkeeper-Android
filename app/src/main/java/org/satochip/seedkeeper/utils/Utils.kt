package org.satochip.seedkeeper.utils

import android.util.Patterns
import androidx.compose.runtime.MutableState
import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.services.SatoLog

const val TAG = "Utlis"

//Generate view
fun isClickable(
    secret: MutableState<String>,
    curValueLabel: MutableState<String>
): Boolean {
    return secret.value.isNotEmpty()
            && curValueLabel.value.isNotEmpty()
}

fun isEmailCorrect(
    curValueLogin: MutableState<String>
): Boolean {
    return curValueLogin.value.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(curValueLogin.value).matches()
}

fun stringToList(inputString: String?): List<String?>? {
    return inputString?.split("\\s+".toRegex())
}

fun parseMasterseedMnemonicCardData(bytes: ByteArray): GeneratePasswordData? {
    var index = 0

    if (bytes.isEmpty()) {
        SatoLog.e(TAG, "Byte array is empty")
        return null
    }
    val masterseedSize = bytes[index].toUByte().toInt()
    index++

    if (masterseedSize < 0 || index + masterseedSize > bytes.size) {
        SatoLog.e(TAG, "Invalid masterseedSize")
        return null
    }
    val masterseedBytes = bytes.copyOfRange(index, index + masterseedSize) // todo: find usage

    index += masterseedSize
    index++
    val entropySize = bytes[index].toUByte().toInt()
    index++

    if (entropySize < 0 || index + entropySize > bytes.size) {
        SatoLog.e(TAG, "Invalid mnemonic size")
        return null
    }
    val entropyBytes = bytes.copyOfRange(index, index + entropySize)

    val mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropyBytes).joinToString(separator = " ")
    index += entropySize

    var passphrase: String? = null
    if (index < bytes.size) {
        val passphraseSize = bytes[index].toUByte().toInt()
        index++
        if (passphraseSize > 0 && index + passphraseSize <= bytes.size) {
            val passphraseBytes = bytes.copyOfRange(index, index + passphraseSize)
            index += passphraseSize
            passphrase = String(passphraseBytes, Charsets.UTF_8)
        }
    }

    return GeneratePasswordData(
        password = passphrase ?: "",
        mnemonic = mnemonic,
        size = countWords(mnemonic),
        label = "",
        type = SeedkeeperSecretType.MASTERSEED
    )
}

fun parseMnemonicCardData(bytes: ByteArray): GeneratePasswordData? {
    var index = 0

    if (bytes.isEmpty()) {
        SatoLog.e(TAG, "Byte array is empty")
        return null
    }
    val mnemonicSize = bytes[index].toUByte().toInt()
    index += 1
    if (mnemonicSize < 0 || index + mnemonicSize > bytes.size) {
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
        val passphraseSize = bytes[index].toUByte().toInt()
        index += 1
        if (passphraseSize > 0 && index + passphraseSize <= bytes.size) {
            val passphraseBytes = bytes.copyOfRange(index, index + passphraseSize)
            index += passphraseSize
            passphrase = String(passphraseBytes, Charsets.UTF_8)
        }
    }

    return GeneratePasswordData(
        password = passphrase ?: "",
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
    return if (generateStatus == GenerateStatus.LOGIN_PASSWORD) SeedkeeperSecretType.PASSWORD else SeedkeeperSecretType.MASTERSEED
}

fun countWords(mnemonic: String): Int {
    return mnemonic.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
}

val instructionsMap: Map<Byte, String> = mapOf(
    0x2A.toByte() to "Setup",
    0x32.toByte() to "Import key",
    0x33.toByte() to "Reset key",
    0x35.toByte() to "Get public from private",
    0x40.toByte() to "Create pin",
    0x42.toByte() to "Verify pin",
    0x44.toByte() to "Change pin",
    0x46.toByte() to "Unblock pin",
    0x60.toByte() to "Logout all",
    0x48.toByte() to "List pins",
    0x3C.toByte() to "Get status",
    0x3D.toByte() to "Card label",
    0x6C.toByte() to "Bip32 import seed",
    0x77.toByte() to "Bip32 reset seed",
    0x73.toByte() to "Bip32 get authentikey",
    0x75.toByte() to "Bip32 set authentikey pubkey",
    0x6D.toByte() to "Bip32 get extended key",
    0x74.toByte() to "Bip32 set extended pubkey",
    0x6E.toByte() to "Sign message",
    0x72.toByte() to "Sign short message",
    0x6F.toByte() to "Sign transaction",
    0x71.toByte() to "Parse transaction",
    0x76.toByte() to "Crypt transaction 2fa",
    0x79.toByte() to "Set 2fa key",
    0x78.toByte() to "Reset 2fa key",
    0x7A.toByte() to "Sign transaction hash",
    0x81.toByte() to "Init secure channel",
    0x82.toByte() to "Process secure channel",
    0xAC.toByte() to "Import encrypted secret",
    0xAA.toByte() to "Import trusted pubkey",
    0xAB.toByte() to "Export trusted pubkey",
    0xAD.toByte() to "Export authentikey",
    0x92.toByte() to "Import pki certificate",
    0x93.toByte() to "Export pki certificate",
    0x94.toByte() to "Sign pki csr",
    0x98.toByte() to "Export pki pubkey",
    0x99.toByte() to "Lock pki",
    0x9A.toByte() to "Challenge response pki",
    0xFF.toByte() to "Reset to factory",
    0xA7.toByte() to "Get seedkeeper status",
    0xA0.toByte() to "Generate seedkeeper master seed",
    0xA3.toByte() to "Generate seedkeeper random secret",
    0xAE.toByte() to "Generate seedkeeper 2fa secret",
    0xA1.toByte() to "Import seedkeeper secret",
    0xA2.toByte() to "Export seedkeeper secret",
    0xA8.toByte() to "Export seedkeeper secret to satochip",
    0xA5.toByte() to "Reset seedkeeper secret",
    0xA6.toByte() to "List seedkeeper secret headers",
    0xA9.toByte() to "Print seedkeeper logs",
    0xAF.toByte() to "Derive seedkeeper master password"
)