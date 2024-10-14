package org.satochip.seedkeeper.ui.views.cardinfo

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.ui.components.card.CardAuthenticityTextBox
import org.satochip.seedkeeper.ui.components.card.CardCertificateField
import org.satochip.seedkeeper.ui.components.card.InfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle
import org.satochip.seedkeeper.ui.theme.SatoGreen
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun CardAuthenticity(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    val scrollState = rememberScrollState()
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    val cardAuthTitle = remember {
        mutableStateOf(R.string.cardAuthSuccessful)
    }
    val cardAuthText = remember {
        mutableStateOf(R.string.cardAuthSuccessfulText)
    }
    val cardAuthUsage = remember {
        mutableStateOf(R.string.cardAuthSuccessfulUsage)
    }
    val isCardCertOpen = remember {
        mutableStateOf(false)
    }
    val clipboardManager = LocalClipboardManager.current
    val copyText = stringResource(id = R.string.copiedToClipboard)

    when (viewModel.authenticityStatus) {
        AuthenticityStatus.AUTHENTIC -> {
            logoColor.value =  SatoGreen
            cardAuthTitle.value = R.string.cardAuthSuccessful
            cardAuthText.value = R.string.cardAuthSuccessfulText
            cardAuthUsage.value = R.string.cardAuthSuccessfulUsage
        }
        AuthenticityStatus.NOT_AUTHENTIC -> {
            logoColor.value =  Color.Red
            cardAuthTitle.value = R.string.cardAuthFailed
            cardAuthText.value = R.string.cardAuthFailedText
            cardAuthUsage.value = R.string.cardAuthFailedUsage
        }
        AuthenticityStatus.UNKNOWN -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HeaderAlternateRow(
            onClick = {
                navController.popBackStack()
            }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
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
                colorFilter = ColorFilter.tint(logoColor.value)
            )
            CardAuthenticityTextBox(
                cardAuthTitle = stringResource(cardAuthTitle.value),
                cardAuthText = stringResource(cardAuthText.value),
                cardAuthUsage = stringResource(cardAuthUsage.value)
            )
            InfoField(
                text = stringResource(id = if (isCardCertOpen.value) R.string.hideCardCertificate else R.string.showCardCertificate),
                onClick = {
                    isCardCertOpen.value = !isCardCertOpen.value
                },
                containerColor = logoColor.value,
                isClickable = true
            )
            if (isCardCertOpen.value) {
                CardCertificateField(
                    certificates = viewModel.getCertificates(),
                    authenticityStatus = viewModel.authenticityStatus,
                    copyToClipboard = { text ->
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}