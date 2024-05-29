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
import org.satochip.seedkeeper.ui.views.splash.SplashView
import org.satochip.seedkeeper.ui.views.welcome.WelcomeView

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashView()
            LaunchedEffect(Unit) {
                delay(500)
                navController.navigate(FirstWelcome)
            }
        }
        composable<FirstWelcome> {
            WelcomeView(
                title = R.string.welcome,
                text = R.string.welcome_info,
                colors = listOf(Color.White, SatoGray, SatoGray),
                backgroundImage = R.drawable.seedkeeper_background_welcome_first_screen,
                onNext = {
                    navController.navigate(SecondWelcome)
                },
                onBack = {}
            )
        }
        composable<SecondWelcome> {
            WelcomeView(
                title = R.string.seedphrase_manager,
                text = R.string.seedphrase_manager_info,
                colors = listOf(SatoGray, Color.White, SatoGray),
                backgroundImage = R.drawable.seedkeeper_background_welcome_second_screen,
                onNext = {
                    navController.navigate(ThirdWelcome)
                },
                onBack = {
                    navController.navigate(FirstWelcome)
                }
            )
        }
        composable<ThirdWelcome> {
            WelcomeView(
                title = R.string.using_nfc,
                text = R.string.using_nfc_info,
                backgroundImage = R.drawable.seedkeeper_background_welcome_third_screen,
                onNext = {
                    navController.navigate(HomeView)
                },
                onBack = {
                    navController.navigate(SecondWelcome)
                }
            )
        }
        composable<HomeView> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Blue),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeView()
            }
        }
    }
}

@Serializable
object Splash

@Serializable
object FirstWelcome

@Serializable
object SecondWelcome

@Serializable
object ThirdWelcome

@Serializable
object HomeView
