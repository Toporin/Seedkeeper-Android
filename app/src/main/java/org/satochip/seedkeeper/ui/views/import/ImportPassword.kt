package org.satochip.seedkeeper.ui.views.import

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.ImportMode
import org.satochip.seedkeeper.data.AppErrorMsg
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.ui.components.import.InputField
import org.satochip.seedkeeper.ui.components.import.PasswordOptionsBox
import org.satochip.seedkeeper.ui.components.import.SecretTextField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.PopUpDialog
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportPassword(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    settings: SharedPreferences,
    importMode: ImportMode,
    curValueLabel: MutableState<String>,
) {
    // NFC dialog
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected
        )
    }

    // error mgmt
    val showError = remember {
        mutableStateOf(false)
    }
    val appError = remember {
        mutableStateOf(AppErrorMsg.OK)
    }

    // secret fields
    val secret = remember {
        mutableStateOf("")
    }
    val curValueLogin = remember {
        mutableStateOf("") //TODO: could be null
        //mutableStateOf<String?>(null)
    }
    val curValueUrl = remember {
        mutableStateOf("") //TODO: could be null
    }
    val passwordOptions = remember {
        mutableStateOf(
            PasswordOptions()
        )
    }

    // Saved login popup
    val isPopUpOpened = remember {
        mutableStateOf(false)
    }
    val retrievedSet = remember {
        mutableStateOf<Set<String>>(emptySet())
    }
    retrievedSet.value = settings.getStringSet(
        SeedkeeperPreferences.USED_LOGINS.name,
        emptySet()
    ) ?: emptySet()

    if (isPopUpOpened.value) {
        PopUpDialog(
            isOpen = isPopUpOpened,
            curValueLogin = curValueLogin,
            title = R.string.emailListTitle,
            list = retrievedSet.value.toList(),
            onClick = { email ->
                val currentSet =
                    settings.getStringSet(SeedkeeperPreferences.USED_LOGINS.name, emptySet())
                        ?.toMutableSet() ?: mutableSetOf()
                if (currentSet.remove(email)) {
                    settings.edit()
                        .putStringSet(SeedkeeperPreferences.USED_LOGINS.name, currentSet)
                        .apply()
                }
                retrievedSet.value = settings.getStringSet(
                    SeedkeeperPreferences.USED_LOGINS.name,
                    emptySet()
                ) ?: emptySet()
                if (retrievedSet.value.isEmpty()) {
                    isPopUpOpened.value = false
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // TITLE
        if (importMode == ImportMode.IMPORT_A_SECRET) {
            TitleTextField(
                title = R.string.importAPassword,
                text = R.string.importAPasswordMessage
            )
        } else {
            TitleTextField(
                title = R.string.generateAPassword,
                text = R.string.generateExplanation
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // LABEL
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // LOGIN
        InputField(
            isEditable = retrievedSet.value.isEmpty(),
            curValue = curValueLogin,
            placeHolder = R.string.loginOptional,
            optional = R.string.optional,
            containerColor = SatoPurple.copy(alpha = 0.5f),
            isEmail = true,
            onClick = {
                if (retrievedSet.value.isNotEmpty()) {
                    isPopUpOpened.value = !isPopUpOpened.value
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // URL
        InputField(
            curValue = curValueUrl,
            placeHolder = R.string.urlOptional,
            optional = R.string.optional,
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

            // Password options
            if (importMode == ImportMode.GENERATE_A_SECRET) {
                PasswordOptionsBox(
                    passwordOptions = passwordOptions
                )
            } else {
                Text(
                    text = stringResource(R.string.enterYourPassword),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraLight,
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // PASSWORD
            SecretTextField(
                curValue = secret,
                isEditable = true,
                isQRCodeEnabled = false,
                minHeight = 250.dp
            )
        }

        // error msg
        if (showError.value) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(appError.value.msg),
                style = TextStyle(
                    color = Color.Red,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {

            // generate button
            if (importMode == ImportMode.GENERATE_A_SECRET) {
                SatoButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        // check passwordOptions
                        if (!passwordOptions.value.isMemorableSelected  &&
                            !passwordOptions.value.isLowercaseSelected &&
                            !passwordOptions.value.isUppercaseSelected &&
                            !passwordOptions.value.isNumbersSelected &&
                            !passwordOptions.value.isSymbolsSelected)
                        {
                            appError.value = AppErrorMsg.NO_CHAR_SELECTED
                            showError.value = true
                            return@SatoButton
                        } else {
                            showError.value = false
                        }

                        secret.value =
                            if (passwordOptions.value.isMemorableSelected) {
                                viewModel.generateMemorablePassword(passwordOptions.value, context)
                            } else {
                                viewModel.generatePassword(passwordOptions.value)
                            }
                    },
                    text = if (secret.value.isNotEmpty()) R.string.regenerate else R.string.generate,
                    horizontalPadding = 1.dp
                )
            }

            //Import
            SatoButton(
                modifier = Modifier,
                onClick = {
                    //check inputs
                    if (curValueLabel.value.isEmpty()){
                        appError.value = AppErrorMsg.LABEL_EMPTY
                        showError.value = true
                        return@SatoButton
                    }
                    if (curValueLabel.value.toByteArray(Charsets.UTF_8).size > 127){
                        appError.value = AppErrorMsg.LABEL_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }
                    if (secret.value.isEmpty()){
                        appError.value = AppErrorMsg.PASSWORD_EMPTY
                        showError.value = true
                        return@SatoButton
                    }
                    if (secret.value.toByteArray(Charsets.UTF_8).size > 255){
                        appError.value = AppErrorMsg.PASSWORD_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }
                    if (curValueLogin.value.toByteArray(Charsets.UTF_8).size > 255){
                        appError.value = AppErrorMsg.LOGIN_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }
                    if (curValueUrl.value.toByteArray(Charsets.UTF_8).size > 255){
                        appError.value = AppErrorMsg.URL_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }

                    val secretData = SecretData(
                        size = passwordOptions.value.passwordLength,
                        type = SeedkeeperSecretType.PASSWORD,
                        password = secret.value,
                        label = curValueLabel.value,
                        login = curValueLogin.value,
                        url = curValueUrl.value,
                    )

                    if (viewModel.getProtocolVersionInt() == 1){
                        val payloadBytes = secretData.getSecretBytes()
                        if (payloadBytes.size > 255){
                            appError.value = AppErrorMsg.SECRET_TOO_LONG_FOR_V1
                            showError.value = true
                            return@SatoButton
                        }
                    }

                    viewModel.setSecretData(secretData)
                    showNfcDialog.value = true
                    viewModel.scanCardForAction(
                        activity = context as Activity,
                        nfcActionType = NfcActionType.IMPORT_SECRET
                    )

                    // save login in preferences
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
                buttonColor = if (
                    isClickable(
                        secret,
                        curValueLabel
                    )
                ) SatoButtonPurple else SatoButtonPurple.copy(alpha = 0.6f),
            ) // import button
        } // Row
    }
}