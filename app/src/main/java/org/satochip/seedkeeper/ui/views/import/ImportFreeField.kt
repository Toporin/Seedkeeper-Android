package org.satochip.seedkeeper.ui.views.import

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import org.satochip.seedkeeper.data.AppErrorMsg
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.ui.components.import.InputField
import org.satochip.seedkeeper.ui.components.import.SecretTextField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportFreeField(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
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

    TitleTextField(
        title = R.string.importFreeField,
        text = R.string.importFreeFieldMessage
    )

    InputField(
        curValue = curValueLabel,
        placeHolder = R.string.label,
        containerColor = SatoPurple.copy(alpha = 0.5f)
    )

    SecretTextField(
        curValue = secret,
        placeholder = stringResource(id = R.string.enterYourData),
        isEditable = true,
        isQRCodeEnabled = false,
        isSeedQRCodeEnabled = false,
        minHeight = 250.dp
    )

    // error msg
    if (showError.value) {
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
        modifier = Modifier.fillMaxWidth(), //fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
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
                    appError.value = AppErrorMsg.DATA_EMPTY
                    showError.value = true
                    return@SatoButton
                }
                if (secret.value.toByteArray(Charsets.UTF_8).size > 65535){
                    appError.value = AppErrorMsg.DATA_TOO_LONG
                    showError.value = true
                    return@SatoButton
                }

                val secretData = SecretData(
                    type = SeedkeeperSecretType.DATA,
                    label = curValueLabel.value,
                    data = secret.value
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