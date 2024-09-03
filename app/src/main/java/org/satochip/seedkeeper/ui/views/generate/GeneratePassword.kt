package org.satochip.seedkeeper.ui.views.generate

import android.content.SharedPreferences
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
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.ui.components.generate.ButtonsField
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.PasswordLengthField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun GeneratePassword(
    settings: SharedPreferences,
    curValueLabel: MutableState<String>,
    passwordOptions: MutableState<PasswordOptions>,
    secret: MutableState<String>,
    curValueLogin: MutableState<String>,
    curValueUrl: MutableState<String>,
    retrievedSet: MutableState<Set<String>>,
    isPopUpOpened: MutableState<Boolean>,
    onClick: (GenerateViewItems, String?, PasswordOptions?) -> String,
    onImportSecret: (SecretData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.generateAPassword,
            text = R.string.generateExplanation
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
        PasswordLengthField(
            passwordOptions = passwordOptions
        )
        Spacer(modifier = Modifier.height(12.dp))
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
                    GenerateViewItems.GENERATE_A_PASSWORD,
                    null,
                    passwordOptions.value
                )
            },
            onImportSecret = {
                if (isClickable(secret, curValueLabel)) {
                    onImportSecret(
                        SecretData(
                            size = passwordOptions.value.passwordLength,
                            type = SeedkeeperSecretType.PASSWORD,
                            password = secret.value,
                            label = curValueLabel.value,
                            login = curValueLogin.value,
                            url = curValueUrl.value,
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
            }
        )
    }
}