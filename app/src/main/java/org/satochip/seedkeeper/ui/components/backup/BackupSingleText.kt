package org.satochip.seedkeeper.ui.components.backup

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun BackupSingleText(
    text: Int,
    fontWeight: FontWeight = FontWeight.ExtraLight
) {
    Text(
        text = stringResource(text),
        style = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    )
}