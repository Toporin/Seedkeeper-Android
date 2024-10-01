package org.satochip.seedkeeper.ui.views.factoryreset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.settings.ResetCardTextField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.views.settings.CardResetButton

@Composable
fun FactoryResetDefault(
    isChecked: MutableState<Boolean>,
    onClick: () -> Unit,
    onBackClick: () -> Unit
) {
    ResetCardTextField(
        text = R.string.factoryResetWarningText,
        warning = R.string.allDataErasedWarning,
        subText = R.string.allDataErasedIrreversible,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
            }
        )
        Text(
            modifier = Modifier
                .clickable { isChecked.value = !isChecked.value },
            text = stringResource(id = R.string.checkToProceed),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraLight
            )
        )
    }
    Spacer(modifier = Modifier.height(35.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardResetButton(
            text = stringResource(id = R.string.resetMyCard),
            onClick = {
                if (isChecked.value) {
                    onClick()
                }
            },
            containerColor = if (isChecked.value) Color.Red else Color.Red.copy(0.6f),
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