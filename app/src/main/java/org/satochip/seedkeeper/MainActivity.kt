package org.satochip.seedkeeper

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import org.satochip.seedkeeper.ui.theme.SeedkeeperTheme
import org.satochip.seedkeeper.viewmodels.SharedViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current as Activity
            //Lock screen orientation to portrait
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            SeedkeeperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.seedkeeper_background),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter),
                            contentScale = ContentScale.FillBounds
                        )
                        Navigation(
                            context = context,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

