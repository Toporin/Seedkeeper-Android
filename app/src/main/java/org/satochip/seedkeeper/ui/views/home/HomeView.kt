package org.satochip.seedkeeper.ui.views.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.seedkeeper.AddSecretView
import org.satochip.seedkeeper.MySecretView
import org.satochip.seedkeeper.PinCodeView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.ui.components.home.HomeHeaderRow
import org.satochip.seedkeeper.ui.components.home.SatoGradientButton
import org.satochip.seedkeeper.ui.components.home.SatoRoundButton
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun HomeView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    val buySeedkeeperUrl = stringResource(id = R.string.buySeedkeeperUrl)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeHeaderRow(
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
            if (viewModel.isCardDataAvailable) {
                SecretsList(
                    cardLabel = viewModel.cardLabel,
                    secretHeaders = viewModel.secretHeaders,
                    addNewSecret = {
                        navController.navigate(AddSecretView)
                    },
                    onSecretClick = { secretHeader ->
                        secretHeader.let {
                            viewModel.updateCurrentSecretHeader(secretHeader)
                            navController.navigate(MySecretView)
                        }
                    },
                )
            } else {
                // SCAN BUTTON
                SatoRoundButton(
                    text = R.string.scan
                ) {
                    navController.navigate(
                        PinCodeView(
                            title = R.string.pinCode,
                            messageTitle = R.string.pinCode,
                            message = R.string.enterPinCodeText,
                            placeholderText = R.string.enterPinCode,
                        )
                    )
                }
                // WEBVIEW
                SatoGradientButton(
                    onClick = {
                        webviewActivityIntent(
                            url = buySeedkeeperUrl,
                            context = context
                        )
                    },
                    text = R.string.noSeedkeeper
                )
            }
        }
    }
}