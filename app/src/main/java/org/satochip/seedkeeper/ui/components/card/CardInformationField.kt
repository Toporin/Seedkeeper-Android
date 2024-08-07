package org.satochip.seedkeeper.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperStatus
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple

@Composable
fun CardInformationField(
    modifier: Modifier = Modifier,
    status: SeedkeeperStatus,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.memoryAvailable) + ": ${status.freeMemory}/${status.totalMemory} ",
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            )
        )
        Text(
            text = stringResource(id = R.string.secretsStored) + ": ${status.nbSecrets}",
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 16.dp)
                .height(2.dp)
                .fillMaxWidth()
                .background(SatoDividerPurple),
        )
    }
}