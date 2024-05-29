package org.satochip.seedkeeper.ui.views.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle

@Composable
fun SplashView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(R.drawable.seedkeeper_background_welcome),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WelcomeViewTitle(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 64.dp)
            )
        }
    }
}


@Preview
@Composable
private fun SplashViewPreview() {
    SplashView()
}