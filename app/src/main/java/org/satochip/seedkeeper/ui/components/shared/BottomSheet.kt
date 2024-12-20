package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    showSheet: MutableState<Boolean>,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    if (!showSheet.value) {
        return
    } else {
        ModalBottomSheet(
            modifier = modifier,
            containerColor = Color.White,
            sheetState = sheetState,
            onDismissRequest = {
                showSheet.value = !showSheet.value
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            content()
        }
    }
}