package org.satochip.seedkeeper

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.data.AddSecretItems
import org.satochip.seedkeeper.data.BackupStatus
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.data.ImportViewItems
import org.satochip.seedkeeper.data.MenuItems
import org.satochip.seedkeeper.data.MySecretItems
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeStatus
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.SettingsItems
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.InfoPopUpDialog
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.addsecret.AddSecretView
import org.satochip.seedkeeper.ui.views.backup.BackupView
import org.satochip.seedkeeper.ui.views.cardinfo.CardAuthenticity
import org.satochip.seedkeeper.ui.views.cardinfo.CardInformation
import org.satochip.seedkeeper.ui.views.editpincode.EditPinCodeView
import org.satochip.seedkeeper.ui.views.generate.GenerateView
import org.satochip.seedkeeper.ui.views.home.HomeView
import org.satochip.seedkeeper.ui.views.import.ImportSecretView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.mysecret.MySecretView
import org.satochip.seedkeeper.ui.views.pincode.PinCodeView
import org.satochip.seedkeeper.ui.views.settings.SettingsView
import org.satochip.seedkeeper.ui.views.showcardlogs.ShowCardLogsView
import org.satochip.seedkeeper.ui.views.showlogs.ShowLogsView
import org.satochip.seedkeeper.ui.views.splash.SplashView
import org.satochip.seedkeeper.ui.views.welcome.WelcomeView
import org.satochip.seedkeeper.utils.parseBitcoinDescriptorData
import org.satochip.seedkeeper.utils.parseMasterseedMnemonicCardData
import org.satochip.seedkeeper.utils.parseMnemonicCardData
import org.satochip.seedkeeper.utils.parsePasswordCardData
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

private const val TAG = "Navigation"

