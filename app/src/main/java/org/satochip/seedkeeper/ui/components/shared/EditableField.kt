package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.theme.SatoLightPurple

@Composable
fun EditableField(
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    isIconShown: Boolean = true,
    title: Int? = null,
    curValue: MutableState<String>,
    drawableId: Int = R.drawable.edit_icon,
    placeHolder: Int? = null,
    containerColor: Color = SatoLightPurple,
    isClickable: Boolean = false,
    onClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon: (@Composable () -> Unit)? =
        if (isIconShown) {
            @Composable {
                Image(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = drawableId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        } else null
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        title?.let {
            Text(
                text = stringResource(id = title),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .clickable {
                    if (isClickable) {
                        onClick()
                    }
                },
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            enabled = isEditable,
            value = curValue.value,
            onValueChange = {
                curValue.value = it
            },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            placeholder = {
                placeHolder?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = placeHolder),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = containerColor,
                disabledTextColor = Color.White,
            ),
            minLines = 1,
            maxLines = 1,
            trailingIcon = trailingIcon
        )
    }
}