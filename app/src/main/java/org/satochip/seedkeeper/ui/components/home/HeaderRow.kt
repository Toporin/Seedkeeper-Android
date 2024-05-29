package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HeaderRow(
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 5.dp, start = 20.dp, end = 5.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // LOGO
        IconButton(
            onClick = onClick,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sato_small),
                contentDescription = "logo",
                modifier = Modifier
                    .size(45.dp), //.size(45.dp)
                //.offset(x = 20.dp, y = 20.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
        // TITLE
        Text(
            text = stringResource(id = R.string.seedkeeper),
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 34.sp,
            ),
        )
        // MENU BUTTON
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.MoreVert, "", tint = Color.Black)
        }
    }
}