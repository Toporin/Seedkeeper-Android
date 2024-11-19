package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple

@Composable
fun SatoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: Int,
    buttonColor: Color = SatoButtonPurple,
    textColor: Color = Color.White,
    image: Int? = null,
    horizontalPadding: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(50)
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(
                vertical = 10.dp,
                horizontal = horizontalPadding
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
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(text),
                style = TextStyle(
                    color = textColor,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            image?.let {
                Spacer(modifier = Modifier.width(4.dp))
                GifImage(
                    modifier = Modifier
                        .size(16.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                    image = image
                )
            }
        }
    }
}