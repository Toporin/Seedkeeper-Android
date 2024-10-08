package org.satochip.seedkeeper.ui.components.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.data.PinCodeStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun EditPinButtons(
    pinCodeStatus: MutableState<PinCodeStatus>,
    curPinCode: MutableState<String>,
    curValue: MutableState<String>,
    buttonText: MutableState<Int>,
    onClick: (CardInformationItems, String?) -> PinCodeStatus
) {
    if (pinCodeStatus.value == PinCodeStatus.WRONG_PIN_CODE) {
        //Back
        SatoButton(
            onClick = {
                pinCodeStatus.value = PinCodeStatus.INPUT_NEW_PIN_CODE
            },
            buttonColor = Color.Transparent,
            textColor = Color.Black,
            text = R.string.restartTheSetup
        )
    }
    SatoButton(
        modifier = Modifier,
        onClick = {
            when (pinCodeStatus.value) {
                PinCodeStatus.CURRENT_PIN_CODE -> {
                    pinCodeStatus.value = onClick(CardInformationItems.EDIT_PIN_CODE, curValue.value)
                }
                PinCodeStatus.INPUT_NEW_PIN_CODE -> {
                    curPinCode.value = curValue.value
                    pinCodeStatus.value = PinCodeStatus.CONFIRM_PIN_CODE
                    curValue.value = ""
                }
                PinCodeStatus.CONFIRM_PIN_CODE -> {
                    if (curPinCode.value == curValue.value) {
                        onClick(CardInformationItems.CONFIRM, curValue.value)
                    } else {
                        curValue.value = ""
                        pinCodeStatus.value = PinCodeStatus.WRONG_PIN_CODE
                    }
                }
                PinCodeStatus.WRONG_PIN_CODE -> {
                    if (curPinCode.value == curValue.value) {
                        onClick(CardInformationItems.CONFIRM, curValue.value)
                    } else {
                        curValue.value = ""
                    }
                }
            }
        },
        text = buttonText.value
    )
}