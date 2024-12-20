package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.theme.SatoGradientPurple
import org.satochip.seedkeeper.ui.theme.SatoGradientPurpleLight

@Composable
fun SatoGradientButton(
    modifier: Modifier = Modifier,
    text: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SatoGradientPurple, SatoGradientPurpleLight)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(text),
                color = Color.White,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            )
            GifImage(
                modifier = Modifier
                    .size(48.dp),
                colorFilter = ColorFilter.tint(Color.White),
                image = R.drawable.cart
            )
        }
    }
}