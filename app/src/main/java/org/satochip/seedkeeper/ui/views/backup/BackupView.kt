package org.satochip.seedkeeper.ui.views.backup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(MaterialTheme.colorScheme.primary)
    ) {
        val title = remember {
            mutableIntStateOf(R.string.backup)
        }
        val scrollState = rememberScrollState()
        val showNfcDialog = remember { mutableStateOf(false) }
        val backupStatus = remember {
            mutableStateOf(BackupStatus.DEFAULT)
        }

        if (showNfcDialog.value) {
            NfcDialog(
                openDialogCustom = showNfcDialog,
            )
        }
        Image(
            painter = painterResource(R.drawable.seedkeeper_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick()
                },
                titleText = title.intValue
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                BackupText(
                    backupStatus = backupStatus
                )
                Spacer(modifier = Modifier.height(60.dp))
                BackupTransferImages(
                    backupStatus = backupStatus.value
                )
                Spacer(modifier = Modifier.height(120.dp))

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

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}



