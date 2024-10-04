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
import org.satochip.seedkeeper.data.PinViewItems
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.InputPinField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.rememberImeState

@Composable
fun PinCodeView(
    title: Int,
    messageTitle: Int,
    message: Int,
    placeholderText: Int,
    isBackupCardScan: Boolean,
    onClick: (PinViewItems, String?) -> Unit
) {
    val curValue = remember {
        mutableStateOf("")
    }
    val imeState = rememberImeState()
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
                    onClick(PinViewItems.BACK, null)
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
                    text = stringResource(id = messageTitle),
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
                    text = stringResource(id = message),
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
                    SatoButton(
                        modifier = Modifier,
                        onClick = {
                            if (isBackupCardScan)
                                onClick(PinViewItems.BACKUP_CARD_SCAN, curValue.value)
                            else
                                onClick(PinViewItems.CONFIRM, curValue.value)
                        },
                        text = R.string.confirm
                    )
                }
            }
            Column {
                SatoButton(
                    modifier = Modifier,
                    onClick = {
                        if (isBackupCardScan)
                            onClick(PinViewItems.BACKUP_CARD_SCAN, curValue.value)
                        else
                            onClick(PinViewItems.CONFIRM, curValue.value)
                        },
                        text = R.string.confirm
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }