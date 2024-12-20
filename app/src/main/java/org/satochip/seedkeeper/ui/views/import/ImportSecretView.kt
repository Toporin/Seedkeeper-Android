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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.ImportMode
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ImportSecretView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
    settings: SharedPreferences,
    importMode: ImportMode,
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

        // label
        val curValueLabel = remember {
            mutableStateOf("")
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
            // HEADER (empty)
            HeaderAlternateRow(
                onClick = {
                    navController.popBackStack()
                },
                titleText = stringResource(R.string.blankTextField)
            )
//            // HEADER (with title and message)
//            HeaderAlternateRow(
//                onClick = {
//                    navController.popBackStack()
//                },
//                titleText =
//                when (generateStatus.value) {
//                    GenerateStatus.DEFAULT -> {
//                        stringResource(R.string.importASecret)
//                    }
//                    GenerateStatus.MNEMONIC_PHRASE -> {
//                        stringResource(R.string.blankTextField)
//                    }
//                    GenerateStatus.LOGIN_PASSWORD -> {
//                        stringResource(R.string.blankTextField)
//                    }
//                    GenerateStatus.WALLET_DESCRIPTOR -> {
//                        stringResource(R.string.blankTextField)
//                    }
//                    GenerateStatus.FREE_FIELD -> {
//                        stringResource(R.string.importFreeField)
//                    }
//                    GenerateStatus.HOME -> {
//                        stringResource(R.string.importASecret)
//                    }
//                },
//                message =
//                when (generateStatus.value) {
//                    GenerateStatus.DEFAULT -> {
//                        R.string.importASecret
//                    }
//                    GenerateStatus.MNEMONIC_PHRASE -> {
//                        R.string.blankTextField
//                    }
//                    GenerateStatus.LOGIN_PASSWORD -> {
//                        R.string.blankTextField
//                    }
//                    GenerateStatus.WALLET_DESCRIPTOR -> {
//                        R.string.blankTextField
//                    }
//                    GenerateStatus.FREE_FIELD -> {
//                        R.string.importFreeField
//                    }
//                    GenerateStatus.HOME -> {
//                        R.string.importASecret
//                    }
//                },
//            )

            // IMPORT SECRET OPTIONS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 16.dp, top = 16.dp)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly //SpaceBetween
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
                            curValueLabel = curValueLabel,
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
                            curValueLabel = curValueLabel,
                        )
                    }
                    GenerateStatus.WALLET_DESCRIPTOR -> {
                        isImportInitiated.value = true
                        ImportWalletDescriptor(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            curValueLabel = curValueLabel,
                        )
                    }
                    GenerateStatus.FREE_FIELD -> {
                        isImportInitiated.value = true
                        ImportFreeField(
                            context = context,
                            navController = navController,
                            viewModel = viewModel,
                            curValueLabel = curValueLabel,
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