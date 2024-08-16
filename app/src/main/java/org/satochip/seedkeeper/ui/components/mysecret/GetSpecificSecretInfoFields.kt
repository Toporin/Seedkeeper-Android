package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData

@Composable
fun GetSpecificSecretInfoFields(
    type: String,
    secret: MutableState<GeneratePasswordData?>,
) {
    if (type == SeedkeeperSecretType.BIP39_MNEMONIC.name || type == SeedkeeperSecretType.MASTERSEED.name || type == SeedkeeperSecretType.ELECTRUM_MNEMONIC.name) {
        SecretInfoField(
            title = R.string.mnemonicSize,
            text = (secret.value?.size ?: "").toString()
        )

        SecretInfoField(
            title = R.string.passphrase,
            text = secret.value?.password ?: ""
        )
    } else if (type == SeedkeeperSecretType.PASSWORD.name) {
            SecretInfoField(
                title = R.string.login,
                text = secret.value?.login ?: ""
            )

            SecretInfoField(
                title = R.string.url,
                text = secret.value?.url ?: ""
            )
    }
}