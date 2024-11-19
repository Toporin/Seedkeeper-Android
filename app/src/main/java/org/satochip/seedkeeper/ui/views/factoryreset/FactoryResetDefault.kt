package org.satochip.seedkeeper.ui.views.factoryreset

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.settings.CardResetButton
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun FactoryResetDefault(
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

    val isChecked = remember {
        mutableStateOf(false)
    }

    ResetCardTextField(
        text = R.string.factoryResetWarningText,
        warning = R.string.allDataErasedWarning,
        subText = R.string.allDataErasedIrreversible,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
            }
        )
        Text(
            modifier = Modifier
                .clickable { isChecked.value = !isChecked.value },
            text = stringResource(id = R.string.checkToProceed),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraLight
            )
        )
    }
    Spacer(modifier = Modifier.height(35.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardResetButton(
            text = stringResource(id = R.string.resetMyCard),
            onClick = {
                if (isChecked.value) {
                    factoryResetStatus.value = FactoryResetStatus.RESET_READY
                }
            },
            containerColor = if (isChecked.value) Color.Red else Color.Red.copy(0.6f),
        )
        Spacer(modifier = Modifier.height(12.dp))
        SatoButton(
            modifier = Modifier
                .padding(
                    horizontal = 6.dp
                ),
            onClick = {
                factoryResetStatus.value = FactoryResetStatus.RESET_CANCELLED
            },
            text = R.string.cancel,
        )
        Spacer(modifier = Modifier.height(35.dp))

    }
}