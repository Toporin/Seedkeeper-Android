package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun MainBackupButton(
    backupStatus: MutableState<BackupStatus>,
    onClick: () -> Unit
) {
    SatoButton(
        onClick = {
            onClick()
        },
        text = when (backupStatus.value) {
            BackupStatus.DEFAULT -> {
                R.string.start
            }
            BackupStatus.FIRST_STEP -> {
                R.string.next
            }
            BackupStatus.SECOND_STEP -> {
                R.string.scanMySeedkeeper
            }
            BackupStatus.THIRD_STEP -> {
                R.string.makeBackup
            }
            BackupStatus.FOURTH_STEP -> {
                R.string.next
            }
            BackupStatus.SUCCESS -> {
                R.string.home
            }
            BackupStatus.FAILURE -> {
                R.string.home
            }
        }
    )
}