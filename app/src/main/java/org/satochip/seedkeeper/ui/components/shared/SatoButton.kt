package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.ui.theme.SatoButtonBlue

@Composable
fun SatoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: Int,
    buttonColor: Color = SatoButtonBlue,
    textColor: Color = Color.White,
    shape: RoundedCornerShape = RoundedCornerShape(50)
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(
                vertical = 10.dp,
                horizontal = 16.dp
            )
            .height(40.dp),
        shape = shape,
        colors = ButtonColors(
            contentColor = textColor,
            containerColor = buttonColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        )
    ) {
        Text(
            text = stringResource(text),
            style = TextStyle(
                color = textColor,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}