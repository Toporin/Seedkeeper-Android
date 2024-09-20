package org.satochip.seedkeeper.ui.views.generate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.views.menu.MenuCard

@Composable
fun GenerateDefault(
    generateStatus: MutableState<GenerateStatus>,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.generateASecret,
            text = R.string.generateExplanation
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            MenuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp),
                text = stringResource(id = R.string.password),
                textMessage = stringResource(R.string.generateAPasswordExplanation),
                textAlign = Alignment.TopStart,
                color = SatoDarkPurple,
                drawableId = R.drawable.password_icon,
                onClick = {
                    generateStatus.value =
                        GenerateStatus.LOGIN_PASSWORD
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp),
                text = stringResource(id = R.string.mnemonic),
                textMessage = stringResource(R.string.generateAMnemonicPhraseExplanation),
                textAlign = Alignment.TopStart,
                color = SatoLightPurple,
                drawableId = R.drawable.mnemonic,
                onClick = {
                    generateStatus.value =
                        GenerateStatus.MNEMONIC_PHRASE
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}