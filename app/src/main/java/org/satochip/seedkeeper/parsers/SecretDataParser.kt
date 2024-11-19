package org.satochip.seedkeeper.parsers

import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.utils.bytesToHex
import org.satochip.seedkeeper.utils.countWords
import java.nio.ByteBuffer

const val TAG = "SecretDataParser"

class SecretDataParser {

    fun parseByType(seedkeeperSecretType: SeedkeeperSecretType, secretObject: SeedkeeperSecretObject): SecretData? {
        return when (seedkeeperSecretType) {
            SeedkeeperSecretType.PASSWORD -> {
                parsePasswordCardData(secretObject.secretBytes)
            }
            SeedkeeperSecretType.MASTERSEED -> {
                if (secretObject.secretHeader.subtype == 0x00.toByte()) {
                    parseMasterseed(secretObject.secretBytes)
                } else {
                    parseMasterseedMnemonicCardData(secretObject.secretBytes)
                }
            }
            SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC -> {
                parseMnemonicCardData(secretObject.secretBytes, seedkeeperSecretType)
            }
            SeedkeeperSecretType.WALLET_DESCRIPTOR -> {
                 parseWalletDescriptorData(secretObject.secretBytes)
            }
            SeedkeeperSecretType.DATA -> {
                parseFreeData(secretObject.secretBytes)
            }
            SeedkeeperSecretType.PUBKEY -> {
                parsePubkey(secretObject.secretBytes)
            }
            else -> {
                parseGeneralData(secretObject.secretBytes)
            }
        }
    }

    private fun parseMasterseedMnemonicCardData(bytes: ByteArray): SecretData? {
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
        val masterseedBytes = bytes.copyOfRange(index, index + masterseedSize) // not used currently

        index += masterseedSize
        index++ // wordlist (currently english only)
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

        var descriptor: String? = null
        if (index < bytes.size) {
            val descriptorSizeArray = bytes.sliceArray(index..index + 1)
            val descriptorSize = ByteBuffer.wrap(descriptorSizeArray).short
            index += 2
            if (descriptorSize > 0 &&  index + descriptorSize <= bytes.size) {
                val descriptorBytes = bytes.copyOfRange(index, index + descriptorSize)
                descriptor = String(descriptorBytes, Charsets.UTF_8)
            }
        }

        return SecretData(
            passphrase = passphrase,
            mnemonic = mnemonic,
            size = mnemonic.countWords(),
            label = "",
            type = SeedkeeperSecretType.MASTERSEED,
            subType = 0x01,
            descriptor = descriptor
        )
    }

    private fun parseMnemonicCardData(bytes: ByteArray, type: SeedkeeperSecretType): SecretData? {
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

        return SecretData(
            passphrase = passphrase,
            mnemonic = mnemonic,
            size = mnemonic.countWords(),
            label = "",
            type = type
        )
    }

    private fun parseWalletDescriptorData(bytes: ByteArray): SecretData? {
        var index = 0

        val descriptorSizeArray = bytes.sliceArray(0..1)
        val descriptorSize = ByteBuffer.wrap(descriptorSizeArray).short
        index += 2
        if (index + descriptorSize > bytes.size) {
            SatoLog.e(TAG, "Invalid descriptor size")
            return null
        }
        val descriptorBytes = bytes.copyOfRange(index, index + descriptorSize)
        var descriptor = String(descriptorBytes, Charsets.UTF_8)
        if (descriptor.isEmpty()) {
            SatoLog.e(TAG, "Descriptor bytes conversion to string failed")
            descriptor = "Descriptor bytes conversion to string failed"
        }
        return SecretData(
            label = "",
            type = SeedkeeperSecretType.WALLET_DESCRIPTOR,
            size = 0,
            data = descriptor
        )
    }

    private fun parseFreeData(bytes: ByteArray): SecretData? {
        var index = 0

        val dataSizeArray = bytes.sliceArray(0..1)
        val dataSize = ByteBuffer.wrap(dataSizeArray).short
        index += 2
        if (index + dataSize > bytes.size) {
            SatoLog.e(TAG, "Invalid data size")
            return null
        }
        val dataBytes = bytes.copyOfRange(index, index + dataSize)
        var data = String(dataBytes, Charsets.UTF_8)
        if (data.isEmpty()) {
            SatoLog.e(TAG, "Descriptor bytes conversion to string failed")
            data = "Descriptor bytes conversion to string failed" //TODO: return hex value
        }
        return SecretData(
            label = "",
            type = SeedkeeperSecretType.DATA,
            size = 0,
            data = data
        )
    }

    private fun parsePasswordCardData(bytes: ByteArray): SecretData? {
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

        return SecretData(
            password = password,
            login = login,
            url = url,
            label = "",
            type = SeedkeeperSecretType.PASSWORD,
            size = 0
        )
    }

    private fun parseMasterseed(bytes: ByteArray): SecretData? {
        val masterseedSize = bytes[0].toInt()
        if (1 + masterseedSize > bytes.size) {
            SatoLog.e(TAG, "Invalid masterseed size")
            return null
        }
        val masterseedBytes = bytes.copyOfRange(1, 1 + masterseedSize)
        val masterseedHex = masterseedBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }

        return SecretData(
            password = masterseedHex, // todo
            label = "",
            type = SeedkeeperSecretType.MASTERSEED,
            size = 0,
        )
    }

    private fun parsePubkey(bytes: ByteArray): SecretData? {
        val pubkeySize = bytes[0].toInt()
        if (1 + pubkeySize > bytes.size) {
            SatoLog.e(TAG, "Invalid pubkey size")
            return null
        }
        val pubkeyBytes = bytes.copyOfRange(1, 1 + pubkeySize)
        val pubkeyHex = pubkeyBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }

        return SecretData(
            password = pubkeyHex,
            label = "",
            type = SeedkeeperSecretType.PUBKEY,
            size = 0,
        )
    }

    private fun parseGeneralData(bytes: ByteArray): SecretData? {
        val secretSize = bytes[0].toInt()
        if (1 + secretSize > bytes.size) {
            SatoLog.e(TAG, "Invalid secret size")
            return null
        }
        val secretBytes = bytes.copyOfRange(1, 1 + secretSize)
        val secretHex = secretBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }

        return SecretData(
            password = secretHex,
            label = "",
            type = SeedkeeperSecretType.DEFAULT_TYPE,
            size = 0,
        )
    }
}