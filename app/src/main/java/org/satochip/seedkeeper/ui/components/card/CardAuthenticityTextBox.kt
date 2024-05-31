package org.satochip.seedkeeper.ui.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardAuthenticityTextBox(
    cardAuthTitle: String,
    cardAuthText: String,
    cardAuthUsage: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cardAuthTitle,
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.W500
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = cardAuthText,
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.W500
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = cardAuthUsage,
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}