package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun SecretTextField(
    modifier: Modifier = Modifier.height(200.dp),
    isEditable: Boolean = false,
    curValue: MutableState<String>,
    containerColor: Color = SatoDividerPurple.copy(alpha = 0.2f),
    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
    copyToClipboard: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordVisibility = remember {
        mutableStateOf(visualTransformation != PasswordVisualTransformation())
    }
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(modifier = Modifier
            .padding(8.dp)
            .width(64.dp)
            .align(Alignment.TopEnd)) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp)
                    .align(Alignment.TopStart)
                    .satoClickEffect(
                        onClick = {
                            copyToClipboard()
                        }
                    ),
                painter = painterResource(id = R.drawable.copy_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .satoClickEffect(
                        onClick = {
                            passwordVisibility.value = !passwordVisibility.value
                        }
                    ),
                painter = painterResource(id = if (passwordVisibility.value) R.drawable.show_password else R.drawable.hide_password),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            enabled = isEditable,
            value = curValue.value,
            onValueChange = {
                curValue.value = it
            },
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            ),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else visualTransformation,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
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
            maxLines = 3,
        )
    }
}