package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.getDrawableIdFromType
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun SecretButton(
    modifier: Modifier = Modifier,
    secretHeader: SeedkeeperSecretHeader? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .satoClickEffect(
                onClick = {
                    onClick()
                }
            )
            .background(
                color = SatoPurple,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        secretHeader?.let {
            val imageId: Int = getDrawableIdFromType(secretHeader.type)
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                GifImage(
                    modifier = Modifier
                        .size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                    image = imageId
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = secretHeader.label,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.ExtraLight
                    )
                )
            }

            GifImage(
                modifier = Modifier
                    .size(24.dp),
                colorFilter = ColorFilter.tint(Color.White),
                image = R.drawable.error_24px
            )
        } ?: run {
            Spacer(modifier = Modifier)
            GifImage(
                modifier = Modifier
                    .size(24.dp),
                colorFilter = ColorFilter.tint(Color.White),
                image = R.drawable.plus_circle
            )
            Spacer(modifier = Modifier)
        }
    }
}