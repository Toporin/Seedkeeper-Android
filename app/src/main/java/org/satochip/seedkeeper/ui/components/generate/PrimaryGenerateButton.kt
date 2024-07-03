package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun PrimaryGenerateButton(
    onClick: () -> Unit,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    text: Int,
) {
    SatoButton(
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        text = text,
        textColor = textColor
    )
}