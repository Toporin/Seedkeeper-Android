package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.theme.SatoPurple

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    curValue: MutableState<String>,
    drawableId: Int = R.drawable.edit_icon,
    placeHolder: Int? = null,
    containerColor: Color = SatoPurple.copy(alpha = 0.5f),
    isEmail: Boolean = false,
    textColor: Color = Color.White,
    onClick: (() -> Unit)? = null,
    onValueChange: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = containerColor,
                shape = RoundedCornerShape(50)
            )
            .clickable {
                if (onClick != null) {
                    onClick()
                }
                if (isEditable) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = if (isEditable || isEmail) Arrangement.SpaceBetween else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = curValue.value,
            onValueChange = {
                curValue.value = it
                if (onValueChange != null) {
                    onValueChange()
                }
            },
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                autoCorrect = false,
                keyboardType = KeyboardType.Text
            ),
            enabled = isEditable,
            readOnly = !isEditable,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = if (isEditable || isEmail) TextAlign.Start else TextAlign.Center
            ),
            decorationBox = { innerTextField ->
                placeHolder?.let {
                    Box(
                        modifier = Modifier
                    ) {
                        if (curValue.value.isEmpty()) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart),
                                text = stringResource(id = placeHolder),
                                style = TextStyle(
                                    color = textColor,
                                    fontSize = 16.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                    }
                }
                innerTextField.invoke()
            }
        )
        if (isEditable || isEmail) {
            Image(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = drawableId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(textColor)
            )
        }
    }
}