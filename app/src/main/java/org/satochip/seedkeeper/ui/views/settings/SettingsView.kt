package org.satochip.seedkeeper.ui.views.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SettingsItems
import org.satochip.seedkeeper.ui.components.settings.SatoDescriptionField
import org.satochip.seedkeeper.ui.components.settings.SatoToggleButton
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonBlue
import org.satochip.seedkeeper.ui.theme.SatoInactiveTracer

@Composable
fun SettingsView(
    starterIntro: MutableState<Boolean>,
    debugMode: MutableState<Boolean>,
    onClick: (SettingsItems) -> Unit,
) {
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
                onClick = { onClick(SettingsItems.BACK) },
                titleText = R.string.settings
            )
            Image(
                painter = painterResource(id = R.drawable.tools),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, bottom = 20.dp)
                    .height(200.dp),
                contentScale = ContentScale.FillHeight
            )
            SatoDescriptionField(
                title = R.string.showInstructionScreens,
                text = R.string.restartApplication
            )
            SatoToggleButton(
                modifier = Modifier,
                text = R.string.starterIntro,
                isChecked = starterIntro,
                onClick = {
                    onClick(SettingsItems.STARTER_INFO)
                }
            )
            Spacer(modifier = Modifier.height(35.dp))
            SatoDescriptionField(
                title = R.string.debugMode,
                text = R.string.verbouseLogs
            )
            SatoToggleButton(
                modifier = Modifier,
                text = R.string.debugMode,
                isChecked = debugMode,
                onClick = {
                    onClick(SettingsItems.DEBUG_MODE)
                }
            )
            Spacer(modifier = Modifier.height(35.dp))
            SatoButton(
                modifier = Modifier
                    .padding(
                        horizontal = 6.dp
                    ),
                onClick = {
                    if (debugMode.value) {
                        onClick(SettingsItems.SHOW_LOGS)
                    } else {
                        onClick(SettingsItems.SHOW_TOAST)
                    }
                },
                text = R.string.showLogs,
                buttonColor = if (debugMode.value) SatoButtonBlue else SatoInactiveTracer,
            )
        }
    }
}