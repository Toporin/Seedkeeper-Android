package org.satochip.seedkeeper

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.satochip.seedkeeper.services.SatoLog
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import org.satochip.seedkeeper.services.NFCCardService
import org.satochip.seedkeeper.ui.theme.SeedkeeperTheme
import org.satochip.seedkeeper.viewmodels.SharedViewModel

private const val TAG = "MainActivity"
private const val INACTIVITY_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
private const val INACTIVITY_CHECK_INTERVAL_MS = 10 * 1000L

class MainActivity : ComponentActivity() {

    private val viewModel: SharedViewModel by viewModels()

    // time of last user interaction, used to clear card data from memory after inactivity
    private var lastInteractionTime = SystemClock.elapsedRealtime()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Draw behind system bars on all API levels (enforced from API 35).
        // Backgrounds are always light, so force dark system bar icons.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        // check inactivity on resume (e.g. back from background) and periodically while in foreground
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    checkInactivity()
                    delay(INACTIVITY_CHECK_INTERVAL_MS)
                }
            }
        }
        setContent {
            val context = LocalContext.current as Activity
            // Lock screen orientation to portrait (ignored by Android 16+ on large screens, sw >= 600dp;
            // configChanges in the manifest keeps the activity and its state on rotation/resize)
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
                        // Keep content clear of status/navigation bars, display cutout and keyboard,
                        // while the background image above stays full screen.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .safeDrawingPadding()
                        ) {
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

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = SystemClock.elapsedRealtime()
    }

    /**
     * Clears card data (secret list, PIN, card info) from memory if there was no user interaction
     * for INACTIVITY_TIMEOUT_MS, including time spent in background.
     */
    private fun checkInactivity() {
        val inactiveTime = SystemClock.elapsedRealtime() - lastInteractionTime
        if (inactiveTime >= INACTIVITY_TIMEOUT_MS && viewModel.hasCardData() && !viewModel.isCardConnected) {
            SatoLog.d(TAG, "checkInactivity: no interaction for ${inactiveTime / 1000}s, clearing card data")
            viewModel.resetCardData(goHome = true)
        }
    }

    override fun onResume() {
        super.onResume()
        // keep NFC reader mode enabled while the app is in the foreground,
        // so that tapping a card does not launch other NFC apps (tags are ignored when no action is pending)
        NFCCardService.enableReaderMode(this)
    }

    override fun onPause() {
        super.onPause()
        NFCCardService.disableReaderMode(this)
    }
}
