package org.satochip.seedkeeper.ui.views.backup

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import org.satochip.seedkeeper.HomeView
import org.satochip.seedkeeper.PinEntryView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeAction
import org.satochip.seedkeeper.services.NFCCardService
import org.satochip.seedkeeper.ui.components.backup.BackupErrorCard
import org.satochip.seedkeeper.ui.components.backup.BackupText
import org.satochip.seedkeeper.ui.components.backup.BackupTransferImages
import org.satochip.seedkeeper.ui.components.backup.MainBackupButton
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun BackupView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    // backup flow state
    val backupStatus = remember { //rememberSaveable {
        mutableStateOf(BackupStatus.DEFAULT)
    }

    // NFC dialog
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected,
            progress = if (backupStatus.value == BackupStatus.SECOND_STEP)
                    viewModel.backupExportProgress
                else
                    viewModel.backupImportProgress,
        )
    }

    LaunchedEffect(viewModel.resultCodeLive) {
        if (viewModel.resultCodeLive == NfcResultCode.BACKUP_CARD_SCANNED_SUCCESSFULLY) {
            backupStatus.value = BackupStatus.SECOND_STEP
        } else if (viewModel.resultCodeLive == NfcResultCode.SECRETS_EXPORTED_SUCCESSFULLY_FROM_MASTER){
            backupStatus.value = BackupStatus.THIRD_STEP
        } else if (viewModel.resultCodeLive == NfcResultCode.CARD_SUCCESSFULLY_BACKED_UP){
            if (NFCCardService.backupErrors.isEmpty()) {
                backupStatus.value = BackupStatus.SUCCESS
            } else {
                backupStatus.value = BackupStatus.FAILURE
            }
        } else if (viewModel.resultCodeLive == NfcResultCode.CARD_BLOCKED ||
                    viewModel.resultCodeLive == NfcResultCode.NO_MEMORY_LEFT)
        {
            backupStatus.value = BackupStatus.FAILURE
        }
    }

    val title = remember {
        mutableIntStateOf(R.string.backup)
    }
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
                    backupStatus.value = BackupStatus.DEFAULT
                },
                titleText = stringResource(title.intValue)
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
                    if (backupStatus.value == BackupStatus.FAILURE){
                        // show error logs
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("details",
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            items(NFCCardService.backupErrors) { errorItem ->
                                BackupErrorCard(errorItem)
                            }
                        }

                    } else {
                        if (backupStatus.value == BackupStatus.SUCCESS){
                            Text("${NFCCardService.backupNumberOfSecretsImported} " + stringResource(R.string.numberSecretsSaved),
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        // backup card illustration
                        BackupTransferImages(
                            backupStatus = backupStatus.value
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    if (!(backupStatus.value == BackupStatus.DEFAULT || backupStatus.value == BackupStatus.SUCCESS)) {
//                        SecondaryBackupButton( // todo remove or improve?
//                            backupStatus = backupStatus,
//                            goBack = {
//                                when (backupStatus.value) {
//                                    BackupStatus.FIRST_STEP -> {
//                                        backupStatus.value = BackupStatus.DEFAULT
//                                    }
//                                    else -> {
//                                        backupStatus.value = BackupStatus.FIRST_STEP
//                                    }
//                                }
//                            }
//                        )
//                    }
                    MainBackupButton(
                        backupStatus = backupStatus,
                        onClick = {
                            when (backupStatus.value) {
                                BackupStatus.DEFAULT -> {
                                    // simple intro, no action
                                    backupStatus.value = BackupStatus.FIRST_STEP
                                    viewModel.setResultCodeLiveTo(NfcResultCode.NONE)// reset resultCodeLive
                                }
                                BackupStatus.FIRST_STEP -> {
                                    // get backup PIN then scan backup card for secret headers
                                    navController.navigate(
                                        PinEntryView(
                                            pinCodeAction = PinCodeAction.ENTER_PIN_CODE.name,
                                            isBackupCard = true,
                                        )
                                    )
                                }
                                BackupStatus.SECOND_STEP -> {
                                    // export secrets from master card
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.EXPORT_SECRETS_FROM_MASTER
                                    )
                                }
                                BackupStatus.THIRD_STEP -> {
                                    // prompt user to switch to backup card again, no action
                                    backupStatus.value = BackupStatus.FOURTH_STEP
                                }
                                BackupStatus.FOURTH_STEP -> {
                                    // scan backup card to import secrets from master
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.IMPORT_SECRETS_TO_BACKUP
                                    )
                                }
                                BackupStatus.SUCCESS -> {
                                    // finished with success, back to home screen
                                    navController.navigate(HomeView) {
                                        popUpTo(0)
                                    }
                                    backupStatus.value = BackupStatus.DEFAULT
                                }
                                BackupStatus.FAILURE -> {
                                    // finished with error, back to home screen
                                    navController.navigate(HomeView) {
                                        popUpTo(0)
                                    }
                                    backupStatus.value = BackupStatus.DEFAULT
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}



