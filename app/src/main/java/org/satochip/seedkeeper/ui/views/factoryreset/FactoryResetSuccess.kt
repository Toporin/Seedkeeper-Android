package org.satochip.seedkeeper.ui.views.factoryreset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun FactoryResetSuccess(
    onHomeClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResetCardTextField(
            title = R.string.congratulations,
            text = R.string.resetSuccessful,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            GifImage(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center),
                image = R.drawable.vault
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SatoButton(
            modifier = Modifier
                .padding(
                    horizontal = 6.dp
                ),
            onClick = {
                onHomeClick()
            },
            text = R.string.home,
        )
        Spacer(modifier = Modifier.height(35.dp))
    }
}