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
    if (type == SeedkeeperSecretType.BIP39_MNEMONIC.name || type == SeedkeeperSecretType.MASTERSEED.name || type == SeedkeeperSecretType.ELECTRUM_MNEMONIC.name) {
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
    } else if (type == SeedkeeperSecretType.PASSWORD.name) {
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
}