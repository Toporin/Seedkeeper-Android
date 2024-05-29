package org.satochip.seedkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.satochip.seedkeeper.ui.theme.SatoGray
import org.satochip.seedkeeper.ui.views.home.HomeView
import org.satochip.seedkeeper.ui.views.menu.MenuView
import org.satochip.seedkeeper.ui.views.splash.SplashView
import org.satochip.seedkeeper.ui.views.welcome.WelcomeView

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashView
    ) {
        composable<SplashView> {
            SplashView()
            LaunchedEffect(Unit) {
                delay(500)
                navController.navigate(FirstWelcomeView) {
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
                }
            )
        }
        composable<MenuView> {
            MenuView (
                onClick = {
                    navController.navigate(HomeView) {
                        popUpTo(0)
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
