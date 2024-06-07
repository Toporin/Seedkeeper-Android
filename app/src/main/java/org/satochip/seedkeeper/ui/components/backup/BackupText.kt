package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus

@Composable
fun BackupText(
    backupStatus: MutableState<BackupStatus>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        when (backupStatus.value) {
            BackupStatus.DEFAULT -> {
                BackupSingleText(R.string.backupDescription)
                Spacer(modifier = Modifier.height(16.dp))
                BackupSingleText(R.string.pairingStartProcess)
            }
            BackupStatus.FIRST_STEP -> {
                BackupSingleText(R.string.pairingFirstStep)
            }
            BackupStatus.SECOND_STEP -> {
                BackupSingleText(R.string.pairingSecondStep)
            }
            BackupStatus.THIRD_STEP -> {
                BackupSingleText(R.string.pairingThirdStepDoneTitle)
                Spacer(modifier = Modifier.height(16.dp))
                BackupSingleText(R.string.pairingThirdStepDoneText)
            }
            BackupStatus.FOURTH_STEP -> {
                BackupSingleText(R.string.pairingFourthStep)
            }
            BackupStatus.FIFTH_STEP -> {
                BackupSingleText(R.string.congratulations)
                Spacer(modifier = Modifier.height(16.dp))
                BackupSingleText(R.string.backupFifthText)
            }
        }
    }
}