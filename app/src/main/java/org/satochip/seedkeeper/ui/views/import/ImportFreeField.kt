package org.satochip.seedkeeper.ui.views.import

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.ui.components.import.InputField
import org.satochip.seedkeeper.ui.components.import.SecretTextField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportFreeField(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    curValueLabel: MutableState<String>,
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

    // secret fields
    val secret = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.importFreeField,
            text = R.string.importFreeFieldMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.enterYourData),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraLight,
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecretTextField(
                curValue = secret,
                isEditable = true,
                isQRCodeEnabled = false,
                minHeight = 250.dp
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            //Import
            SatoButton(
                modifier = Modifier,
                onClick = {
                    if (isClickable(secret, curValueLabel)) {
                        val secretData = SecretData(
                            type = SeedkeeperSecretType.DATA,
                            label = curValueLabel.value,
                            data = secret.value
                        )
                        //isImportInitiated.value = true
                        viewModel.setPasswordData(secretData)
                        showNfcDialog.value = true
                        viewModel.scanCardForAction(
                            activity = context as Activity,
                            nfcActionType = NfcActionType.GENERATE_A_SECRET
                        )

                    }
                },
                text = R.string.importButton,
                buttonColor = if (
                    isClickable(
                        secret,
                        curValueLabel
                    )
                ) SatoButtonPurple else SatoButtonPurple.copy(alpha = 0.6f),
            )
        }
    }
}