package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.BackupErrorData
import org.satochip.seedkeeper.utils.getDrawableIdFromType

@Composable
fun BackupErrorCard(backupErrorData: BackupErrorData) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        //elevation = 5.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val imageId: Int = getDrawableIdFromType(backupErrorData.type)
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = backupErrorData.label,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(backupErrorData.nfcResultCode.resMsg),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }


        }
    }
}