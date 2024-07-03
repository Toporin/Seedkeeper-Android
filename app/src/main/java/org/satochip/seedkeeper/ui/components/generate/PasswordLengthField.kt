package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoInactiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple

@Composable
fun PasswordLengthField(
    passwordOptions: MutableState<PasswordOptions>
) {
    var sliderPosition by remember { mutableFloatStateOf(passwordOptions.value.passwordLength.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SatoPurple.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.passwordLength),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(0.1f),
                text = passwordOptions.value.passwordLength.toString(),
            )
            Slider(
                modifier = Modifier.weight(1f),
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    passwordOptions.value =
                        passwordOptions.value.copy(passwordLength = sliderPosition.toInt())
                },
                valueRange = 4f..16f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = SatoActiveTracer,
                    inactiveTrackColor = SatoInactiveTracer,
                    disabledThumbColor = Color.Black
                ),
                steps = 16,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ToggleOption(
                    label = "abc",
                    isChecked = passwordOptions.value.isLowercaseSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isLowercaseSelected = it)
                }
            }
            item {
                ToggleOption(
                    label = "ABC",
                    isChecked = passwordOptions.value.isUppercaseSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isUppercaseSelected = it)
                }
            }
            item {
                ToggleOption(
                    label = "123",
                    isChecked = passwordOptions.value.isNumbersSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isNumbersSelected = it)
                }
            }
            item {
                ToggleOption(
                    label = "#$!",
                    isChecked = passwordOptions.value.isSymbolsSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isSymbolsSelected = it)
                }
            }
        }
        //Memorable password
        ToggleOption(
            label = stringResource(id = R.string.memorablePassword) + " ",
            isChecked = passwordOptions.value.isMemorableSelected
        ) {
            passwordOptions.value = passwordOptions.value.copy(isMemorableSelected = it)
        }
    }
}