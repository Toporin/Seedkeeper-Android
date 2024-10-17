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
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.InputPinField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.rememberImeState
import org.satochip.seedkeeper.viewmodels.SharedViewModel

// TODO merge with EditPinCodeView?
@Composable
fun PinCodeView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    title: Int,
    messageTitle: Int,
    message: Int,
    placeholderText: Int,
    isBackupCardScan: Boolean,
) {
    LaunchedEffect(viewModel.resultCodeLive) {
        if (viewModel.resultCodeLive == NfcResultCode.SECRET_HEADER_LIST_SET && viewModel.isCardDataAvailable) {
            navController.popBackStack()
        } else if (viewModel.resultCodeLive == NfcResultCode.BACKUP_CARD_SCANNED_SUCCESSFULLY) {
            navController.popBackStack()
        }else {
            // todo something in other cases?
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

    val curValue = remember {
        mutableStateOf("")
    }
    val imeState = rememberImeState()

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
                titleText = title
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = messageTitle),
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
                    text = stringResource(id = message),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                InputPinField(
                    curValue = curValue,
                    placeHolder = placeholderText
                )
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
                if (imeState.value) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SatoButton(
                        modifier = Modifier,
                        onClick = {
                            if (curValue.value.toByteArray(Charsets.UTF_8).size in 4..16) {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setPinStringForCard(pinString = curValue.value, isBackupCard = isBackupCardScan)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = if (isBackupCardScan) NfcActionType.SCAN_BACKUP_CARD else NfcActionType.SCAN_CARD
                                )
                            } else {
                                appError.value = AppErrorMsg.PIN_WRONG_FORMAT
                                showError.value = true
                            }
                        },
                        text = R.string.confirm
                    )
                }
            }
            Column {
                SatoButton(
                    modifier = Modifier,
                    onClick = {
                        if (curValue.value.toByteArray(Charsets.UTF_8).size in 4..16) {
                            showNfcDialog.value = true // NfcDialog
                            //viewModel.setNewPinString(curValue.value) // TODO set pin according to isBackupCardScan!
                            viewModel.setPinStringForCard(pinString = curValue.value, isBackupCard = isBackupCardScan)
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = if (isBackupCardScan) NfcActionType.SCAN_BACKUP_CARD else NfcActionType.SCAN_CARD
                            )
                        } else {
                            appError.value = AppErrorMsg.PIN_WRONG_FORMAT
                            showError.value = true
                        }
                    },
                    text = R.string.confirm
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }