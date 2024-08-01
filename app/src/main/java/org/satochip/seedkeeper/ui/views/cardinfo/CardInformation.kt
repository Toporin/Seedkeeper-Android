package org.satochip.seedkeeper.ui.views.cardinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.ui.components.card.InfoField
import org.satochip.seedkeeper.ui.components.shared.EditableField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoGreen

@Composable
fun CardInformation(
    authenticityStatus: AuthenticityStatus,
    cardLabel: String,
    cardAppletVersion: String,
    onClick: (CardInformationItems, String?) -> Unit,
) {
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    val cardAuthenticityText = remember {
        mutableStateOf(0)
    }
     when (authenticityStatus) {
        AuthenticityStatus.AUTHENTIC -> {
            cardAuthenticityText.value = R.string.cardIsGenuine
            logoColor.value = SatoGreen
        }
        AuthenticityStatus.NOT_AUTHENTIC -> {
            cardAuthenticityText.value = R.string.cardIsNotGenuine
            logoColor.value = Color.Red
        }
        AuthenticityStatus.UNKNOWN -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderAlternateRow(
                titleText = R.string.cardInfo,
                onClick = {
                    onClick(CardInformationItems.BACK, null)
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    InfoField(
                        text = cardAppletVersion,
                        onClick = {}
                    )
                }
                item {
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf(cardLabel)
                    }
                    EditableField(
                        isEditable = true,
                        isIconShown = true,
                        title = R.string.cardLabel,
                        curValue = curValue,
                        onClick = {
                            onClick(CardInformationItems.EDIT_CARD_LABEL, curValue.value)
                        }
                    )
                }
                item {
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf("")
                    }
                    EditableField(
                        isEditable = false,
                        isIconShown = true,
                        title = R.string.pinCode,
                        curValue = curValue,
                        placeHolder = R.string.changePinCode,
                        isClickable = true,
                        onClick = {
                            onClick(CardInformationItems.EDIT_PIN_CODE, null)
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 16.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(SatoDividerPurple),
                    )
                }
                item {
                    InfoField(
                        title = R.string.cardAuthenticity,
                        text = stringResource(id = cardAuthenticityText.value),
                        onClick = {
                            onClick(CardInformationItems.CARD_AUTHENTICITY, null)
                        },
                        containerColor = logoColor.value,
                        isClickable = true
                    )
                }
            }
        }
    }
}