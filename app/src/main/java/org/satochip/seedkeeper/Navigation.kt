package org.satochip.seedkeeper

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.satochip.seedkeeper.data.AddSecretItems
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.data.PinCodeAction
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.addsecret.AddSecretView
import org.satochip.seedkeeper.ui.views.backup.BackupView
import org.satochip.seedkeeper.ui.views.cardinfo.CardAuthenticity
import org.satochip.seedkeeper.ui.views.cardinfo.CardInformation
import org.satochip.seedkeeper.ui.views.factoryreset.FactoryResetView
import org.satochip.seedkeeper.ui.views.home.HomeView
import org.satochip.seedkeeper.ui.views.import.ImportSecretView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.mysecret.MySecretView
import org.satochip.seedkeeper.ui.views.pincode.PinEntryView
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
    val navController = rememberNavController()
    val settings = context.getSharedPreferences("seedkeeper", Context.MODE_PRIVATE)
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

    // NFC DIALOG
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected
        )
    }

    // FIRST TIME SETUP
    if (viewModel.resultCodeLive == NfcResultCode.REQUIRE_SETUP) {
        SatoLog.d(TAG, "Navigation: Card needs to be setup!")
        navController.navigate(
//            NewPinCodeView(
//                pinCodeStatus = PinCodeStatus.INPUT_NEW_PIN_CODE.name,
//                isBackupCard = false,
//            )
            PinEntryView(
                pinCodeAction = PinCodeAction.SETUP_PIN_CODE.name,
                isBackupCard = false,
            )
        )
    } else if (viewModel.resultCodeLive == NfcResultCode.REQUIRE_SETUP_FOR_BACKUP) {
        SatoLog.d(TAG, "Navigation: Card needs to be setup!")
        navController.navigate(
//            NewPinCodeView(
//                pinCodeStatus = PinCodeStatus.INPUT_NEW_PIN_CODE.name,
//                isBackupCard = true,
//            )
            PinEntryView(
                pinCodeAction = PinCodeAction.SETUP_PIN_CODE.name,
                isBackupCard = true,
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
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<MenuView> {
            MenuView(
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<SettingsView> {
            SettingsView(
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<FactoryResetView> {
            FactoryResetView(
                context = context,
                navController = navController,
                viewModel = viewModel,
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
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<PinEntryView> {
            val args = it.toRoute<PinEntryView>()
            PinEntryView(
                context = context,
                navController = navController,
                viewModel = viewModel,
                pinCodeAction = PinCodeAction.valueOf(args.pinCodeAction),
                isBackupCard = args.isBackupCard,
            )
        }
//        composable<NewPinCodeView> {
//            val args = it.toRoute<NewPinCodeView>()
//            LaunchedEffect(viewModel.resultCodeLive) {
//                if (viewModel.resultCodeLive == NfcResultCode.CARD_SETUP_SUCCESSFUL) {
//                    navController.navigate(HomeView) {
//                        popUpTo(0)
//                    }
//                } else if (viewModel.resultCodeLive == NfcResultCode.CARD_SETUP_FOR_BACKUP_SUCCESSFUL) {
//                    // TODO redirect to BackupView?
//                } else {
//                    print("NewPinCodeView TODO resultCodeLive: ${viewModel.resultCodeLive}") // TODO something?
//                }
//            }
//            EditPinCodeView(
//                placeholderText = R.string.enterPinCode,
//                pinCode = PinCodeStatus.valueOf(args.pinCodeStatus),
//                isBackupCard = args.isBackupCard,
//                onClick = { item, pinString ->
//                    when (item) {
//                        CardInformationItems.CONFIRM -> {
//                            pinString?.let {
//                                if (pinString.length >= 4) {// todo check max length in bytes + error mgmt
//                                    showNfcDialog.value = true // NfcDialog
//                                    viewModel.setNewPinString(pinString)
//                                    viewModel.scanCardForAction(
//                                        activity = context as Activity,
//                                        nfcActionType = if (args.isBackupCard) NfcActionType.SETUP_CARD_FOR_BACKUP else NfcActionType.SETUP_CARD
//                                    )
//                                }
//                            }
//                        }
//                        CardInformationItems.BACK -> {
//                            navController.popBackStack()
//                        }
//                        else -> {}
//                    }
//                    return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
//                }
//            )
//        }
//        composable<EditPinCodeView> {
//            val args = it.toRoute<EditPinCodeView>()
//            LaunchedEffect(viewModel.resultCodeLive) {
//                when (viewModel.resultCodeLive) {
//                    NfcResultCode.PIN_CHANGED -> {
//                        navController.navigate(HomeView) {
//                            popUpTo(0)
//                        }
//                    }
//                    else -> {}
//                }
//            }
//
//            val wrongPinText = stringResource(id = R.string.wrongPinCode)
//            EditPinCodeView(
//                placeholderText = R.string.enterPinCode,
//                pinCode = PinCodeStatus.valueOf(args.pinCodeStatus),
//                isBackupCard = false,
//                onClick = { item, pinString ->
//                    when (item) {
//                        CardInformationItems.EDIT_PIN_CODE -> {
//                            if (pinString == viewModel.getCurrentPinString()) {
//                                return@EditPinCodeView PinCodeStatus.INPUT_NEW_PIN_CODE
//                            } else {
//                                Toast.makeText(context, wrongPinText, Toast.LENGTH_SHORT).show()
//                                return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
//                            }
//                        }
//                        CardInformationItems.CONFIRM -> {
//                            pinString?.let {
//                                if (pinString.length >= 4) {// todo check byte length <=16
//                                    showNfcDialog.value = true // NfcDialog
//                                    viewModel.setNewPinString(pinString)
//                                    viewModel.scanCardForAction(
//                                        activity = context as Activity,
//                                        nfcActionType = NfcActionType.CHANGE_PIN
//                                    )
//                                }
//                            }
//                        }
//                        CardInformationItems.BACK -> {
//                            navController.popBackStack()
//                        }
//                        else -> {}
//                    }
//                    return@EditPinCodeView PinCodeStatus.CURRENT_PIN_CODE
//                }
//            )
//        }
//        composable<PinCodeView> {
//            val args = it.toRoute<PinCodeView>()
//            PinCodeView (
//                context = context,
//                navController = navController,
//                viewModel = viewModel,
//                title = R.string.blankTextField,
//                messageTitle = args.messageTitle,
//                message = args.message,
//                placeholderText = args.placeholderText,
//                isBackupCardScan = args.isBackupCardScan,
//            )
//        }
        composable<BackupView> {
            BackupView(
                context = context,
                navController = navController,
                viewModel = viewModel,
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
            MySecretView(
                context = context,
                navController = navController,
                viewModel = viewModel,
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
                context = context,
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable<ShowLogsView> {
            ShowLogsView(
                context = context,
                navController = navController,
                viewModel = viewModel,
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
object MySecretView

@Serializable
data class PinEntryView (
    val pinCodeAction: String,
    val isBackupCard: Boolean = false,
)

//@Serializable
//data class EditPinCodeView ( // TODO deprecate
//    val pinCodeStatus: String
//)

//@Serializable
//data class NewPinCodeView ( // TODO deprecate
//    val pinCodeStatus: String,
//    val isBackupCard: Boolean = false,
//)

//@Serializable
//data class PinCodeView ( // TODO deprecate
//    val title: Int, // todo integrate as always the same?
//    val messageTitle: Int, // todo integrate as always the same?
//    val message: Int, // todo integrate as always the same?
//    val placeholderText: Int = R.string.enterCurrentPinCode, // todo integrate as always the same?
//    val isBackupCardScan: Boolean = false,
//)