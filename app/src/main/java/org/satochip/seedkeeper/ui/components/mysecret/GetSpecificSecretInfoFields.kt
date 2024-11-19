package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.ui.components.import.SecretTextField

@Composable
fun GetSpecificSecretInfoFields(
    secretType: SeedkeeperSecretType,
    secret: MutableState<SecretData?>,
) {
    when (secretType) {

        // Mnemonic
        SeedkeeperSecretType.MASTERSEED, SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC -> {
            if (secret.value?.subType != 0 || secretType != SeedkeeperSecretType.MASTERSEED) {

                secret.value?.size?.also { size ->
                    SecretInfoField(
                        title = R.string.mnemonicSize,
                        text = (size).toString()
                    )
                }
                secret.value?.passphrase?.also { passphrase ->
                    SecretInfoField(
                        title = R.string.passphrase,
                        optional = R.string.optional,
                        text = passphrase
                    )
                }
                secret.value?.descriptor?.also { descriptor ->
                    // use SecretTextField for descriptor
                    val secretText = remember {
                        mutableStateOf(descriptor)
                    }
                    SecretTextField(
                        title = stringResource(id = R.string.descriptor)+":",
                        curValue = secretText,
                        placeholder = stringResource(id = R.string.descriptor),
                        isSeedQRCodeEnabled = false,
                        minHeight = 150.dp
                    )
                }
            }
        }

        // Password
        SeedkeeperSecretType.PASSWORD -> {
            secret.value?.login?.also{ login ->
                SecretInfoField(
                    title = R.string.login,
                    optional = R.string.optional,
                    text = login
                )
            }

            secret.value?.url?.also{url ->
                SecretInfoField(
                    title = R.string.url,
                    optional = R.string.optional,
                    text = url,
                    isUrl = true,
                )
            }

        }

        // currently no other optional field supported
        else -> {}
    }
}