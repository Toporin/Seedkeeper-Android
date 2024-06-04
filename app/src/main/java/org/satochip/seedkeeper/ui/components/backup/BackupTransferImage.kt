package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BackupTransferImage(
    modifier: Modifier = Modifier.height(150.dp),
    image: Int,
    text: Int? = null,
    alpha: Float = 1f
) {
    Column(
        modifier = Modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
        text?.let {
            BackupSingleText(
                text = text,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}