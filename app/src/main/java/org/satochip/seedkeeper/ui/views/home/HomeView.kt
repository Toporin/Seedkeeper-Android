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
import org.satochip.seedkeeper.data.HomeItems
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
                        //onClick(HomeItems.ADD_NEW_SECRET, null)
                        navController.navigate(AddSecretView)
                    },
                    onSecretClick = { secret ->
                        //onClick(HomeItems.OPEN_SECRET, item)
                        // TODO: use secretHeader instead of sid
                        secret.sid.let {
                            viewModel.setCurrentSecret(secret.sid)
                            navController.navigate(
                                MySecretView(
                                    sid = secret.sid,
                                    type = secret.type.name,
                                    label = secret.label,
                                    exportRights = secret.exportRights.value.toInt(),
                                    subType = secret.subtype.toInt()
                                )
                            )
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