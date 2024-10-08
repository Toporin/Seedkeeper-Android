package org.satochip.seedkeeper.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.ui.theme.SatoChecked
import org.satochip.seedkeeper.ui.theme.SatoChecker
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.theme.SatoToggleBlack
import org.satochip.seedkeeper.ui.theme.SatoToggleGray
import org.satochip.seedkeeper.ui.theme.SatoToggled

@Composable
fun SatoToggleButton(
    modifier: Modifier,
    text: Int,
    isChecked: MutableState<Boolean>,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .background(
                color = SatoLightPurple,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = text),
            style = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Switch(
            modifier = Modifier,
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = !isChecked.value
                onClick()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = SatoChecked,
                checkedTrackColor = Color.White,
                uncheckedThumbColor = SatoChecker,
                uncheckedTrackColor = Color.White,
                checkedBorderColor = Color.Transparent,
                disabledUncheckedBorderColor = Color.Transparent,
                disabledCheckedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
            ),
            thumbContent = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            shape = CircleShape,
                            color = if (isChecked.value) SatoChecked else SatoChecker
                        )
                )
            }
        )
    }
}