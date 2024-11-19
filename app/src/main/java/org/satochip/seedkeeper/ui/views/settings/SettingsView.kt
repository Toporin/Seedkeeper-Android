package org.satochip.seedkeeper.ui.views.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.FactoryResetView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ShowCardLogs
import org.satochip.seedkeeper.ShowLogsView
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.ui.components.card.InfoField
import org.satochip.seedkeeper.ui.components.settings.*
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun SettingsView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    val scrollState = rememberScrollState()
    val settings = context.getSharedPreferences("seedkeeper", Context.MODE_PRIVATE)
    val starterIntro = remember {
        mutableStateOf(
            settings.getBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, true)
        )
    }
    val debugMode = remember {
        mutableStateOf(settings.getBoolean(SeedkeeperPreferences.DEBUG_MODE.name, false))
    }
    val logsDisabledText = stringResource(id = R.string.logsDisabledText)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderAlternateRow(
                onClick = { navController.popBackStack() },
                titleText = stringResource(R.string.settings)
            )
            Image(
                painter = painterResource(id = R.drawable.tools),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, bottom = 20.dp)
                    .height(170.dp),
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
                    settings.edit().putBoolean(
                        SeedkeeperPreferences.FIRST_TIME_LAUNCH.name,
                        starterIntro.value
                    ).apply()
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
                    settings.edit().putBoolean(
                        SeedkeeperPreferences.DEBUG_MODE.name,
                        debugMode.value
                    ).apply()
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            SatoButton(
                modifier = Modifier
                    .padding(
                        horizontal = 6.dp
                    ),
                onClick = {
                    if (debugMode.value) {
                        navController.navigate(ShowLogsView)
                    } else {
                        Toast.makeText(context, logsDisabledText, Toast.LENGTH_SHORT).show()
                    }
                },
                text = R.string.showLogs,
                buttonColor = if (debugMode.value) SatoButtonPurple else SatoButtonPurple.copy(0.6f),
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 20.dp, bottom = 20.dp)
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(SatoDividerPurple),
            )
            SatoDescriptionField(
                title = R.string.factoryReset,
                //text = R.string.factoryResetText // redundant
                //text = R.string.factoryResetWarning
            )
//            InfoField(
//                title = R.string.factoryResetWarning,
//                titleColor = Color.Red,
//                text = stringResource(id = R.string.resetMyCard),
//                onClick = {
//                    navController.navigate(FactoryResetView)
//                },
//                containerColor = Color.Red,
//                isClickable = true,
//                icon = R.drawable.warning,
//                isPadded = false
//            )
            CardResetButton(
                title = R.string.factoryResetWarning,
                text = stringResource(id = R.string.resetMyCard),
                onClick = {
                    navController.navigate(FactoryResetView)
                },
                containerColor = Color.Red,
                titleColor = Color.Red
            )
            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}