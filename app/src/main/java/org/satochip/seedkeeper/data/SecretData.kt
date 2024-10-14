package org.satochip.seedkeeper.data

import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.utils.toMnemonicList
import java.nio.ByteBuffer

private const val TAG = "SecretData"

// TODO: refactor SecretData into multiple class according to secret type
data class SecretData(
    var size: Int? = null,
    var type: SeedkeeperSecretType,
    var password: String? = null,
    var passphrase: String? = null, // TODO: use this instead of password for passphrase
    var label: String,
    var login: String? = null,
    var url: String? = null,
    var mnemonic: String? = null,
    var exportRights: Int = SeedkeeperExportRights.EXPORT_PLAINTEXT_ALLOWED.value.toInt(), // TODO: use SeedkeeperExportRights
    var subType: Int? = 0x00, // TODO rename subtype with Byte value
    var descriptor: String? = null,
    var data: String? = null,
    var genericSecret: String? = null // TODO: add pubkey, masterseed fields...
) {
    fun getSecretBytes(): ByteArray {
        val secretBytes = mutableListOf<Byte>()

        when (this.type) {
            SeedkeeperSecretType.MASTERSEED -> {
                this.mnemonic?.let { mnemonic ->
                    val masterseedBytes = MnemonicCode.toSeed(mnemonic.toMnemonicList(), this.passphrase ?: "")
                    val masterseedSize = masterseedBytes.size.toByte()
                    val entropyBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic.toMnemonicList())
                    val entropySize = entropyBytes.size.toByte()
                    secretBytes.add(masterseedSize)
                    secretBytes.addAll(masterseedBytes.toList())
                    secretBytes.add(0x00.toByte())
                    secretBytes.add(entropySize)
                    secretBytes.addAll(entropyBytes.toList())
                    this.passphrase?.also { passphrase ->
                        val passphraseBytes = passphrase.toByteArray(Charsets.UTF_8)
                        val passphraseSize = passphraseBytes.size.toByte()
                        secretBytes.add(passphraseSize)
                        secretBytes.addAll(passphraseBytes.toList())
                    } ?: run {
                        secretBytes.add(0x00.toByte())
                    }
                    this.descriptor?.let { descriptor ->
                        val descriptorBytes = descriptor.toByteArray(Charsets.UTF_8)
                        val descriptorSize = descriptorBytes.size.toShort()
                        val descriptorSizeArray = ByteBuffer.allocate(2).putShort(descriptorSize).array()
                        secretBytes.addAll(descriptorSizeArray.toList())
                        secretBytes.addAll(descriptorBytes.toList())
                    }
                }
            }
            SeedkeeperSecretType.BIP39_MNEMONIC -> {
                this.mnemonic?.let { mnemonic ->
                    val mnemonicBytes = mnemonic.toByteArray(Charsets.UTF_8)
                    val mnemonicSize = mnemonicBytes.size.toByte()
                    secretBytes.add(mnemonicSize)
                    secretBytes.addAll(mnemonicBytes.toList())
                    this.passphrase?.also { passphrase ->
                        val passphraseBytes = passphrase.toByteArray(Charsets.UTF_8)
                        val passphraseSize = passphraseBytes.size.toByte()
                        secretBytes.add(passphraseSize)
                        secretBytes.addAll(passphraseBytes.toList())
                    }
                }
            }
            SeedkeeperSecretType.PASSWORD -> {
                this.password?.also{ password ->
                    val passwordBytes = password.toByteArray(Charsets.UTF_8)
                    val passwordSize = passwordBytes.size.toByte()
                    secretBytes.add(passwordSize)
                    secretBytes.addAll(passwordBytes.toList())
                    this.login?.also { login ->
                        val loginBytes = login.toByteArray(Charsets.UTF_8)
                        val loginSize = loginBytes.size.toByte()
                        secretBytes.add(loginSize)
                        secretBytes.addAll(loginBytes.toList())
                    } ?: run {
                        secretBytes.add(0x00.toByte())
                    }
                    this.url?.let {
                        val urlBytes = it.toByteArray(Charsets.UTF_8)
                        val urlSize = urlBytes.size.toByte()
                        secretBytes.add(urlSize)
                        secretBytes.addAll(urlBytes.toList())
                    }
                }

            }
            SeedkeeperSecretType.WALLET_DESCRIPTOR -> {
                this.descriptor?.let { descriptor ->
                    val descriptorBytes = descriptor.toByteArray(Charsets.UTF_8)
                    val descriptorSize = descriptorBytes.size.toShort()
                    val descriptorSizeArray = ByteBuffer.allocate(2).putShort(descriptorSize).array()

                    secretBytes.addAll(descriptorSizeArray.toList())
                    secretBytes.addAll(descriptorBytes.toList())
                }
            }
            SeedkeeperSecretType.DATA -> {
                this.data?.let { data ->
                    val dataBytes = data.toByteArray(Charsets.UTF_8)
                    val dataSize = dataBytes.size.toShort()
                    val dataSizeArray = ByteBuffer.allocate(2).putShort(dataSize).array()

                    secretBytes.addAll(dataSizeArray.toList())
                    secretBytes.addAll(dataBytes.toList())
                }
            }
            else -> {
                SatoLog.e(TAG, "Unsupported secret type")
            }
        }
        return secretBytes.toByteArray()
    }
}
