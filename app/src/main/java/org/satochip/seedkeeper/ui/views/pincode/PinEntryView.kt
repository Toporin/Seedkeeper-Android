package org.satochip.seedkeeper.ui.views.pincode

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AppErrorMsg
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeAction
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.InputPinField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun PinEntryView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    pinCodeAction: PinCodeAction,
    isBackupCard: Boolean,
) {
    LaunchedEffect(viewModel.resultCodeLive) {
        SatoLog.d("PinEntryView", "LaunchedEffect resultCodeLive: ${viewModel.resultCodeLive}")

        when(pinCodeAction){
            PinCodeAction.ENTER_PIN_CODE -> {
                if (viewModel.resultCodeLive == NfcResultCode.CARD_SCANNED_SUCCESSFULLY){
                    navController.popBackStack()
                } else if (viewModel.resultCodeLive == NfcResultCode.BACKUP_CARD_SCANNED_SUCCESSFULLY){
                    navController.popBackStack()
                }
            }
            PinCodeAction.SETUP_PIN_CODE -> {
                if (viewModel.resultCodeLive == NfcResultCode.CARD_SETUP_SUCCESSFUL){
                    navController.popBackStack()
                } else if (viewModel.resultCodeLive == NfcResultCode.CARD_SETUP_FOR_BACKUP_SUCCESSFUL){
                    navController.popBackStack()
                }
            }
            PinCodeAction.CHANGE_PIN_CODE -> {
                if (viewModel.resultCodeLive == NfcResultCode.PIN_CHANGED){
                    navController.popBackStack()
                }
            }
            else -> {} // should not happen
        }
    }

    // NFC DIALOG
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

    val curPinValue = remember {
        mutableStateOf("")
    }
    val curSetupPinValue = remember {
        mutableStateOf("")
    }
    val curChangePinValue = remember {
        mutableStateOf("")
    }
    val curConfirmPinValue = remember {
        mutableStateOf("")
    }

    // initial state depending on action targeted
    val pinCodeStatus = remember {
        mutableStateOf(
            when (pinCodeAction) {
                PinCodeAction.ENTER_PIN_CODE -> {PinCodeAction.ENTER_PIN_CODE}
                PinCodeAction.SETUP_PIN_CODE -> {PinCodeAction.SETUP_PIN_CODE}
                PinCodeAction.CHANGE_PIN_CODE -> {PinCodeAction.ENTER_PIN_CODE}
                else -> {PinCodeAction.ENTER_PIN_CODE} // should not happen
            }
        )
    }

    val placeholderText = R.string.enterPinCode
    val title =  remember { mutableStateOf(0) } //R.string.blankTextField
    val messageTitle =  remember { mutableStateOf(0) }
    val message =  remember { mutableStateOf(0) }
    val buttonText =  remember { mutableStateOf(0) }

    // check PIN format
    fun checkPinFormat(pin: String): Boolean {
        if (pin.toByteArray(Charsets.UTF_8).size !in 4..16) {
            appError.value = AppErrorMsg.PIN_WRONG_FORMAT
            showError.value = true
            return false
        }
        return true
    }

    // set label texts accoring to PinCodeAction & PinCodeStatus
    when (pinCodeAction){
        PinCodeAction.ENTER_PIN_CODE -> {
            title.value = R.string.enterPinCodeTitle
            when (pinCodeStatus.value){
                PinCodeAction.ENTER_PIN_CODE -> {
                    messageTitle.value = if (isBackupCard) R.string.enterBackupPinCode else R.string.enterMasterPinCode
                    message.value = R.string.enterPinCodeText
                    buttonText.value = R.string.next
                }
                else -> {} // should not happen
            }
        }
        PinCodeAction.SETUP_PIN_CODE -> {
            title.value = R.string.setupPinTitle
            when (pinCodeStatus.value){
                PinCodeAction.SETUP_PIN_CODE -> {
                    messageTitle.value = if (isBackupCard)  R.string.createBackupPinCode else R.string.createPinCode
                    message.value = R.string.createPinCodeMessage
                    buttonText.value = R.string.next
                }
                PinCodeAction.CONFIRM_PIN_CODE -> {
                    messageTitle.value = R.string.confirmPinCode
                    message.value = R.string.confirmPinCodeMessage
                    buttonText.value = R.string.confirm
                }
                else -> {} // should not happen
            }
        }
        PinCodeAction.CHANGE_PIN_CODE -> {
            title.value = R.string.editPinCodeTitle
            when (pinCodeStatus.value){
                PinCodeAction.ENTER_PIN_CODE -> {
                    messageTitle.value = R.string.editPinCode
                    message.value = R.string.editPinCodeEnterCurrentPin
                    buttonText.value = R.string.next
                }
                PinCodeAction.CHANGE_PIN_CODE -> {
                    messageTitle.value = R.string.editPinCode
                    message.value = R.string.editPinCodeEnterNewPin
                    buttonText.value = R.string.next
                }
                PinCodeAction.CONFIRM_PIN_CODE -> {
                    messageTitle.value = R.string.confirmPinCode
                    message.value = R.string.confirmPinCodeMessage
                    buttonText.value = R.string.confirm
                }
                else -> {} // should not happen
            }
        }
        else -> {
            title.value = R.string.shouldNotHappen
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = {
                    navController.popBackStack()
                },
                titleText = title.value
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = messageTitle.value),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = message.value),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // PIN field according to status
                // We use different variables to store user PIN for the different steps
                when (pinCodeStatus.value) {
                    PinCodeAction.ENTER_PIN_CODE -> {
                        InputPinField(
                            curValue = curPinValue,
                            placeHolder = placeholderText
                        )
                    }
                    PinCodeAction.SETUP_PIN_CODE -> {
                        InputPinField(
                            curValue = curSetupPinValue,
                            placeHolder = placeholderText
                        )
                    }
                    PinCodeAction.CHANGE_PIN_CODE -> {
                        InputPinField(
                            curValue = curChangePinValue,
                            placeHolder = placeholderText
                        )
                    }
                    PinCodeAction.CONFIRM_PIN_CODE -> {
                        InputPinField(
                            curValue = curConfirmPinValue,
                            placeHolder = placeholderText
                        )
                    }
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

                Spacer(modifier = Modifier.height(16.dp))

                SatoButton(
                    modifier = Modifier,
                    text = buttonText.value,
                    onClick = {
                        // update state
                        when (pinCodeAction) {
                            PinCodeAction.ENTER_PIN_CODE -> {
                                if (!checkPinFormat(pin = curPinValue.value)){ return@SatoButton }
                                viewModel.setPinStringForCard(pinString = curPinValue.value, isBackupCard = isBackupCard)
                                showNfcDialog.value = true // NfcDialog
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = if (isBackupCard) NfcActionType.SCAN_BACKUP_CARD else NfcActionType.SCAN_CARD
                                )
                            }

                            PinCodeAction.SETUP_PIN_CODE -> {
                                when (pinCodeStatus.value){
                                    PinCodeAction.SETUP_PIN_CODE -> {
                                        if (!checkPinFormat(pin = curSetupPinValue.value)){ return@SatoButton }
                                       pinCodeStatus.value = PinCodeAction.CONFIRM_PIN_CODE
                                    }
                                    PinCodeAction.CONFIRM_PIN_CODE -> {
                                        if (!checkPinFormat(pin = curConfirmPinValue.value)){ return@SatoButton }
                                        // check that pins match
                                        if (curSetupPinValue.value != curConfirmPinValue.value) {
                                            appError.value = AppErrorMsg.PIN_MISMATCH
                                            showError.value = true
                                            return@SatoButton
                                        }
                                        // perform setup
                                        viewModel.setPinStringForCard(curSetupPinValue.value, isBackupCard = isBackupCard)
                                        showNfcDialog.value = true // NfcDialog
                                        viewModel.scanCardForAction(
                                            activity = context as Activity,
                                            nfcActionType = if (isBackupCard) NfcActionType.SETUP_CARD_FOR_BACKUP else NfcActionType.SETUP_CARD
                                        )
                                    }
                                    else -> {} // should not happen
                                }
                            }

                            PinCodeAction.CHANGE_PIN_CODE -> {
                                when (pinCodeStatus.value){
                                    PinCodeAction.ENTER_PIN_CODE -> {
                                        if (!checkPinFormat(pin = curPinValue.value)){ return@SatoButton }
                                        // enter existing PIN then switch to new PIN
                                        pinCodeStatus.value = PinCodeAction.CHANGE_PIN_CODE
                                    }
                                    PinCodeAction.CHANGE_PIN_CODE -> {
                                        if (!checkPinFormat(pin = curChangePinValue.value)){ return@SatoButton }
                                        // enter new PIN, then switch to confirm new PIN
                                        pinCodeStatus.value = PinCodeAction.CONFIRM_PIN_CODE
                                    }
                                    PinCodeAction.CONFIRM_PIN_CODE -> {
                                        if (!checkPinFormat(pin = curConfirmPinValue.value)){ return@SatoButton }
                                        // check that pins match
                                        if (curChangePinValue.value != curConfirmPinValue.value) {
                                            appError.value = AppErrorMsg.PIN_MISMATCH
                                            showError.value = true
                                            return@SatoButton
                                        }
                                        // perform change
                                        viewModel.setPinStringForCard(curPinValue.value, isBackupCard = false)
                                        viewModel.changePinStringForCard(curChangePinValue.value)
                                        showNfcDialog.value = true // NfcDialog
                                        viewModel.scanCardForAction(
                                            activity = context as Activity,
                                            nfcActionType = NfcActionType.CHANGE_PIN
                                        )
                                    }
                                    else -> {} // should not happen
                                }
                            }
                            else -> {} // should not happen
                        }
                    },
                )

                // show a cancel button in case of error
                if (showError.value){
                    Spacer(modifier = Modifier.height(16.dp))
                    SatoButton(
                        modifier = Modifier,
                        text = R.string.cancel,
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            Spacer(modifier = Modifier)
        }
    }
}

