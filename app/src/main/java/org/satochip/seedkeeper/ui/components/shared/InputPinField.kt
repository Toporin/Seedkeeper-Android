package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun InputPinField(
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    curValue: MutableState<String>,
    placeHolder: Int? = null,
    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
) {
    val passwordVisibility = remember {
        mutableStateOf(visualTransformation != PasswordVisualTransformation())
    }
    val keyboardController = LocalSoftwareKeyboardController.current


    val trailingIcon: (@Composable () -> Unit)? = @Composable {
        Image(
            modifier = Modifier
                .size(24.dp)
                .satoClickEffect(
                    onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }
                ),
            painter = painterResource(id = if (passwordVisibility.value) R.drawable.show_password else R.drawable.hide_password),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.Black)
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                .clip(
                    RoundedCornerShape(8.dp)
                ),
            enabled = isEditable,
            value = curValue.value,
            onValueChange = {
                curValue.value = it
            },
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else visualTransformation,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
            placeholder = {
                placeHolder?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = placeHolder),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledTextColor = Color.Black,
            ),
            minLines = 1,
            maxLines = 1,
            trailingIcon = trailingIcon
        )
    }
}