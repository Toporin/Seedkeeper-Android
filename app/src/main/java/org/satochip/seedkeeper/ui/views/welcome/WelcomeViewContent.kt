package org.satochip.seedkeeper.ui.views.welcome

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeViewContent(
    title: Int,
    text: Int,
    link: String? = null
) {
    Text(
        text = stringResource(title),
        style = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            lineHeight = (14.5).sp,
        ),
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        modifier = Modifier.padding(horizontal = 30.dp),
        text = stringResource(text),
        style = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        ),
    )
}