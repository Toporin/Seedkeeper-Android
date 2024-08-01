package org.satochip.seedkeeper.ui.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.ui.components.home.HomeHeaderRow
import org.satochip.seedkeeper.ui.components.home.SatoGradientButton
import org.satochip.seedkeeper.ui.components.home.SatoRoundButton

@Composable
fun HomeView(
    isCardDataAvailable: Boolean,
    cardLabel: String,
    secretHeaders: SnapshotStateList<SeedkeeperSecretHeader?>,
    authenticityStatus: AuthenticityStatus,
    onClick: (HomeItems, SeedkeeperSecretHeader?) -> Unit,
    webViewAction: (String) -> Unit
) {
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
                isCardDataAvailable = isCardDataAvailable,
                authenticityStatus = authenticityStatus,
                onClick = { homeItems ->
                    onClick(homeItems, null)
                },
            )
            if (isCardDataAvailable) {
                SecretsList(
                    cardLabel = cardLabel,
                    secretHeaders = secretHeaders,
                    addNewSecret = {
                        onClick(HomeItems.ADD_NEW_SECRET, null)
                    },
                    onSecretClick = { item ->
                        onClick(HomeItems.OPEN_SECRET, item)
                    }
                )
            } else {
                // SCAN BUTTON
                SatoRoundButton(
                    text = R.string.scan
                ) {
                    onClick(HomeItems.SCAN_CARD, null)
                }
                // WEBVIEW
                SatoGradientButton(
                    onClick = {
                        webViewAction("https://satochip.io/product/seedkeeper/")
                    },
                    text = R.string.noSeedkeeper
                )
            }
        }
    }
}