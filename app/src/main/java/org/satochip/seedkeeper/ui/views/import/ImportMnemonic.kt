package org.satochip.seedkeeper.ui.views.import

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.ImportViewItems
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.import.MnemonicImportField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.getType
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun ImportMnemonic(
    curValueLabel: MutableState<String>,
    curValuePassphrase: MutableState<String>,
    secret: MutableState<String>,
    passwordOptions: MutableState<PasswordOptions>,
    generateStatus: MutableState<GenerateStatus>,
    typeOfSecret: MutableState<TypeOfSecret>,
    onClick: (ImportViewItems, String?) -> Unit,
    onImportSecret: (GeneratePasswordData) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.importAMnemonicPhrase,
            text = R.string.importAMnemonicPhraseMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        MnemonicImportField(
            text = R.string.mnemonicType,
            type = R.string.bip,
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.enterYourMnemonic),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecretTextField(
                curValue = secret,
                isEditable = true,
                isQRCodeEnabled = false,
                copyToClipboard = {
                    onClick(ImportViewItems.COPY_TO_CLIPBOARD, secret.value)
                }
            )
        }
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
            //Import
            SatoButton(
                modifier = Modifier,
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