package org.satochip.seedkeeper.ui.components.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ColorFilter
import kotlinx.coroutines.delay
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.BottomDrawer
import kotlin.time.Duration.Companion.seconds

@Composable
fun NfcDialog(openDialogCustom: MutableState<Boolean>) {

    BottomDrawer(
        showSheet = openDialogCustom
    ) {
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
}