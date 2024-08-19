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
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.getType
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun ImportPassword(
    settings: SharedPreferences,
    curValueLabel: MutableState<String>,
    curValuePassphrase: MutableState<String>,
    secret: MutableState<String>,
    passwordOptions: MutableState<PasswordOptions>,
    generateStatus: MutableState<GenerateStatus>,
    typeOfSecret: MutableState<TypeOfSecret>,
    curValueLogin: MutableState<String>,
    curValueUrl: MutableState<String>,
    isPopUpOpened: MutableState<Boolean>,
    retrievedSet: MutableState<Set<String>>,
    onClick: (ImportViewItems, String?) -> Unit,
    onImportSecret: (GeneratePasswordData) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.importAPassword,
            text = R.string.importAPasswordMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        InputField(
            isEditable = retrievedSet.value.isEmpty(),
            curValue = curValueLogin,
            placeHolder = R.string.loginOptional,
            containerColor = SatoPurple.copy(alpha = 0.5f),
            isEmail = true,
            onClick = {
                if (retrievedSet.value.isNotEmpty()) {
                    isPopUpOpened.value = !isPopUpOpened.value
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        InputField(
            curValue = curValueUrl,
            placeHolder = R.string.urlOptional,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.enterYourPassword),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            SecretTextField(
                modifier = Modifier.height(200.dp),
                curValue = secret,
                isEditable = true,
                copyToClipboard = {
                    onClick(ImportViewItems.COPY_TO_CLIPBOARD, secret.value)
                }
            )
        }
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
            //Import
            SatoButton(
                modifier = Modifier,
                onClick = {
                    if (isClickable(secret, curValueLabel)) {
                        val type = getType(generateStatus.value)
                        var password: String = ""
                        var mnemonic: String? = null
                        if (type == SeedkeeperSecretType.BIP39_MNEMONIC) {
                            mnemonic = secret.value
                            password = curValuePassphrase.value
                        } else {
                            password = secret.value
                        }
                        onImportSecret(
                            GeneratePasswordData(
                                size = passwordOptions.value.passwordLength,
                                type = getType(generateStatus.value),
                                password = password,
                                label = curValueLabel.value,
                                login = curValueLogin.value,
                                url = curValueUrl.value,
                                mnemonic = mnemonic
                            )
                        )
                    }
                    if (curValueLogin.value.isNotEmpty()) {
                        val stringSet = listOf(curValueLogin.value).toSet()
                        retrievedSet.value += stringSet
                        settings.edit().putStringSet(
                            SeedkeeperPreferences.USED_LOGINS.name,
                            retrievedSet.value
                        ).apply()
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