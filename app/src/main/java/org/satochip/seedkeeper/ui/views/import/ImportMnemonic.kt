package org.satochip.seedkeeper.ui.views.import

import android.app.Activity
import android.content.Context
import android.widget.Toast
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
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.ui.components.import.InputField
import org.satochip.seedkeeper.ui.components.import.SecretTextField
import org.satochip.seedkeeper.ui.components.import.SelectField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.import.MnemonicImportField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportMnemonic(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
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
    val curValuePassphrase = remember {
        mutableStateOf("")
    }
    val curValueWalletDescriptor = remember {
        mutableStateOf("")
    }
    val passwordOptions = remember {
        mutableStateOf(
            PasswordOptions()
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (importMode == ImportMode.IMPORT_A_SECRET) {
            TitleTextField(
                title = R.string.importAMnemonicPhrase,
                text = R.string.importAMnemonicPhraseMessage
            )
        } else {
            TitleTextField(
                title = R.string.generateAMnemonicPhrase,
                text = R.string.generateExplanation
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Label
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (importMode == ImportMode.GENERATE_A_SECRET){
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
        } else {
            MnemonicImportField(
                text = R.string.mnemonicType,
                type = R.string.bip,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Passphrase
        InputField(
            curValue = curValuePassphrase,
            placeHolder = R.string.passphrase,
            optional = R.string.optional,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Wallet descriptor
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (importMode == ImportMode.IMPORT_A_SECRET) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.enterYourMnemonic),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraLight,
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
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
                        try {
                            secret.value = viewModel.generateMnemonic(passwordOptions.value.passwordLength)
                        } catch (e: IllegalArgumentException) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show() // todo error msg instead
                            secret.value = ""
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
                        appError.value = AppErrorMsg.MNEMONIC_EMPTY
                        showError.value = true
                        return@SatoButton
                    }
                    if (secret.value.toByteArray(Charsets.UTF_8).size > 255){
                        appError.value = AppErrorMsg.MNEMONIC_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }
                    if (!viewModel.isMnemonicValid(mnemonic = secret.value)){
                        appError.value = AppErrorMsg.MNEMONIC_WRONG_FORMAT
                        showError.value = true
                        return@SatoButton
                    }
                    if (curValuePassphrase.value.toByteArray(Charsets.UTF_8).size > 255){
                        appError.value = AppErrorMsg.PASSPHRASE_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }
                    if (curValueWalletDescriptor.value.toByteArray(Charsets.UTF_8).size > 65535){
                        appError.value = AppErrorMsg.DESCRIPTOR_TOO_LONG
                        showError.value = true
                        return@SatoButton
                    }

                    val secretData = SecretData(
                        size = passwordOptions.value.passwordLength,
                        type = SeedkeeperSecretType.MASTERSEED,
                        subType = 0x01,
                        passphrase = curValuePassphrase.value,
                        label = curValueLabel.value,
                        mnemonic = secret.value,
                        descriptor = curValueWalletDescriptor.value
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


                },
                text = R.string.importButton,
                buttonColor = if (
                    isClickable(
                        secret,
                        curValueLabel
                    )
                ) SatoButtonPurple else SatoButtonPurple.copy(alpha = 0.6f),
            )
        }
    }
}