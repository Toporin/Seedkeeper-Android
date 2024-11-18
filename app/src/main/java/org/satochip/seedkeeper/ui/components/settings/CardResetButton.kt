package org.satochip.seedkeeper.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.ui.theme.SatoLightPurple

@Composable
fun CardResetButton(
    title: Int? = null,
    text: String,
    containerColor: Color = SatoLightPurple,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    titleColor: Color = Color.Black
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title?.let {
            Text(
                text = stringResource(id = title),
                style = TextStyle(
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 16.dp,
                )
                .height(40.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonColors(
                contentColor = textColor,
                containerColor = containerColor,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            )
        )
        {
            Text(
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}