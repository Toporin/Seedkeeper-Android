package org.satochip.seedkeeper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import org.satochip.seedkeeper.data.FactoryResetStatus
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.data.MenuItems
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeStatus
import org.satochip.seedkeeper.data.PinViewItems
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.SettingsItems
import org.satochip.seedkeeper.parsers.SecretDataParser
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.InfoPopUpDialog
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.addsecret.AddSecretView
import org.satochip.seedkeeper.ui.views.backup.BackupView
import org.satochip.seedkeeper.ui.views.cardinfo.CardAuthenticity
import org.satochip.seedkeeper.ui.views.cardinfo.CardInformation
import org.satochip.seedkeeper.ui.views.editpincode.EditPinCodeView
import org.satochip.seedkeeper.ui.views.factoryreset.FactoryResetView
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
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

private const val TAG = "Navigation"

@Composable
fun Navigation(
    context: Context,
    viewModel: SharedViewModel,
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
                backgroundImage = R.drawable.first_welcome_card,
                onNext = {
                    navController.navigate(SecondWelcomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {},
                onClick = {}
            )
        }
        composable<SecondWelcomeView> {
            WelcomeView(
                title = R.string.seedphraseManager,
                text = R.string.seedphraseManagerInfo,
                colors = listOf(SatoGray, Color.White, SatoGray),
                backgroundImage = R.drawable.second_welcome_card,
                onNext = {
                    navController.navigate(ThirdWelcomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.navigate(FirstWelcomeView) {
                        popUpTo(0)
                    }
                },
                onClick = {}
            )
        }
        composable<ThirdWelcomeView> {
            val linkUrl = stringResource(id = R.string.moreInfoUrl)
            WelcomeView(
                title = R.string.usingNfc,
                text = R.string.usingNfcInfo,
                backgroundImage = R.drawable.third_welcome_screen,
                isFullWidth = true,
                link = linkUrl,
                onNext = {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.navigate(SecondWelcomeView) {
                        popUpTo(0)
                    }
                },
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(linkUrl)
                    )
                    val packageManager = context.packageManager
                    val chooserIntent = Intent.createChooser(intent, "Open with")

                    if (chooserIntent.resolveActivity(packageManager) != null) {
                        context.startActivity(chooserIntent)
                    } else {
                        webviewActivityIntent(
                            url = linkUrl,
                            context = context
                        )
                    }
                }
            )
        }
        composable<HomeView> {
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
                            viewModel.setIsReadyForPinCode()
                            navController.navigate(
                                PinCodeView(
                                    title = R.string.pinCode,
                                    messageTitle = R.string.pinCode,
                                    message = R.string.enterPinCodeText,
                                    placeholderText = R.string.enterPinCode,
                                )
                            )
                        }
                        HomeItems.MENU -> {
                            navController.navigate(MenuView)
                        }
                        HomeItems.SCAN_CARD -> {
                            navController.navigate(
                                PinCodeView(
                                    title = R.string.pinCode,
                                    messageTitle = R.string.pinCode,
                                    message = R.string.enterPinCodeText,
                                    placeholderText = R.string.enterPinCode,
                                )
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
                                        label = secret.label,
                                        exportRights = secret.exportRights.value.toInt(),
                                        subType = secret.subtype.toInt()
                                    )
                                )
                            }
                        }
                    }
                },
                onEditCardLabel = { cardLabel ->
                    showNfcDialog.value = true // NfcDialog
                    viewModel.setupNewCardLabel(cardLabel)
                    viewModel.scanCardForAction(
                        activity = context as Activity,
                        nfcActionType = NfcActionType.EDIT_CARD_LABEL
                    )
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
                        SettingsItems.RESET_CARD -> {
                            navController.navigate(FactoryResetView)
                        }
                    }
                }
            )
        }
        composable<FactoryResetView> {
            val factoryResetStatus = remember {
                mutableStateOf(FactoryResetStatus.DEFAULT)
            }
            val steps = remember {
                mutableStateOf(1)
            }
            LaunchedEffect(viewModel.resultCodeLive) {
                if (viewModel.resultCodeLive == NfcResultCode.CARD_READY_FOR_RESET) {
                    if (viewModel.getCardStatus().protocolVersion == 1) {
                        steps.value = 5
                    }
                    factoryResetStatus.value = FactoryResetStatus.RESET_READY
                }
                if (viewModel.resultCodeLive == NfcResultCode.CARD_RESET &&
                    factoryResetStatus.value == FactoryResetStatus.RESET_READY) {
                    steps.value--
                    if (steps.value == 0) {
                        factoryResetStatus.value = FactoryResetStatus.RESET_SUCCESSFUL
                    }
                }
            }
            FactoryResetView(
                factoryResetStatus = factoryResetStatus,
                steps = steps,
                onBackClick = {
                    navController.popBackStack()
                },
                onCardGetStatusClick = {
                    showNfcDialog.value = true // NfcDialog
                    viewModel.scanCardForAction(
                        activity = context as Activity,
                        nfcActionType = NfcActionType.GET_STATUS
                    )
                },
                onCardResetClick = {
                    showNfcDialog.value = true // NfcDialog
                    viewModel.scanCardForAction(
                        activity = context as Activity,
                        nfcActionType = NfcActionType.RESET_CARD
                    )
                },
                onHomeClick = {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<CardInformation> {
            CardInformation(
                context = context,
                navController = navController,
                viewModel = viewModel,
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
                                if (pinString.length >= 4) {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.setNewPinString(pinString)
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.SETUP_CARD
                                    )
                                }
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
                                if (pinString.length >= 4) {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.setNewPinString(pinString)
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.CHANGE_PIN
                                    )
                                }
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
                if (viewModel.isCardDataAvailable && !viewModel.isReadyForPinCode) {
                    navController.popBackStack()
                }
            }
            PinCodeView (
                title = R.string.blankTextField,
                messageTitle = args.messageTitle,
                message = args.message,
                placeholderText = args.placeholderText,
                isBackupCardScan = args.isBackupCardScan,
                onClick = { item, pinString ->
                    when (item) {
                        PinViewItems.BACK -> {
                            navController.popBackStack()
                        }
                        PinViewItems.CONFIRM -> {
                            pinString?.let {
                                if (pinString.length >= 4) {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.setNewPinString(pinString)
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.SCAN_CARD
                                    )
                                }
                            }
                        }
                        PinViewItems.BACKUP_CARD_SCAN -> {
                            pinString?.let {
                                if (pinString.length >= 4) {
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.setNewPinString(pinString)
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.SCAN_BACKUP_CARD
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        composable<BackupView> {
            BackupView(
                backupStatusState = viewModel.backupStatusState,
                onClick = { item ->
                    when (item) {
                        BackupStatus.DEFAULT -> {
                            viewModel.setBackupStatus(BackupStatus.FIRST_STEP)
                        }
                        BackupStatus.FIRST_STEP -> {
                            viewModel.setIsReadyForPinCode()
                            navController.navigate(
                                PinCodeView(
                                    title = R.string.pinCode,
                                    messageTitle = R.string.pinCode,
                                    message = R.string.enterPinCodeText,
                                    placeholderText = R.string.enterPinCode,
                                    isBackupCardScan = true
                                )
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
                            navController.navigate(HomeView) {
                                popUpTo(0)
                            }
                            viewModel.setBackupStatus(BackupStatus.DEFAULT)
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
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<MySecretView> {
            val args = it.toRoute<MySecretView>()
            val data = remember {
                mutableStateOf<SecretData?>(null)
            }
            LaunchedEffect(viewModel.currentSecretId) {
                if (viewModel.currentSecretId == null) {
                    navController.navigate(HomeView) {
                        popUpTo(0)
                    }
                }
            }
            LaunchedEffect(Unit) {
                data.value = SecretData(
                    label = args.label,
                    type = SeedkeeperSecretType.valueOf(args.type),
                    exportRights = args.exportRights,
                    subType = args.subType
                )
            }
            LaunchedEffect(viewModel.currentSecretObject) {
                viewModel.currentSecretObject?.let { secretObject ->
                    data.value = SecretDataParser().parseByType(
                        seedkeeperSecretType = SeedkeeperSecretType.valueOf(args.type),
                        secretObject = secretObject
                    )
                    data.value?.label = secretObject.secretHeader.label
                    data.value?.subType = secretObject.secretHeader.subtype.toInt()
                }
            }

            MySecretView(
                context = context,
                navController = navController,
                viewModel = viewModel,
                secret = data,
                type = args.type,
                isOldVersion = viewModel.getSeedkeeperStatus() == null,
            )
        }
        composable<ImportSecretView> {
            val args = it.toRoute<ImportSecretView>()
            ImportSecretView(
                context = context,
                navController = navController,
                viewModel = viewModel,
                settings = settings,
                importMode = AddSecretItems.valueOf(args.importMode),
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
data class ImportSecretView(
    val importMode: String,
)
@Serializable
object AddSecretView
@Serializable
object ShowLogsView
@Serializable
object ShowCardLogs
@Serializable
object FactoryResetView

@Serializable
data class MySecretView (
    val sid: Int,
    val type: String,// TODO: SeedKeeperSecretType
    val label: String,
    val exportRights: Int,
    val subType: Int
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
    val isBackupCardScan: Boolean = false,
    val isPinChange: Boolean = false
)