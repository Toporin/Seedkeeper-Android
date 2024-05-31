package org.satochip.seedkeeper

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.satochip.seedkeeper.data.MenuItems
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.SettingsItems
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.home.HomeView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.settings.SettingsView
import org.satochip.seedkeeper.ui.views.splash.SplashView
import org.satochip.seedkeeper.ui.views.welcome.WelcomeView
import org.satochip.seedkeeper.utils.webviewActivityIntent

@Composable
fun Navigation(
    context: Context
) {
    val navController = rememberNavController()
    val settings = context.getSharedPreferences("seedkeeper", Context.MODE_PRIVATE)
    val startDestination =
        if (settings.getBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, true)) {
            settings.edit().putBoolean(SeedkeeperPreferences.FIRST_TIME_LAUNCH.name, false).apply()
            FirstWelcomeView
        } else {
            HomeView
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
            HomeView(
                onMenuClick = {
                    navController.navigate(MenuView)
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
                            navController.navigate(HomeView) {
                                popUpTo(0)
                            }
                        }

                        MenuItems.CARD_INFORMATION -> {}
                        MenuItems.MAKE_A_BACKUP -> {}
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
                            navController.navigate(MenuView) {
                                popUpTo(0)
                            }
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
