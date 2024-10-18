package org.satochip.seedkeeper.ui.views.cardinfo

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.seedkeeper.PinEntryView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ShowCardLogs
import org.satochip.seedkeeper.data.AppErrorMsg
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeAction
import org.satochip.seedkeeper.services.NFCCardService
import org.satochip.seedkeeper.ui.components.card.CardStatusField
import org.satochip.seedkeeper.ui.components.card.InfoField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.EditableField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoGreen
import org.satochip.seedkeeper.utils.bytesToHex
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun CardInformation(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
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

    // Authenticity
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    val cardAuthenticityText = remember {
        mutableStateOf(0)
    }

    val authenticityStatus = viewModel.authenticityStatus
    val cardLabel = viewModel.cardLabel
    val cardAppletVersion = viewModel.getAppletVersionString()
    val cardStatus = viewModel.getSeedkeeperStatus()
    val cardAuthentikey = viewModel.getAuthentikeyDescription()

     when (authenticityStatus) {
        AuthenticityStatus.AUTHENTIC -> {
            cardAuthenticityText.value = R.string.cardIsGenuine
            logoColor.value = SatoGreen
        }
        AuthenticityStatus.NOT_AUTHENTIC -> {
            cardAuthenticityText.value = R.string.cardIsNotGenuine
            logoColor.value = Color.Red
        }
        AuthenticityStatus.UNKNOWN -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderAlternateRow(
                titleText = R.string.cardInfo,
                onClick = {
                    navController.popBackStack()
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CardStatusField(
                        title = R.string.seedkeeperStatus,
                        cardAppletVersion = cardAppletVersion,
                        cardAuthentikey = cardAuthentikey,
                        cardStatus = cardStatus
                    )
                }
                item {
                    InfoField(
                        title = R.string.cardAuthenticity,
                        text = stringResource(id = cardAuthenticityText.value),
                        onClick = {
                            navController.navigate(org.satochip.seedkeeper.CardAuthenticity)
                        },
                        containerColor = logoColor.value,
                        isClickable = true,
                        icon = R.drawable.show_password,
                        isPadded = false
                    )
                }
                item {
                    val curValue = remember {
                        mutableStateOf(cardLabel)
                    }

                    EditableField(
                        isEditable = true,
                        isIconShown = true,
                        title = R.string.cardLabel,
                        curValue = curValue,
                        onClick = {
                            if (cardLabel != null && cardLabel.toByteArray(Charsets.UTF_8).size <= 64) {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setupNewCardLabel(cardLabel)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.EDIT_CARD_LABEL
                                )
                            } else {
                                appError.value = AppErrorMsg.CARD_LABEL_TOO_LONG
                                showError.value = true
                            }
                        }
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
                }
                item {
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf("")
                    }
                    EditableField(
                        isEditable = false,
                        isIconShown = true,
                        title = R.string.pinCode,
                        curValue = curValue,
                        placeHolder = R.string.changePinCode,
                        isClickable = true,
                        onClick = {
                            viewModel.setResultCodeLiveTo(NfcResultCode.NONE)
                            navController.navigate(
                                PinEntryView(
                                    pinCodeAction = PinCodeAction.CHANGE_PIN_CODE.name,
                                    isBackupCard = false,
                                )
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        SatoButton(
                            modifier = Modifier
                                .padding(
                                    horizontal = 6.dp
                                ),
                            onClick = {
                                // TODO: scan card to fetch logs
                                navController.navigate(ShowCardLogs)
                            },
                            text = R.string.showLogs,
                            buttonColor = SatoButtonPurple,
                        )
                    }
                }
            }
        }
    }
}