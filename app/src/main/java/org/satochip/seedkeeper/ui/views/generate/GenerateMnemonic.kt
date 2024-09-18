package org.satochip.seedkeeper.ui.views.generate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.ui.components.generate.ButtonsField
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.generate.SelectField
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun GenerateMnemonic(
    curValueLabel: MutableState<String>,
    passwordOptions: MutableState<PasswordOptions>,
    curValuePassphrase: MutableState<String>,
    curValueWalletDescriptor: MutableState<String>,
    secret: MutableState<String>,
    onClick: (GenerateViewItems, String?, PasswordOptions?) -> String,
    onImportSecret: (SecretData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.generateAMnemonicPhrase,
            text = R.string.generateExplanation
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        SelectField(
            selectList = listOf(
                SelectFieldItem(prefix = null, text = R.string.mnemonicSize),
                SelectFieldItem(prefix = 12, text = R.string.mnemonicWords),
                SelectFieldItem(prefix = 18, text = R.string.mnemonicWords),
                SelectFieldItem(prefix = 24, text = R.string.mnemonicWords),
            ),
            onClick = { length ->
                passwordOptions.value.passwordLength = length
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        InputField(
            curValue = curValuePassphrase,
            placeHolder = R.string.passphrase,
            optional = R.string.optional,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        InputField(
            curValue = curValueWalletDescriptor,
            placeHolder = R.string.walletDescriptorOptional,
            optional = R.string.optional,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SecretTextField(
            curValue = secret,
            isQRCodeEnabled = false,
            copyToClipboard = {
                onClick(GenerateViewItems.COPY_TO_CLIPBOARD, secret.value, null)
            }
        )
        ButtonsField(
            secret = secret,
            curValueLabel = curValueLabel,
            onGenerateClick = {
                secret.value = onClick(
                    GenerateViewItems.GENERATE_MNEMONIC_PHRASE,
                    null,
                    passwordOptions.value
                )
            },
            onImportSecret = {
                if (isClickable(secret, curValueLabel)) {
                    onImportSecret(
                        SecretData(
                            size = passwordOptions.value.passwordLength,
                            type = SeedkeeperSecretType.MASTERSEED,
                            password = curValuePassphrase.value,
                            label = curValueLabel.value,
                            login = "",
                            url = "",
                            mnemonic = secret.value,
                            descriptor = curValueWalletDescriptor.value
                        )
                    )
                }
            }
        )
    }
}