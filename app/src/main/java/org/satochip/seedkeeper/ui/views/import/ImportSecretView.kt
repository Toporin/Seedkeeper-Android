package org.satochip.seedkeeper.ui.views.import

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AddSecretItems
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.ImportViewItems
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.PopUpDialog
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportSecretView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    settings: SharedPreferences,
    importMode: AddSecretItems,
) {
    val scrollState = rememberScrollState()

    val isImportDone = remember {
        mutableStateOf(false)
    }
    val isImportInitiated = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(viewModel.resultCodeLive) {
        if (viewModel.resultCodeLive == NfcResultCode.SECRET_IMPORTED_SUCCESSFULLY && isImportInitiated.value) {
            isImportDone.value = true
        } else {
            isImportDone.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val generateStatus = remember {
            mutableStateOf(GenerateStatus.DEFAULT)
        }

        // TODO: remove these state and add them below
        val secret = remember {
            mutableStateOf("")
        }
        val curValueLabel = remember {
            mutableStateOf("")
        }
        val curValuePassphrase = remember {
            mutableStateOf("")
        }
        val curValueWalletDescriptor = remember {
            mutableStateOf("")
        }
        val passwordOptions = remember {
            mutableStateOf(
                PasswordOptions()
            )
        }

        if (isImportDone.value) {
            generateStatus.value = GenerateStatus.HOME
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderAlternateRow(
                onClick = {
                    //onClick(ImportViewItems.BACK, null)
                    navController.popBackStack()
                },
                titleText = R.string.blankTextField
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 16.dp, top = 16.dp)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                when (generateStatus.value) {
                    GenerateStatus.DEFAULT -> {
                        ImportDefault(
                            importMode = importMode,
                            generateStatus = generateStatus
                        )
                    }
                    GenerateStatus.MNEMONIC_PHRASE -> {
                        isImportInitiated.value = true
                        ImportMnemonic(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            importMode = importMode,
                        )
                    }
                    GenerateStatus.LOGIN_PASSWORD -> {
                        isImportInitiated.value = true
                        ImportPassword(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            settings = settings,
                            importMode =  importMode,
                        )
                    }
                    GenerateStatus.WALLET_DESCRIPTOR -> {
                        isImportInitiated.value = true
                        ImportWalletDescriptor(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            curValueLabel = curValueLabel,
                            secret = secret,
                        )
                    }
                    GenerateStatus.FREE_FIELD -> {
                        isImportInitiated.value = true
                        ImportFreeField(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            curValueLabel = curValueLabel,
                            secret = secret,
                        )
                    }
                    GenerateStatus.HOME -> {
                        ImportHome(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            curValueLabel = curValueLabel,
                        )
                    }
                }
            }
        }
    }
}