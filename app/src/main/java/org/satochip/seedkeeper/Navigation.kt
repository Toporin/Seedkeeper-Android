package org.satochip.seedkeeper

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
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.HomeItems
import org.satochip.seedkeeper.data.MenuItems
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.SettingsItems
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.backup.BackupView
import org.satochip.seedkeeper.ui.views.cardinfo.CardAuthenticity
import org.satochip.seedkeeper.ui.views.cardinfo.CardEditPinCode
import org.satochip.seedkeeper.ui.views.cardinfo.CardInformation
import org.satochip.seedkeeper.ui.views.generate.GenerateView
import org.satochip.seedkeeper.ui.views.home.HomeView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.settings.SettingsView
import org.satochip.seedkeeper.ui.views.splash.SplashView
import org.satochip.seedkeeper.ui.views.welcome.WelcomeView
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun Navigation(
    context: Context
) {
    val clipboardManager = LocalClipboardManager.current
    val navController = rememberNavController()
    val settings = context.getSharedPreferences("seedkeeper", Context.MODE_PRIVATE)
    val startDestination =
        if (settings.getBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, true)) {
            settings.edit().putBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, false).apply()
            FirstWelcomeView
        } else {
            HomeView
        }
    val viewModel = SharedViewModel()

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
            HomeView(
                onClick = { item ->
                    when (item) {
                        HomeItems.CARD_INFO -> {
//                            navController.navigate(CardAuthenticity)
                            navController.navigate(GenerateView)
                        }
                        HomeItems.REFRESH -> {}
                        HomeItems.MENU -> {
                            navController.navigate(MenuView)
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
        composable<MenuView> {
            MenuView(
                onClick = { item ->
                    when (item) {
                        MenuItems.BACK -> {
                            navController.popBackStack()
                        }
                        MenuItems.CARD_INFORMATION -> {
                            navController.navigate(CardInformation)
                        }
                        MenuItems.MAKE_A_BACKUP -> {
                            navController.navigate(BackupView)
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
            val debugMode = remember {
                mutableStateOf(settings.getBoolean(SeedkeeperPreferences.DEBUG_MODE.name, false))
            }
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
                        SettingsItems.SHOW_LOGS -> {}
                    }
                }
            )
        }
        composable<CardInformation> {
            CardInformation(
                onClick = { item ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        CardInformationItems.CARD_AUTHENTICITY -> {
                            navController.navigate(CardAuthenticity)
                        }
                        CardInformationItems.EDIT_PIN_CODE -> {
                            navController.navigate(CardEditPinCodeView)
                        }
                        else -> {}
                    }
                }
            )
        }
        composable<CardAuthenticity> {
            CardAuthenticity(
                onClick = { item ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                }
            )
        }
        composable<CardEditPinCodeView> {
            CardEditPinCode(
                onClick = { item ->
                    when (item) {
                        CardInformationItems.BACK -> {
                            navController.popBackStack()
                        }
                        else -> {}
                    }
                }
            )
        }
        composable<BackupView> {
            BackupView(
                onClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<GenerateView> {
            val copyText = stringResource(id = R.string.copiedToClipboard)
            val selectMoreSets = stringResource(id = R.string.selectMoreSets)
            GenerateView(
                settings = settings,
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
                            return@GenerateView ""
                        }
                        GenerateViewItems.GENERATE_A_PASSWORD -> {
                            return@GenerateView passwordOptions?.let { options ->
                                if (options.isMemorableSelected) {
                                    viewModel.generateMemorablePassword(options)
                                } else {
                                    val password = viewModel.generatePassword(options)
                                    password?.let {
                                        it
                                    } ?: run {
                                        Toast.makeText(context, selectMoreSets, Toast.LENGTH_SHORT)
                                            .show()
                                        ""
                                    }
                                }
                            } ?: run {
                                ""
                            }
                        }
                    }
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
object CardEditPinCodeView
@Serializable
object BackupView
@Serializable
object GenerateView