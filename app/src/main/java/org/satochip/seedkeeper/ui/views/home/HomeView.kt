package org.satochip.seedkeeper.ui.views.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.ui.components.home.HomeHeaderRow
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.home.SatoGradientButton
import org.satochip.seedkeeper.ui.components.home.SatoRoundButton

@Composable
fun HomeView(
    onClick: (HomeItems) -> Unit,
    webViewAction: (String) -> Unit
) {
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    // NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.primary)
    ) {
//        Image(
//            painter = painterResource(R.drawable.seedkeeper_background),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxSize()
//                .align(Alignment.BottomCenter),
//            contentScale = ContentScale.FillBounds
//        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeHeaderRow(
                onClick = { homeItems ->
                    onClick(homeItems)
                },
            )
            // SCAN BUTTON
            SatoRoundButton(
                text = R.string.scan
            ) {
                showNfcDialog.value = true // NfcDialog
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