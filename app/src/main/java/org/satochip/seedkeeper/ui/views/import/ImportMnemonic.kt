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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AddSecretItems
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
    importMode: AddSecretItems,
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
        if (importMode == AddSecretItems.IMPORT_A_SECRET) {
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

        if (importMode == AddSecretItems.GENERATE_A_SECRET){
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.enterYourMnemonic),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraLight,
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecretTextField(
                curValue = secret,
                isEditable = true,
                isQRCodeEnabled = false,
                minHeight = 250.dp
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            // generate button
            if (importMode == AddSecretItems.GENERATE_A_SECRET) {
                SatoButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        // TODO check passwordOptions
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
                    // TODO: check input +validate mnemonic
                    if (isClickable(secret, curValueLabel)) {
                        val secretData = SecretData(
                            size = passwordOptions.value.passwordLength,
                            type = SeedkeeperSecretType.MASTERSEED,
                            subType = 0x01,
                            passphrase = curValuePassphrase.value,
                            label = curValueLabel.value,
                            mnemonic = secret.value,
                            descriptor = curValueWalletDescriptor.value
                        )
                        //isImportInitiated.value = true
                        viewModel.setPasswordData(secretData)
                        showNfcDialog.value = true
                        viewModel.scanCardForAction(
                            activity = context as Activity,
                            nfcActionType = NfcActionType.GENERATE_A_SECRET
                        )

                    }
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