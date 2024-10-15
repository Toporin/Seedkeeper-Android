package org.satochip.seedkeeper.ui.views.backup

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.HomeView
import org.satochip.seedkeeper.PinCodeView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.data.BackupViewItems
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.ui.components.backup.BackupText
import org.satochip.seedkeeper.ui.components.backup.BackupTransferImages
import org.satochip.seedkeeper.ui.components.backup.MainBackupButton
import org.satochip.seedkeeper.ui.components.backup.SecondaryBackupButton
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun BackupView(
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

    val title = remember {
        mutableIntStateOf(R.string.backup)
    }
    val backupStatus = rememberSaveable {
        mutableStateOf(BackupStatus.DEFAULT)
    }
    backupStatus.value = viewModel.backupStatusState
    when (backupStatus.value) {
        BackupStatus.FIRST_STEP -> {
            title.intValue = R.string.pairing
        }
        else -> {
            title.intValue = R.string.backup
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderAlternateRow(
                onClick = {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                    viewModel.setBackupStatus(BackupStatus.DEFAULT)
                },
                titleText = title.intValue
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                BackupText(
                    backupStatus = backupStatus
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(24.dp)
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    BackupTransferImages(
                        backupStatus = backupStatus.value
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!(backupStatus.value == BackupStatus.DEFAULT || backupStatus.value == BackupStatus.FIFTH_STEP)) {
                        SecondaryBackupButton(
                            backupStatus = backupStatus,
                            goBack = {
                                when (viewModel.backupStatusState) {
                                    BackupStatus.FIRST_STEP -> {
                                        viewModel.setBackupStatus(BackupStatus.DEFAULT)
                                    }
                                    else -> {
                                        viewModel.setBackupStatus(BackupStatus.FIRST_STEP)
                                    }
                                }
                            }
                        )
                    }
                    MainBackupButton(
                        backupStatus = backupStatus,
                        onClick = {
                            when (backupStatus.value) {
                                BackupStatus.DEFAULT -> {
                                    viewModel.setBackupStatus(BackupStatus.FIRST_STEP)
                                }
                                BackupStatus.FIRST_STEP -> {
                                    viewModel.setIsReadyForPinCode()
                                    navController.navigate(
                                        PinCodeView(
                                            title = R.string.pinCode,
                                            messageTitle = R.string.pinCode,
                                            message = R.string.enterPinCodeText,
                                            placeholderText = R.string.enterPinCode,
                                            isBackupCardScan = true
                                        )
                                    )
                                }
                                BackupStatus.SECOND_STEP -> {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.SCAN_MASTER_CARD
                                    )
                                }
                                BackupStatus.THIRD_STEP -> {
                                    viewModel.setBackupStatus(BackupStatus.FOURTH_STEP)

                                }
                                BackupStatus.FOURTH_STEP -> {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.TRANSFER_TO_BACKUP
                                    )
                                }
                                BackupStatus.FIFTH_STEP -> {
                                    navController.navigate(HomeView) {
                                        popUpTo(0)
                                    }
                                    viewModel.setBackupStatus(BackupStatus.DEFAULT)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}



