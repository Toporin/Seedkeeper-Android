package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun SecondaryGenerateButton(
    generateStatus: MutableState<GenerateStatus>
) {
    SatoButton(
        onClick = {
            generateStatus.value = GenerateStatus.DEFAULT
        },
        buttonColor = Color.Transparent,
        textColor = Color.Black,
        text = R.string.back
    )
}