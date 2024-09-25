package org.satochip.seedkeeper.ui.views.factoryreset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.views.settings.CardResetButton

@Composable
fun FactoryResetReady(
    steps: MutableState<Int>,
    onClick: () -> Unit,
    onBackClick: () -> Unit
) {
    ResetCardTextField(
        text = R.string.currentlyPerformingReset,
        subText = R.string.clickOnResetToContinue,
        subTextDescription = R.string.orCancelToQuit
    )
    Spacer(modifier = Modifier.height(35.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (steps.value > 1) {
            Text(
                text = stringResource(id = R.string.stepsRemaining) + " ${steps.value}",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        CardResetButton(
            text = stringResource(id = R.string.sendResetCommand),
            onClick = {
                onClick()
            },
            containerColor = Color.Red,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SatoButton(
            modifier = Modifier
                .padding(
                    horizontal = 6.dp
                ),
            onClick = {
                onBackClick()
            },
            text = R.string.cancel,
        )
        Spacer(modifier = Modifier.height(35.dp))
    }
}