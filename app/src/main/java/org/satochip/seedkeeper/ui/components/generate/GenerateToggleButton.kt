package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.ui.theme.SatoChecked
import org.satochip.seedkeeper.ui.theme.SatoChecker
import org.satochip.seedkeeper.ui.theme.SatoToggled

@Composable
fun ToggleOption(
    modifier: Modifier = Modifier,
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White
        )
        Switch(
            modifier = Modifier
                .scale(scale = 0.8f),
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
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
                        color = if (isChecked) SatoChecked else SatoChecker
                    )
                )
            }
        )
    }
}