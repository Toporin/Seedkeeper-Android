package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun MainBackupButton(
    backupStatus: MutableState<BackupStatus>,
    showNfcDialog: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SatoButton(
        onClick = {
            when (backupStatus.value) {
                BackupStatus.DEFAULT -> {
                    backupStatus.value = BackupStatus.FIRST_STEP
                }

                BackupStatus.FIRST_STEP -> {
                    backupStatus.value = BackupStatus.SECOND_STEP
                    showNfcDialog.value = !showNfcDialog.value
                }

                BackupStatus.SECOND_STEP -> {
                    backupStatus.value = BackupStatus.THIRD_STEP
                    showNfcDialog.value = !showNfcDialog.value
                }

                BackupStatus.THIRD_STEP -> {
                    backupStatus.value = BackupStatus.FOURTH_STEP
                }

                BackupStatus.FOURTH_STEP -> {
                    backupStatus.value = BackupStatus.FIFTH_STEP
                    showNfcDialog.value = !showNfcDialog.value
                }

                BackupStatus.FIFTH_STEP -> {
                    onClick()
                }
            }
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

            BackupStatus.FIFTH_STEP -> {
                R.string.home
            }
        }
    )
}