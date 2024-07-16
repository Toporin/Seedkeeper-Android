package org.satochip.seedkeeper.ui.views.backup

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.ui.components.backup.BackupText
import org.satochip.seedkeeper.ui.components.backup.BackupTransferImages
import org.satochip.seedkeeper.ui.components.backup.MainBackupButton
import org.satochip.seedkeeper.ui.components.backup.SecondaryBackupButton
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow

@Composable
fun BackupView(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val title = remember {
            mutableIntStateOf(R.string.backup)
        }
        val showNfcDialog = remember { mutableStateOf(false) }
        val backupStatus = remember {
            mutableStateOf(BackupStatus.DEFAULT)
        }
        when (backupStatus.value) {
            BackupStatus.FIRST_STEP -> {
                title.intValue = R.string.pairing
            }
            else -> {
                title.intValue = R.string.backup
            }
        }

//        if (showNfcDialog.value) {
//            NfcDialog(
//                openDialogCustom = showNfcDialog,
//            )
//        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick()
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
                            backupStatus = backupStatus
                        )
                    }
                    MainBackupButton(
                        backupStatus = backupStatus,
                        showNfcDialog = showNfcDialog,
                        onClick = onClick
                    )
                }
            }
        }
    }
}



