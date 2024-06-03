package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun SecondaryBackupButton(
    backupStatus: MutableState<BackupStatus>
) {
    SatoButton(
        onClick = {
            when (backupStatus.value) {
                BackupStatus.FIRST_STEP -> {
                    backupStatus.value = BackupStatus.DEFAULT
                }

                BackupStatus.SECOND_STEP -> {
                    backupStatus.value = BackupStatus.FIRST_STEP
                }

                BackupStatus.THIRD_STEP -> {
                    backupStatus.value = BackupStatus.FIRST_STEP                }

                BackupStatus.FOURTH_STEP -> {
                    backupStatus.value = BackupStatus.FIRST_STEP
                }

                else -> {}
            }
        },
        buttonColor = Color.Transparent,
        textColor = Color.Black,
        text = when (backupStatus.value) {
            BackupStatus.FIRST_STEP -> {
                R.string.back
            }
            else -> {
                R.string.restart
            }
        }
    )
}