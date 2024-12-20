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
import org.satochip.seedkeeper.data.ImportMode
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
import org.satochip.seedkeeper.ui.views.cardinfo.EditCardLabelView
import org.satochip.seedkeeper.ui.views.import.ImportSecretView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.mysecret.ShowSecretView
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
            PinEntryView(
                pinCodeAction = PinCodeAction.SETUP_PIN_CODE.name,
                isBackupCard = false,
            )
        )
    } else if (viewModel.resultCodeLive == NfcResultCode.REQUIRE_SETUP_FOR_BACKUP) {
        SatoLog.d(TAG, "Navigation: Card needs to be setup!")
        navController.navigate(
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
        composable<EditCardLabelView> {
            EditCardLabelView(
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
            ShowSecretView(
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
                importMode = ImportMode.valueOf(args.importMode),
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
object EditCardLabelView
@Serializable
object BackupView
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
data class ImportSecretView(
    val importMode: String,
)

@Serializable
data class PinEntryView (
    val pinCodeAction: String,
    val isBackupCard: Boolean = false,
)
