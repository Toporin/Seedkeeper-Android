package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SecretData

@Composable
fun GetSpecificSecretInfoFields(
    type: String,
    secret: MutableState<SecretData?>,
) {
    val secretType = SeedkeeperSecretType.valueOf(type)
    when (secretType) {
        SeedkeeperSecretType.MASTERSEED, SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC -> {
            if (secret.value?.subType != 0 || secretType != SeedkeeperSecretType.MASTERSEED) {
                SecretInfoField(
                    title = R.string.mnemonicSize,
                    text = (secret.value?.size ?: "").toString()
                )
                SecretInfoField(
                    title = R.string.passphrase,
                    optional = R.string.optional,
                    text = secret.value?.password ?: ""
                )
                SecretInfoField(
                    title = R.string.walletDescriptorOptional,
                    optional = R.string.optional,
                    text = secret.value?.descriptor ?: ""
                )
            }
        }
        SeedkeeperSecretType.PASSWORD -> {
            SecretInfoField(
                title = R.string.login,
                optional = R.string.optional,
                text = secret.value?.login ?: ""
            )

            SecretInfoField(
                title = R.string.url,
                optional = R.string.optional,
                text = secret.value?.url ?: ""
            )
        }
        else -> {}
    }
}