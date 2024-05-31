package org.satochip.seedkeeper.ui.views.cardinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.ui.components.card.CardAuthenticityTextBox
import org.satochip.seedkeeper.ui.components.shared.EditableField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle
import org.satochip.seedkeeper.ui.theme.SatoGreen

@Composable
fun CardAuthenticity(
    onClick: (CardInformationItems) -> Unit,
) {
    val scrollState = rememberScrollState()

    // Mocked data
    val cardAuthTitle = stringResource(id = R.string.cardAuthSuccessful)
    val cardAuthText = stringResource(id = R.string.cardAuthSuccessfulText)
    val cardAuthUsage = stringResource(id = R.string.cardAuthSuccessfulUsage)
    val curValue = remember {
        mutableStateOf("")
    }

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
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick(CardInformationItems.BACK)
                }
            )
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .verticalScroll(state = scrollState)
            .padding(horizontal = 16.dp)
    ) {
        WelcomeViewTitle()
        Image(
            painter = painterResource(id = R.drawable.ic_sato_small),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .height(150.dp),
            contentScale = ContentScale.FillHeight,
            colorFilter = ColorFilter.tint(SatoGreen)
        )
        CardAuthenticityTextBox(
            cardAuthTitle = cardAuthTitle,
            cardAuthText = cardAuthText,
            cardAuthUsage = cardAuthUsage
        )
        EditableField(
            isEditable = false,
            isIconShown = false,
            curValue = curValue,
            placeHolder = R.string.showCardCertificate,
            containerColor = SatoGreen,
            isClickable = true,
            onClick = {
                onClick(CardInformationItems.SHOW_CARD_CERTIFICATE)
            }
        )
        EditableField(
            isEditable = false,
            isIconShown = false,
            curValue = curValue,
            placeHolder = R.string.showCaCardCertificate,
            containerColor = SatoGreen,
            isClickable = true,
            onClick = {
                onClick(CardInformationItems.SHOW_CA_CERTIFICATE)
            }
        )
    }
}