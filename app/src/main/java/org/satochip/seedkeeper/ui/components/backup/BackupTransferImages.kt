package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.ui.components.shared.GifImage

@Composable
fun BackupTransferImages(
    backupStatus: BackupStatus
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (backupStatus != BackupStatus.FIFTH_STEP) {
            BackupTransferImage(
                image = R.drawable.first_welcome_card,
                text = R.string.masterCard,
                alpha = if (backupStatus == BackupStatus.FIRST_STEP || backupStatus == BackupStatus.FOURTH_STEP) 0.3f else 1f
            )
            BackupTransferImage(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(120.dp),
                image = R.drawable.key_backup,
                alpha = if (backupStatus == BackupStatus.FIRST_STEP || backupStatus == BackupStatus.SECOND_STEP) 0.3f else 1f
            )
            BackupTransferImage(
                image = R.drawable.first_welcome_card,
                text = R.string.backupCard,
                alpha = if (backupStatus == BackupStatus.SECOND_STEP) 0.3f else 1f
            )
        } else {
            GifImage(
                modifier = Modifier
                    .size(300.dp),
                image = R.drawable.second_welcome_card
            )
        }
    }
}