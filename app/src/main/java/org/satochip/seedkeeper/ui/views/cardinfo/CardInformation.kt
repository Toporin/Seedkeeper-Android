package org.satochip.seedkeeper.ui.views.cardinfo

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.CardAuthenticity
import org.satochip.seedkeeper.EditCardLabelView
import org.satochip.seedkeeper.PinEntryView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ShowCardLogs
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeAction
import org.satochip.seedkeeper.ui.components.card.CardStatusField
import org.satochip.seedkeeper.ui.components.card.InfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.theme.SatoGreen
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun CardInformation(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {

    // Authenticity
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    val cardAuthenticityText = remember {
        mutableStateOf(0)
    }

    val authenticityStatus = viewModel.authenticityStatus
    val cardLabel = viewModel.cardLabel
    val cardAppletVersion = viewModel.getAppletVersionString()
    val seedkeeperStatus = viewModel.getSeedkeeperStatus()
    val cardAuthentikey = viewModel.getAuthentikeyDescription()

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
                titleText = stringResource(R.string.cardInfo),
                onClick = {
                    navController.popBackStack()
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CardStatusField(
                        title = R.string.seedkeeperStatus,
                        cardAppletVersion = cardAppletVersion,
                        cardAuthentikey = cardAuthentikey,
                        seedkeeperStatus = seedkeeperStatus
                    )
                    //Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    InfoField(
                        title = R.string.cardAuthenticity,
                        text = stringResource(id = cardAuthenticityText.value),
                        onClick = {
                            navController.navigate(CardAuthenticity)
                        },
                        containerColor = logoColor.value,
                        isClickable = true,
                        icon = R.drawable.show_password,
                        isPadded = false
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    InfoField(
                        title = R.string.cardLabel,
                        text = cardLabel,
                        onClick = {
                            viewModel.setResultCodeLiveTo(NfcResultCode.NONE)
                            navController.navigate(EditCardLabelView)
                        },
                        containerColor = SatoLightPurple,
                        isClickable = true,
                        icon = R.drawable.edit_icon,
                        isPadded = false
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    InfoField(
                        title = R.string.pinCode,
                        text = stringResource(id = R.string.changePinCode),
                        onClick = {
                            viewModel.setResultCodeLiveTo(NfcResultCode.NONE)
                            navController.navigate(
                                PinEntryView(
                                    pinCodeAction = PinCodeAction.CHANGE_PIN_CODE.name,
                                    isBackupCard = true,
                                )
                            )
                        },
                        containerColor = SatoLightPurple,
                        isClickable = true,
                        icon = R.drawable.edit_icon,
                        isPadded = false
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    InfoField(
                        title = R.string.cardLogs,
                        text = stringResource(id = R.string.showCardLogs),
                        onClick = {
                            navController.navigate(ShowCardLogs)
                        },
                        containerColor = SatoLightPurple,
                        isClickable = true,
                        icon = R.drawable.free_data,
                        isPadded = false
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

        }
    }
}