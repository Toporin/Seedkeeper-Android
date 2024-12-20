package org.satochip.seedkeeper.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.ui.theme.SatoLightPurple

@Composable
fun InfoField(
    modifier: Modifier = Modifier,
    title: Int? = null,
    text: String,
    containerColor: Color = SatoLightPurple,
    isClickable: Boolean = false,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    titleColor: Color = Color.Black,
    icon: Int? = null,
    isPadded: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPadded) {
            Spacer(modifier = Modifier.height(32.dp))
        }
        title?.let {
            Text(
                text = stringResource(id = title),
                style = TextStyle(
                    color = titleColor,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(
                    color = containerColor
                )
                .clickable {
                    if (isClickable) {
                        onClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            icon?.let {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterEnd)
                        .size(24.dp),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(textColor)
                )
            }
        }
    }
}