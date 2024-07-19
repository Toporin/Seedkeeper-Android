package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.ui.components.shared.DataAsQrCode
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple

@Composable
fun SecretImageField(
    modifier: Modifier = Modifier.height(150.dp).fillMaxWidth(),
    curValue: MutableState<String>,
    containerColor: Color = SatoDividerPurple.copy(alpha = 0.2f),
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (curValue.value.isNotEmpty()) {
            DataAsQrCode(
                data = curValue.value
            )
        }
    }
}