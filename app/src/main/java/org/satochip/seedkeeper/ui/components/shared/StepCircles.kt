package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepCircles(colors: List<Color>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
    ) {
        colors.forEachIndexed { index, color ->
            Canvas(modifier = Modifier.size(12.dp), onDraw = {
                drawCircle(color = color)
            })
            if (index != colors.size - 1) {
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}