@Composable
fun Navigation(
    context: Context
) {
    val clipboardManager = LocalClipboardManager.current
    val navController = rememberNavController()
    val settings = context.getSharedPreferences("seedkeeper", Context.MODE_PRIVATE)
    val copyText = stringResource(id = R.string.copiedToClipboard)
    val debugMode = remember {
        mutableStateOf(settings.getBoolean(SeedkeeperPreferences.DEBUG_MODE.name, false))
    }
    SatoLog.isDebugModeActivated = debugMode.value
    val startDestination =
        if (settings.getBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, true)) {
            settings.edit().putBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, false).apply()
            FirstWelcomeView
        } else {
            HomeView
        }
    val viewModel = SharedViewModel()
    viewModel.setContext(context)

    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    val showInfoDialog = remember { mutableStateOf(false) } // for infoDialog

    // NFC DIALOG
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected
        )
    }

    // INFO DIALOG
    if (showInfoDialog.value) {
        InfoPopUpDialog(
            isOpen = showInfoDialog,
            title = R.string.cardNeedToBeScannedTitle,
            message = R.string.cardNeedToBeScannedMessage
        )
    }

    // FIRST TIME SETUP
    if (viewModel.isSetupNeeded) {
        SatoLog.d(TAG, "Navigation: Card needs to be setup!")
        navController.navigate(
            NewPinCodeView(
                pinCodeStatus = PinCodeStatus.INPUT_NEW_PIN_CODE.name
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = SplashView
    ) {
        composable<SplashView> {
            SplashView()
            LaunchedEffect(Unit) {
                delay(500)
                navController.navigate(startDestination) {
                    popUpTo(0)
                }
            }
        }
        composable<FirstWelcomeView> {
            WelcomeView(
                title = R.string.welcome,
                text = R.string.welcomeInfo,
                colors = listOf(Color.White, SatoGray, SatoGray),
                backgroundImage = R.drawable.seedkeeper_background_welcome_first_screen,
                onNext = {
                    navController.navigate(SecondWelcomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {}
            )
        }
        composable<SecondWelcomeView> {
            WelcomeView(
                title = R.string.seedphraseManager,
                text = R.string.seedphraseManagerInfo,
                colors = listOf(SatoGray, Color.White, SatoGray),
                backgroundImage = R.drawable.seedkeeper_background_welcome_second_screen,
                onNext = {
                    navController.navigate(ThirdWelcomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.navigate(FirstWelcomeView) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<ThirdWelcomeView> {
            WelcomeView(
                title = R.string.usingNfc,
                text = R.string.usingNfcInfo,
                backgroundImage = R.drawable.seedkeeper_background_welcome_third_screen,
                onNext = {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.navigate(SecondWelcomeView) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<HomeView> {
            LaunchedEffect(viewModel.isReadyForPinCode) {
                // PIN CODE
                if (viewModel.isReadyForPinCode) {
                    SatoLog.d(TAG, "Navigation: Card needs to be verified!")
                    navController.navigate(
                        PinCodeView(
                            title = R.string.pinCode,
                            messageTitle = R.string.pinCode,
                            message = R.string.enterPinCode,
                            placeholderText = R.string.enterPinCode,
                            isMultiStep = false
                        )
                    )
                }
            }
            HomeView(
                isCardDataAvailable = viewModel.isCardDataAvailable,
                cardLabel = viewModel.cardLabel,
                secretHeaders = viewModel.secretHeaders,
                authenticityStatus = viewModel.authenticityStatus,
                onClick = { item, secret ->
                    when (item) {
                        HomeItems.CARD_INFO -> {
                            if (viewModel.isCardDataAvailable) {
                                navController.navigate(CardAuthenticity)
                            } else {
                                showInfoDialog.value = !showInfoDialog.value
                            }
                        }
                        HomeItems.REFRESH -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.SCAN_CARD
                            )
                        }
                        HomeItems.MENU -> {
                            navController.navigate(MenuView)
                        }
                        HomeItems.SCAN_CARD -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.SCAN_CARD
                            )
                        }
                        HomeItems.ADD_NEW_SECRET -> {
                            navController.navigate(AddSecretView)
                        }
                        HomeItems.OPEN_SECRET -> {
                            secret?.sid?.let {
                                viewModel.setCurrentSecret(secret.sid)
                                navController.navigate(
                                    MySecretView(
                                        sid = secret.sid,
                                        type = secret.type.name,
                                        label = secret.label
                                    )
                                )
                            }
                        }
                    }
                },
                webViewAction = { link ->
                    webviewActivityIntent(
                        url = link,
                        context = context
                    )
                },
            )
        }
        composable<MenuView> {
            MenuView(
                onClick = { item ->
                    when (item) {
                        MenuItems.BACK -> {
                            navController.popBackStack()
                        }
                        MenuItems.CARD_INFORMATION -> {
                            if (viewModel.isCardDataAvailable) {
                                navController.navigate(CardInformation)
                            } else {
                                showInfoDialog.value = !showInfoDialog.value
                            }
                        }
                        MenuItems.MAKE_A_BACKUP -> {
                            if (viewModel.isCardDataAvailable) {
                                navController.navigate(BackupView)
                            } else {
                                showInfoDialog.value = !showInfoDialog.value
                            }
                        }
                        MenuItems.SETTINGS -> {
                            navController.navigate(SettingsView)
                        }
                    }
                },
                webViewAction = { link ->
                    webviewActivityIntent(
                        url = link,
                        context = context
                    )
                }
            )
        }
        composable<SettingsView> {
            val starterIntro = remember {
                mutableStateOf(
                    settings.getBoolean(
                        SeedkeeperPreferences.FIRST_TIME_LAUNCH.name,
                        true
                    )
                )
            }
            val logsDisabledText = stringResource(id = R.string.logsDisabledText)
            SettingsView(
                starterIntro = starterIntro,
                debugMode = debugMode,
                onClick = { item ->
                    when (item) {
                        SettingsItems.BACK -> {
                            navController.popBackStack()
                        }
                        SettingsItems.STARTER_INFO -> {
                            settings.edit().putBoolean(
                                SeedkeeperPreferences.FIRST_TIME_LAUNCH.name,
                                starterIntro.value
                            ).apply()
                        }
                        SettingsItems.DEBUG_MODE -> {
                            settings.edit().putBoolean(
                                SeedkeeperPreferences.DEBUG_MODE.name,
                                debugMode.value
                            ).apply()
                        }
                        SettingsItems.SHOW_LOGS -> {
                            navController.navigate(ShowLogsView)
                        }
                        SettingsItems.SHOW_TOAST -> {
                            Toast.makeText(context, logsDisabledText, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
        composable<CardInformation> {
            CardInformation(
                authenticityStatus = viewModel.authenticityStatus,
                cardLabel = viewModel.cardLabel,
                cardAppletVersion = viewModel.getAppletVersion(),
                cardStatus = viewModel.getSeedkeeperStatus(),
                onClick = { item, cardLabel ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        CardInformationItems.CARD_AUTHENTICITY -> {
                            navController.navigate(CardAuthenticity)
                        }
                        CardInformationItems.EDIT_PIN_CODE -> {
                            navController.navigate(
                                EditPinCodeView(
                                    pinCodeStatus = PinCodeStatus.CURRENT_PIN_CODE.name
                                )
                            )
                        }
                        CardInformationItems.EDIT_CARD_LABEL -> {
                            if (cardLabel != null) {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setupNewCardLabel(cardLabel)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.EDIT_CARD_LABEL
                                )
                            }
                        }
                        CardInformationItems.SHOW_CARD_LOGS -> {
                            navController.navigate(ShowCardLogs)
                        }
                        else -> {}
                    }
                }
            )
        }
        composable<CardAuthenticity> {
            CardAuthenticity(
                authenticityStatus = viewModel.authenticityStatus,
                certificates = viewModel.getCertificates(),
                onClick = { item ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                },
                copyToClipboard = { text ->
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                }
            )
        }
        composable<NewPinCodeView> {
            val args = it.toRoute<NewPinCodeView>()
            LaunchedEffect(viewModel.isCardDataAvailable) {
                if (viewModel.isCardDataAvailable) {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                }
            }
            EditPinCodeView(
                placeholderText = R.string.enterPinCode,
                pinCode = PinCodeStatus.valueOf(args.pinCodeStatus),
                onClick = { item, pinString ->
                    when (item) {
                        CardInformationItems.CONFIRM -> {
                            pinString?.let {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setNewPinString(pinString)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.SETUP_CARD
                                )
                            }
                        }
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                    return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
                }
            )
        }
        composable<EditPinCodeView> {
            val args = it.toRoute<EditPinCodeView>()
            LaunchedEffect(viewModel.resultCodeLive) {
                when (viewModel.resultCodeLive) {
                    NfcResultCode.PIN_CHANGED -> {
                        navController.navigate(HomeView) {
                            popUpTo(0)
                        }
                    }
                    else -> {}
                }
            }

            val wrongPinText = stringResource(id = R.string.wrongPinCode)
            EditPinCodeView(
                placeholderText = R.string.enterPinCode,
                pinCode = PinCodeStatus.valueOf(args.pinCodeStatus),
                onClick = { item, pinString ->
                    when (item) {
                        CardInformationItems.EDIT_PIN_CODE -> {
                            if (pinString == viewModel.getCurrentPinString()) {
                                return@EditPinCodeView PinCodeStatus.INPUT_NEW_PIN_CODE
                            } else {
                                Toast.makeText(context, wrongPinText, Toast.LENGTH_SHORT).show()
                                return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
                            }
                        }
                        CardInformationItems.CONFIRM -> {
                            pinString?.let {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setNewPinString(pinString)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.CHANGE_PIN
                                )
                            }
                        }
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                    return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
                }
            )
        }
        composable<PinCodeView> {
            val args = it.toRoute<PinCodeView>()
            LaunchedEffect(viewModel.isCardDataAvailable) {
                if (viewModel.isCardDataAvailable) {
                    navController.popBackStack()
                }
            }
            PinCodeView (
                title = args.title,
                messageTitle = args.messageTitle,
                message = args.message,
                placeholderText = args.placeholderText,
                isMultiStep = args.isMultiStep,
                onClick = { item, pinString ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        CardInformationItems.CONFIRM -> {
                            pinString?.let {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.setNewPinString(pinString)
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.VERIFY_PIN
                                )
                            }
                        }
                        else -> {}
                    }
                }
            )
        }
        composable<BackupView> {
            LaunchedEffect(viewModel.isReadyForPinCode) {
                if (viewModel.isReadyForPinCode) {
                    SatoLog.d(TAG, "Navigation: Card needs to be verified!")
                    navController.navigate(
                        PinCodeView(
                            title = R.string.pinCode,
                            messageTitle = R.string.pinCode,
                            message = R.string.enterPinCode,
                            placeholderText = R.string.enterPinCode,
                            isMultiStep = false
                        )
                    )
                }
            }
            BackupView(
                backupStatusState = viewModel.backupStatusState,
                onClick = { item ->
                    when (item) {
                        BackupStatus.DEFAULT -> {
                            viewModel.setBackupStatus(BackupStatus.FIRST_STEP)
                        }
                        BackupStatus.FIRST_STEP -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.SCAN_BACKUP_CARD
                            )
                        }
                        BackupStatus.SECOND_STEP -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.SCAN_MASTER_CARD
                            )
                        }
                        BackupStatus.THIRD_STEP -> {
                            viewModel.setBackupStatus(BackupStatus.FOURTH_STEP)

                        }
                        BackupStatus.FOURTH_STEP -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.TRANSFER_TO_BACKUP
                            )
                        }
                        BackupStatus.FIFTH_STEP -> {
                            viewModel.setBackupStatus(BackupStatus.DEFAULT)
                            navController.popBackStack()
                        }
                    }
                },
                goBack = {
                    when (viewModel.backupStatusState) {
                        BackupStatus.FIRST_STEP -> {
                            viewModel.setBackupStatus(BackupStatus.DEFAULT)
                        }
                        else -> {
                            viewModel.setBackupStatus(BackupStatus.FIRST_STEP)
                        }
                    }
                }
            )
        }
        composable<AddSecretView> {
            AddSecretView(
                onClick = { item ->
                    when (item) {
                        AddSecretItems.GENERATE_A_SECRET -> {
                            navController.navigate(GenerateView)
                        }
                        AddSecretItems.IMPORT_A_SECRET -> {
                            navController.navigate(ImportSecretView)
                        }
                        AddSecretItems.BACK -> {
                            navController.popBackStack()
                        }
                    }
                },
                webViewAction = { link ->
                    webviewActivityIntent(
                        url = link,
                        context = context
                    )
                }
            )
        }
        composable<MySecretView> {
            val args = it.toRoute<MySecretView>()
            val data = remember {
                mutableStateOf<GeneratePasswordData?>(null)
            }
            val retrieveTheSecretFirstText = stringResource(id = R.string.retrieveTheSecretFirst)
            LaunchedEffect(viewModel.currentSecretId) {
                if (viewModel.currentSecretId == null) {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                }
            }
            LaunchedEffect(Unit) {
                data.value = GeneratePasswordData(
                    password = "",
                    login = "",
                    url = "",
                    label = args.label,
                    type = SeedkeeperSecretType.valueOf(args.type)
                )
            }
            LaunchedEffect(viewModel.currentSecretObject) {
                if (viewModel.currentSecretObject != null) {
                    viewModel.currentSecretObject?.let { secretObject ->
                        when (SeedkeeperSecretType.valueOf(args.type)) {
                            SeedkeeperSecretType.MASTERSEED -> {
                                data.value = parseMasterseedMnemonicCardData(secretObject.secretBytes)
                            }
                            SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC -> {
                                data.value = parseMnemonicCardData(secretObject.secretBytes)
                            }
                            SeedkeeperSecretType.PASSWORD -> {
                                data.value = parsePasswordCardData(secretObject.secretBytes)
                            }
                            SeedkeeperSecretType.DATA -> {
                                data.value = parseBitcoinDescriptorData(secretObject.secretBytes)
                            }
                            else -> {}
                        }
                        data.value?.label = secretObject.secretHeader.label
                    }
                }
            }

            MySecretView(
                secret = data,
                type = args.type,
                isOldVersion = viewModel.getSeedkeeperStatus() == null,
                onClick = { item ->
                    when (item) {
                        MySecretItems.SHOW -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.GET_SECRET
                            )
                        }
                        MySecretItems.DELETE -> {
                            showNfcDialog.value = true // NfcDialog
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.DELETE_SECRET
                            )
                        }
                        MySecretItems.BACK -> {
                            viewModel.resetCurrentSecretObject()
                            navController.popBackStack()
                        }
                        MySecretItems.BUY_SEEDKEEPER -> {
                            webviewActivityIntent(
                                url = "https://satochip.io/product/seedkeeper",
                                context = context
                            )
                        }
                    }
                },
                copyToClipboard = { secret ->
                    clipboardManager.setText(AnnotatedString(secret))
                    Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                },
                getSeedQR = { mnemonic ->
                    try {
                        return@MySecretView viewModel.getSeedQr(mnemonic, context)
                    } catch (e: Exception) {
                        Toast.makeText(context, retrieveTheSecretFirstText, Toast.LENGTH_SHORT).show()
                        return@MySecretView ""
                    }
                }
            )
        }
        composable<GenerateView> {
            val selectMoreSets = stringResource(id = R.string.selectMoreSets)
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
            GenerateView(
                settings = settings,
                isImportDone = isImportDone,
                onClick = { item, text, passwordOptions ->
                    when (item) {
                        GenerateViewItems.COPY_TO_CLIPBOARD -> {
                            text?.let {
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                            }
                            return@GenerateView ""
                        }
                        GenerateViewItems.BACK -> {
                            navController.popBackStack()
                            return@GenerateView ""
                        }
                        GenerateViewItems.GENERATE_MNEMONIC_PHRASE -> {

                            return@GenerateView passwordOptions?.let { options ->
                                try {
                                    viewModel.generateMnemonic(passwordOptions.passwordLength)
                                } catch (e: IllegalArgumentException) {
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                    ""
                                }
                            } ?: run {
                                ""
                            }
                        }
                        GenerateViewItems.GENERATE_A_PASSWORD -> {
                            return@GenerateView passwordOptions?.let { options ->
                                if (options.isMemorableSelected) {
                                    viewModel.generateMemorablePassword(options, context)
                                } else {
                                    val password = viewModel.generatePassword(options)
                                    password ?: run {
                                        Toast.makeText(context, selectMoreSets, Toast.LENGTH_SHORT)
                                            .show()
                                        ""
                                    }
                                }
                            } ?: run {
                                ""
                            }
                        }
                        GenerateViewItems.HOME -> {
                            navController.popBackStack()
                            navController.popBackStack()
                            return@GenerateView ""
                        }
                    }
                },
                onImportSecret = { passwordData ->
                    isImportInitiated.value = true
                    viewModel.setPasswordData(passwordData)
                    showNfcDialog.value = true // NfcDialog
                    viewModel.scanCardForAction(
                        activity = context as Activity,
                        nfcActionType = NfcActionType.GENERATE_A_SECRET
                    )
                }
            )
        }
        composable<ImportSecretView> {
            val isImportDone = remember {
                mutableStateOf(false)
            }
            val isImportInitiated = remember {
                mutableStateOf(false)
            }
            val mnemonicValidText = stringResource(id = R.string.mnemonicValidText)
            LaunchedEffect(viewModel.resultCodeLive) {
                if (viewModel.resultCodeLive == NfcResultCode.SECRET_IMPORTED_SUCCESSFULLY && isImportInitiated.value) {
                    isImportDone.value = true
                } else {
                    isImportDone.value = false
                }
            }
            ImportSecretView(
                settings = settings,
                isImportDone = isImportDone,
                onClick = { item, text ->
                    when (item) {
                        ImportViewItems.COPY_TO_CLIPBOARD -> {
                            text?.let {
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                            }
                        }
                        ImportViewItems.HOME -> {
                            navController.popBackStack()
                            navController.popBackStack()
                        }
                        ImportViewItems.BACK -> {
                            navController.popBackStack()
                        }
                    }
                },
                onImportSecret = { passwordData ->
                    isImportInitiated.value = true
                    viewModel.setPasswordData(passwordData)
                    val isMasterSeed = passwordData.type == SeedkeeperSecretType.MASTERSEED
                    val mnemonic = passwordData.mnemonic

                    if (isMasterSeed && mnemonic != null) {
                        if (viewModel.isMnemonicValid(mnemonic)) {
                            showNfcDialog.value = true
                            viewModel.scanCardForAction(
                                activity = context as Activity,
                                nfcActionType = NfcActionType.GENERATE_A_SECRET
                            )
                        } else {
                            Toast.makeText(context, mnemonicValidText, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showNfcDialog.value = true
                        viewModel.scanCardForAction(
                            activity = context as Activity,
                            nfcActionType = NfcActionType.GENERATE_A_SECRET
                        )
                    }
                }
            )
        }
        composable<ShowCardLogs> {
            ShowCardLogsView(
                onClick = {
                    navController.navigateUp()
                },
                cardLogs = viewModel.getCardLogs(),
                copyToClipboard = { logsText ->
                    clipboardManager.setText(AnnotatedString(logsText))
                    Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                }
            )
        }
        composable<ShowLogsView> {
            ShowLogsView(
                onClick = {
                    navController.navigateUp()
                },
                copyToClipboard = { logsText ->
                    clipboardManager.setText(AnnotatedString(logsText))
                    Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Serializable
object SplashView
@Serializable
object FirstWelcomeView
@Serializable
object SecondWelcomeView
@Serializable
object ThirdWelcomeView
@Serializable
object HomeView
@Serializable
object MenuView
@Serializable
object SettingsView
@Serializable
object CardInformation
@Serializable
object CardAuthenticity
@Serializable
object BackupView
@Serializable
object GenerateView
@Serializable
object ImportSecretView
@Serializable
object AddSecretView
@Serializable
object ShowLogsView
@Serializable
object ShowCardLogs

@Serializable
data class MySecretView (
    val sid: Int,
    val type: String,
    val label: String
)

@Serializable
data class EditPinCodeView (
    val pinCodeStatus: String
)

@Serializable
data class NewPinCodeView (
    val pinCodeStatus: String
)

@Serializable
data class PinCodeView (
    val title: Int,
    val messageTitle: Int,
    val message: Int,
    val placeholderText: Int = R.string.enterCurrentPinCode,
    val isMultiStep: Boolean = true,
    val isPinChange: Boolean = false
)