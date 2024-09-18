package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.ui.theme.SatoGreen

@Composable
fun HomeHeaderRow(
    isCardDataAvailable: Boolean,
    authenticityStatus: AuthenticityStatus,
    onClick: (HomeItems) -> Unit,
) {
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    logoColor.value = when (authenticityStatus) {
        AuthenticityStatus.AUTHENTIC -> {
            SatoGreen
        }
        AuthenticityStatus.NOT_AUTHENTIC -> {
            Color.Red
        }
        AuthenticityStatus.UNKNOWN -> {
            Color.Black
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 5.dp, start = 20.dp, end = 5.dp)
            .height(50.dp)
    ) {
        // LOGO
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = {
                onClick(HomeItems.CARD_INFO)
            },
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sato_small),
                contentDescription = "logo",
                modifier = Modifier
                    .size(45.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(logoColor.value)
            )
        }
        // TITLE
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.seedkeeper),
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 34.sp,
            ),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            if (isCardDataAvailable) {
                // RESCAN BUTTON
                IconButton(
                    onClick = {
                        onClick(HomeItems.REFRESH)
                    },
                ) {
                    Image(
                        painter = painterResource(R.drawable.rescan),
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(16.dp),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
            }
            // MENU BUTTON
            IconButton(onClick = {
                onClick(HomeItems.MENU)
            }) {
                Icon(Icons.Default.MoreVert, "", tint = Color.Black)
            }
        }
    }
}