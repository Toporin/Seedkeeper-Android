package org.satochip.seedkeeper.data

import org.bitcoinj.crypto.MnemonicCode
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.utils.toMnemonicList
import java.nio.ByteBuffer

private const val TAG = "SecretData"

data class SecretData(
    var size: Int? = null,
    var type: SeedkeeperSecretType,
    var password: String,
    var label: String,
    var login: String? = null,
    var url: String? = null,
    var mnemonic: String? = null,
    var exportRights: Int = SeedkeeperExportRights.EXPORT_PLAINTEXT_ALLOWED.value.toInt(),
    var subType: Int? = null,
    var descriptor: String? = null
) {
    fun getSecretBytes(): ByteArray {
        val secretBytes = mutableListOf<Byte>()

        when (this.type) {
            SeedkeeperSecretType.MASTERSEED -> {
                this.mnemonic?.let { mnemonic ->
                    val masterseedBytes = MnemonicCode.toSeed(mnemonic.toMnemonicList(), this.password)
                    val masterseedSize = masterseedBytes.size.toByte()
                    val entropyBytes = MnemonicCode.INSTANCE.toEntropy(mnemonic.toMnemonicList())
                    val entropySize = entropyBytes.size.toByte()
                    val passphraseBytes = this.password.toByteArray(Charsets.UTF_8)
                    val passphraseSize = passphraseBytes.size.toByte()
                    secretBytes.add(masterseedSize)
                    secretBytes.addAll(masterseedBytes.toList())
                    secretBytes.add(0x00.toByte())
                    secretBytes.add(entropySize)
                    secretBytes.addAll(entropyBytes.toList())
                    secretBytes.add(passphraseSize)
                    secretBytes.addAll(passphraseBytes.toList())
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
                    if (this.password.isNotEmpty()) {
                        val passphraseBytes = this.password.toByteArray(Charsets.UTF_8)
                        val passphraseSize = passphraseBytes.size.toByte()
                        secretBytes.add(passphraseSize)
                        secretBytes.addAll(passphraseBytes.toList())
                    }
                }
            }
            SeedkeeperSecretType.PASSWORD -> {
                val passwordBytes = this.password.toByteArray(Charsets.UTF_8)
                val passwordSize = passwordBytes.size.toByte()
                secretBytes.add(passwordSize)
                secretBytes.addAll(passwordBytes.toList())
                this.login?.let {
                    val loginBytes = it.toByteArray(Charsets.UTF_8)
                    val loginSize = loginBytes.size.toByte()
                    secretBytes.add(loginSize)
                    secretBytes.addAll(loginBytes.toList())
                }
                this.url?.let {
                    val urlBytes = it.toByteArray(Charsets.UTF_8)
                    val urlSize = urlBytes.size.toByte()
                    secretBytes.add(urlSize)
                    secretBytes.addAll(urlBytes.toList())
                }
            }
            SeedkeeperSecretType.DATA, SeedkeeperSecretType.WALLET_DESCRIPTOR -> {
                this.descriptor?.let { descriptor ->
                    val descriptorBytes = descriptor.toByteArray(Charsets.UTF_8)
                    val descriptorSize = descriptorBytes.size.toShort()
                    val descriptorSizeArray = ByteBuffer.allocate(2).putShort(descriptorSize).array()

                    secretBytes.addAll(descriptorSizeArray.toList())
                    secretBytes.addAll(descriptorBytes.toList())
                }
            }
            else -> {
                SatoLog.e(TAG, "Unsupported secret type")
            }
        }
        return secretBytes.toByteArray()
    }
}
