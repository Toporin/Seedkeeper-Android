package org.satochip.seedkeeper.ui.components.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.BottomDrawer

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