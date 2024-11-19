package org.satochip.seedkeeper.ui.views.import

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
import org.satochip.seedkeeper.data.ImportMode
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.views.menu.MenuCard

@Composable
fun ImportDefault(
    importMode: ImportMode,
    generateStatus: MutableState<GenerateStatus>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (importMode == ImportMode.IMPORT_A_SECRET) {
            TitleTextField(
                title = R.string.importASecret,
                text = R.string.importASecretMessage
            )
        } else {
            TitleTextField(
                title = R.string.generateASecret,
                text = R.string.generateExplanation
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Column {
            MenuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp),
                text = stringResource(id = R.string.password),
                textMessage = if (importMode == ImportMode.IMPORT_A_SECRET) stringResource(R.string.importAPasswordMessage) else stringResource(R.string.generateAPasswordExplanation),
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
                textMessage = if (importMode == ImportMode.IMPORT_A_SECRET) stringResource(R.string.importAMnemonicPhraseMessage) else stringResource(R.string.generateAMnemonicPhraseExplanation),
                textAlign = Alignment.TopStart,
                color = SatoLightPurple,
                drawableId = R.drawable.mnemonic,
                onClick = {
                    generateStatus.value =
                        GenerateStatus.MNEMONIC_PHRASE
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // these secrets can only be imported, not randomly generated
            if (importMode == ImportMode.IMPORT_A_SECRET) {
                MenuCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp),
                    text = stringResource(id = R.string.descriptor),
                    textMessage = stringResource(R.string.importAWalletDescriptorMessage),
                    textAlign = Alignment.TopStart,
                    color = SatoDarkPurple,
                    drawableId = R.drawable.wallet,
                    onClick = {
                        generateStatus.value =
                            GenerateStatus.WALLET_DESCRIPTOR
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                MenuCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp),
                    text = stringResource(id = R.string.data),
                    textMessage = stringResource(R.string.importFreeFieldMessage),
                    textAlign = Alignment.TopStart,
                    color = SatoLightPurple,
                    drawableId = R.drawable.free_data,
                    onClick = {
                        generateStatus.value =
                            GenerateStatus.FREE_FIELD
                    }
                )
            } // if importMode
        }
    }
}