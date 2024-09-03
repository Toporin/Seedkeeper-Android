package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun ButtonsField(
    secret: MutableState<String>,
    curValueLabel: MutableState<String>,
    onGenerateClick: () -> Unit,
    onImportSecret: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        //Generate
        SatoButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                onGenerateClick()
            },
            text = if (secret.value.isNotEmpty()) R.string.regenerate else R.string.generate,
            horizontalPadding = 1.dp
        )
        //Import
        SatoButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                if (isClickable(secret, curValueLabel)) {
                    onImportSecret()
                }
            },
            text = R.string.importButton,
            buttonColor = if (
                isClickable(
                    secret,
                    curValueLabel
                )
            ) SatoButtonPurple else SatoButtonPurple.copy(alpha = 0.6f),
            horizontalPadding = 1.dp
        )
    }
}