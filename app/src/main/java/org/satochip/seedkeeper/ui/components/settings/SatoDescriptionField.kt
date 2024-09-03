package org.satochip.seedkeeper.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SatoDescriptionField(
    title: Int,
    text: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = title),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraLight
            )
        )
        Text(
            text = stringResource(id = text),
            style = TextStyle(
                color = Color.Black,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.W400
            )
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

}