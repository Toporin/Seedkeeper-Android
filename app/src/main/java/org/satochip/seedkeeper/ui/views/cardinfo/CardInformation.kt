package org.satochip.seedkeeper.ui.views.cardinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.ui.components.shared.EditableField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoGreen

@Composable
fun CardInformation(
    onClick: (CardInformationItems) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(R.drawable.seedkeeper_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderAlternateRow(
                titleText = R.string.cardInfo,
                onClick = {
                    onClick(CardInformationItems.BACK)
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf("Seedkeeper v0.1")
                    }
                    EditableField(
                        isEditable = false,
                        isIconShown = false,
                        title = R.string.cardVersion,
                        curValue = curValue,
                        onClick = {}
                    )
                }
                item {
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf("")
                    }
                    EditableField(
                        isEditable = true,
                        isIconShown = true,
                        title = R.string.cardLabel,
                        curValue = curValue,
                        onClick = {}
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
                            onClick(CardInformationItems.EDIT_PIN_CODE)
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
                    // Mocked data
                    val curValue = remember {
                        mutableStateOf("")
                    }
                    EditableField(
                        isEditable = false,
                        isIconShown = false,
                        title = R.string.cardAuthenticity,
                        curValue = curValue,
                        placeHolder = R.string.cardIsGenuine,
                        containerColor = SatoGreen,
                        isClickable = true,
                        onClick = {
                            onClick(CardInformationItems.CARD_AUTHENTICITY)
                        }
                    )
                }
            }
        }
    }
}