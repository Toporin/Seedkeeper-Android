package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.ui.theme.SatoDarkGray
import org.satochip.seedkeeper.ui.theme.SatoPurple

@Composable
fun ToggleOption(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White
        )
        Switch(
            modifier = Modifier
                .rotate(180f),
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = SatoPurple.copy(alpha = 0.5f),
                checkedTrackColor = Color.White,
                uncheckedThumbColor = SatoDarkGray,
                uncheckedTrackColor = Color.White,
                checkedBorderColor = Color.Transparent,
                disabledUncheckedBorderColor = Color.Transparent,
                disabledCheckedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
            )
        )
    }
}