package org.satochip.seedkeeper.ui.views.factoryreset

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.settings.CardResetButton
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun FactoryResetView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    val factoryResetStatus = remember {
        mutableStateOf(FactoryResetStatus.DEFAULT)
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                HeaderAlternateRow(
                    onClick = { navController.popBackStack() },
                    titleText = R.string.reset
                )
                if (factoryResetStatus.value != FactoryResetStatus.RESET_SUCCESSFUL) {
                    Image(
                        painter = painterResource(id = R.drawable.tools),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 17.dp, bottom = 20.dp)
                            .height(170.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
            when(factoryResetStatus.value) {
                FactoryResetStatus.DEFAULT -> {
                    FactoryResetDefault(
                        context = context,
                        navController = navController,
                        viewModel = viewModel,
                        factoryResetStatus = factoryResetStatus,
                    )
                }
                FactoryResetStatus.RESET_READY -> {
                    FactoryResetReady(
                        context = context,
                        navController = navController,
                        viewModel = viewModel,
                        factoryResetStatus = factoryResetStatus,
                    )
                }
                FactoryResetStatus.RESET_SUCCESSFUL -> {
                    FactoryResetSuccess(
                        context = context,
                        navController = navController,
                        viewModel = viewModel,
                    )
                }

                FactoryResetStatus.RESET_CANCELLED -> {
                    FactoryResetCancel(
                        context = context,
                        navController = navController,
                        viewModel = viewModel,
                    )
                }

            }
        }
    }
}