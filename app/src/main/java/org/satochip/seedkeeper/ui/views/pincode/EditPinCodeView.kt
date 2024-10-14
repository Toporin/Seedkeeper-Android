package org.satochip.seedkeeper.ui.views.pincode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.data.PinCodeStatus
import org.satochip.seedkeeper.ui.components.card.EditPinButtons
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.InputPinField
import org.satochip.seedkeeper.ui.components.shared.rememberImeState

@Composable
fun EditPinCodeView(
    placeholderText: Int,
    pinCode: PinCodeStatus,
    onClick: (CardInformationItems, String?) -> PinCodeStatus
) {
    val curValue = remember {
        mutableStateOf("")
    }
    val pinCodeStatus = remember {
        mutableStateOf(pinCode)
    }
    val title =  R.string.blankTextField
    val messageTitle =  remember { mutableStateOf(0) }
    val message =  remember { mutableStateOf(0) }
    val buttonText =  remember { mutableStateOf(0) }
    val curPinCode = remember {
        mutableStateOf("")
    }
    val imeState = rememberImeState()

    when (pinCodeStatus.value) {
        PinCodeStatus.CURRENT_PIN_CODE -> {
            messageTitle.value = R.string.editPinCode
            message.value = R.string.editPinCodeMessage
            buttonText.value = R.string.next
        }
        PinCodeStatus.INPUT_NEW_PIN_CODE -> {
            curValue.value = ""
            messageTitle.value = R.string.createPinCode
            message.value = R.string.createPinCodeMessage
            buttonText.value = R.string.next
        }
        PinCodeStatus.CONFIRM_PIN_CODE -> {
            messageTitle.value = R.string.confirmPinCode
            message.value = R.string.confirmPinCodeMessage
            buttonText.value = R.string.confirm
        }
        PinCodeStatus.WRONG_PIN_CODE -> {
            messageTitle.value = R.string.wrongPinCode
            message.value = R.string.wrongPinCodeMessage
            buttonText.value = R.string.confirm
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick(CardInformationItems.BACK, null)
                },
                titleText = title
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = messageTitle.value),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = message.value),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                InputPinField(
                    curValue = curValue,
                    placeHolder = placeholderText
                )
                if (imeState.value) {
                    Spacer(modifier = Modifier.height(16.dp))

                    EditPinButtons(
                        pinCodeStatus = pinCodeStatus,
                        curPinCode = curPinCode,
                        curValue = curValue,
                        buttonText = buttonText,
                        onClick = onClick
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditPinButtons(
                    pinCodeStatus = pinCodeStatus,
                    curPinCode = curPinCode,
                    curValue = curValue,
                    buttonText = buttonText,
                    onClick = onClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

