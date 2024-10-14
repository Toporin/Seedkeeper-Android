package org.satochip.seedkeeper.ui.views.factoryreset

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.FactoryResetStatus
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.settings.CardResetButton
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun FactoryResetReady(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    factoryResetStatus: MutableState<FactoryResetStatus>,
) {
    // NFC dialog
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected
        )
    }

    val steps = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(viewModel.resultCodeLive) {
        if (viewModel.resultCodeLive == NfcResultCode.CARD_RESET &&
            factoryResetStatus.value == FactoryResetStatus.RESET_READY)
        {
            factoryResetStatus.value = FactoryResetStatus.RESET_SUCCESSFUL
        } else if (viewModel.resultCodeLive == NfcResultCode.CARD_RESET_CANCELLED &&
            factoryResetStatus.value == FactoryResetStatus.RESET_READY)
        {
            factoryResetStatus.value = FactoryResetStatus.RESET_CANCELLED
        } else if (viewModel.resultCodeLive == NfcResultCode.CARD_RESET_SENT &&
            factoryResetStatus.value == FactoryResetStatus.RESET_READY)
        {
            steps.value = viewModel.resultCodeLive.triesLeft ?: 0
            SatoLog.e("FactoryResetReady", "Remaining steps: ${steps.value}")
        }
    }

    ResetCardTextField(
        text = R.string.currentlyPerformingReset,
        subText = R.string.clickOnResetToContinue,
        subTextDescription = R.string.orCancelToQuit
    )
    Spacer(modifier = Modifier.height(35.dp))

    if (steps.value >= 1) {
        Text(
            text = stringResource(id = R.string.stepsRemaining) + " ${steps.value}",
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CardResetButton(
            text = stringResource(id = R.string.sendResetCommand),
            onClick = {
                //onClick()
                showNfcDialog.value = true // NfcDialog
                viewModel.scanCardForAction(
                    activity = context as Activity,
                    nfcActionType = NfcActionType.RESET_CARD
                )
            },
            containerColor = Color.Red,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SatoButton(
            modifier = Modifier
                .padding(
                    horizontal = 6.dp
                ),
            onClick = {
                //navController.popBackStack()
                factoryResetStatus.value = FactoryResetStatus.RESET_CANCELLED
            },
            text = R.string.cancel,
        )
        Spacer(modifier = Modifier.height(35.dp))
    }
}