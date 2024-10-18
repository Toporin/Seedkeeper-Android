package org.satochip.seedkeeper.ui.components.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import kotlinx.coroutines.delay
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.NfcResultCode
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.shared.BottomDrawer
import kotlin.time.Duration.Companion.seconds

private const val TAG = "NfcDialog"

@Composable
fun NfcDialog(
    openDialogCustom: MutableState<Boolean>,
    resultCodeLive: NfcResultCode,
    isConnected: Boolean
) {
    BottomDrawer(
        showSheet = openDialogCustom
    ) {
//        LaunchedEffect(resultCodeLive) {
//            SatoLog.d(TAG, "LaunchedEffect START ${resultCodeLive}")
//            while (resultCodeLive == NfcResultCode.BUSY) {
//                SatoLog.d(TAG, "LaunchedEffect in while delay 2s ${resultCodeLive}")
//                delay(2.seconds)
//            }
//            SatoLog.d(TAG, "LaunchedEffect after while delay ${resultCodeLive}")
//        }
        if (resultCodeLive == NfcResultCode.BUSY) {
            // Busy scanning
            if (isConnected) {
                DrawerScreen(
                    closeSheet = {
                        openDialogCustom.value = !openDialogCustom.value
                    },
                    message = R.string.scanning,
                    image = R.drawable.nfc_scanner,
                    //message = NfcResultCode.Busy.res, // show?
                )
            } else {
                DrawerScreen(
                    closeSheet = {
                        openDialogCustom.value = !openDialogCustom.value
                    },
                    closeDrawerButton = true,
                    title = R.string.readyToScan,
                    image = R.drawable.phone_icon,
                    message = R.string.nfcHoldSeedkeeper
                )
            }
        } else {
            // finished scanning with some result code
            DrawerScreen(
                closeSheet = {
                    openDialogCustom.value = !openDialogCustom.value
                },
                title = resultCodeLive.resTitle,
                image = resultCodeLive.resImage,
                message = resultCodeLive.resMsg,
                colorFilter = if (resultCodeLive.resTitle == R.string.nfcTitleWarning) ColorFilter.tint(
                    Color.Red
                ) else null,
                triesLeft = resultCodeLive.triesLeft
            )
            LaunchedEffect(Unit) {
                // automatically close nfc toast after some delay
                delay(1.seconds)
                openDialogCustom.value = false
            }
        }
    }
}