package org.satochip.seedkeeper.ui.components.import

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
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
            val sliderState = remember {
                SliderState(
                    value = 4f,
                    valueRange = 4f..16f,
                    onValueChangeFinished = {
                        passwordOptions.value =
                            passwordOptions.value.copy(passwordLength = sliderPosition.toInt())
                    }
                )
            }
            LaunchedEffect(sliderState.value) {
                sliderPosition = sliderState.value
                passwordOptions.value =
                    passwordOptions.value.copy(passwordLength = sliderState.value.toInt())
            }

            Slider(
                state = sliderState,
                track = {
                    SliderDefaults.Track(
                        modifier = Modifier.height(4.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = SatoActiveTracer,
                            inactiveTrackColor = SatoInactiveTracer,
                            disabledActiveTrackColor = SatoActiveTracer,
                            disabledInactiveTrackColor = SatoInactiveTracer,
                            disabledThumbColor = Color.Black
                        ),
                        sliderState = sliderState,
                        thumbTrackGapSize = 0.dp,
                    )
                },
                thumb = {
                    Column(
                        modifier = Modifier.offset(y = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(50)
                                ),
                        )

                        Text(
                            modifier = Modifier,
                            text = passwordOptions.value.passwordLength.toString(),
                            color = Color.White
                        )
                    }
                },
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToggleOption(
                    label = "abc",
                    isChecked = passwordOptions.value.isLowercaseSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isLowercaseSelected = it)
                }
                ToggleOption(
                    label = "123",
                    isChecked = passwordOptions.value.isNumbersSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isNumbersSelected = it)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToggleOption(
                    label = "ABC",
                    isChecked = passwordOptions.value.isUppercaseSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isUppercaseSelected = it)
                }
                ToggleOption(
                    label = "#$!",
                    isChecked = passwordOptions.value.isSymbolsSelected
                ) {
                    passwordOptions.value = passwordOptions.value.copy(isSymbolsSelected = it)
                }
            }
            //Memorable password
            ToggleOption(
                modifier = Modifier
                    .fillMaxWidth(),
                label = stringResource(id = R.string.memorablePassword) + " ",
                isChecked = passwordOptions.value.isMemorableSelected
            ) {
                passwordOptions.value = passwordOptions.value.copy(isMemorableSelected = it)
            }
        }
    }
}