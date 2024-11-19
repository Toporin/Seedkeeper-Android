package org.satochip.seedkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.satochip.seedkeeper.data.IntentConstants
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.Spinner
import org.satochip.seedkeeper.ui.components.webview.WebViewComponent
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SeedkeeperTheme

class WebviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val url = remember {
                mutableStateOf("")
            }
            val isSpinnerActive = remember {
                mutableStateOf(true)
            }

            intent.getStringExtra(IntentConstants.URL_STRING.name)?.let {
                url.value = it
            }
            intent.removeExtra(IntentConstants.URL_STRING.name)

            SeedkeeperTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = SatoButtonPurple
                        )
                ) {
                    HeaderAlternateRow(
                        modifier = Modifier.background(
                            color = Color.White
                        ),
                        onClick = {
                            finish()
                        }
                    )
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WebViewComponent(
                            url = url,
                            isSpinnerActive = isSpinnerActive
                        )
                        if (isSpinnerActive.value) {
                            Spinner()
                        }
                    }
                }
            }
        }
    }
}