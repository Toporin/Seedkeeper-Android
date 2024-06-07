package org.satochip.seedkeeper.ui.components.generate

//import androidx.compose.material3.Slider
//import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoInactiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordLengthField() {
    val options = listOf(0, 8, 12, 16)
    var sliderPosition by remember { mutableStateOf(1f) }

    val selectedOption = options[sliderPosition.toInt()]

    var includeLowerCase by remember { mutableStateOf(true) }
    var includeUpperCase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSpecialChars by remember { mutableStateOf(true) }

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
                text = selectedOption.toString(),
            )
            Slider(
                modifier = Modifier.weight(1f),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..(options.size - 1).toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = SatoActiveTracer,
                    inactiveTrackColor = SatoInactiveTracer,
                    disabledThumbColor = Color.Black
                ),
                steps = options.size - 2,
//                thumb = {
//                    Box{
////                        Image(
////                            painterResource(id = R.drawable.circle),
////                            modifier = Modifier.size(32.dp),
////                            contentScale = ContentScale.FillWidth,
////                            contentDescription = null
////                        )
//                        Spacer(
//                            modifier = Modifier
//                                .height(16.dp)
//                                .width(2.dp)
//                                .background(Color.White),
//                        )
//                        Text(
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .offset(y = 24.dp, x = -3.dp),
//                            text = selectedOption.toString(),
//                        )
//                    }
//                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ToggleOption(label = "abc ", isChecked = includeLowerCase) { includeLowerCase = it }
            }
            item {
                ToggleOption(label = "ABC ", isChecked = includeUpperCase) { includeUpperCase = it }
            }
            item {
                ToggleOption(label = "123 ", isChecked = includeNumbers) { includeNumbers = it }
            }
            item {
                ToggleOption(
                    label = "#$! ",
                    isChecked = includeSpecialChars
                ) { includeSpecialChars = it }
            }
        }
    }
}