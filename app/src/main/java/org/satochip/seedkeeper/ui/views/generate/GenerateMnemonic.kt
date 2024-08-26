package org.satochip.seedkeeper.ui.views.generate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.generate.SelectField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.getType
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun GenerateMnemonic(
    curValueLabel: MutableState<String>,
    passwordOptions: MutableState<PasswordOptions>,
    curValuePassphrase: MutableState<String>,
    secret: MutableState<String>,
    generateStatus: MutableState<GenerateStatus>,
    typeOfSecret: MutableState<TypeOfSecret>,
    onClick: (GenerateViewItems, String?, PasswordOptions?) -> String,
    onImportSecret: (GeneratePasswordData) -> Unit
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
        //Back
        SatoButton(
            onClick = {
                secret.value = ""
                generateStatus.value = GenerateStatus.DEFAULT
                typeOfSecret.value = TypeOfSecret.TYPE_OF_SECRET
                passwordOptions.value.passwordLength = 4

            },
            buttonColor = Color.Transparent,
            textColor = Color.Black,
            text = R.string.back
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            //Generate
            SatoButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    secret.value = onClick(
                        GenerateViewItems.GENERATE_MNEMONIC_PHRASE,
                        null,
                        passwordOptions.value
                    )
                },
                text = if (secret.value.isNotEmpty()) R.string.regenerate else R.string.generate,
            )
            //Import
            SatoButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    if (isClickable(secret, curValueLabel)) {
                        onImportSecret(
                            GeneratePasswordData(
                                size = passwordOptions.value.passwordLength,
                                type = getType(generateStatus.value),
                                password = curValuePassphrase.value,
                                label = curValueLabel.value,
                                login = "",
                                url = "",
                                mnemonic = secret.value
                            )
                        )
                    }
                },
                text = R.string.importButton,
                textColor = if (
                    isClickable(
                        secret,
                        curValueLabel
                    )
                ) Color.White else SatoActiveTracer
            )
        }
    }
}