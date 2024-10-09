package org.satochip.seedkeeper.ui.views.import

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.ImportViewItems
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.isClickable

@Composable
fun ImportFreeField(
    curValueLabel: MutableState<String>,
    secret: MutableState<String>,
    onClick: (ImportViewItems, String?) -> Unit,
    onImportSecret: (SecretData) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.importFreeField,
            text = R.string.importFreeFieldMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            curValue = curValueLabel,
            placeHolder = R.string.label,
            containerColor = SatoPurple.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.enterYourData),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraLight,
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecretTextField(
                curValue = secret,
                isEditable = true,
                isQRCodeEnabled = false,
                minHeight = 250.dp
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            //Import
            SatoButton(
                modifier = Modifier,
                onClick = {
                    if (isClickable(secret, curValueLabel)) {
                        onImportSecret(
                            SecretData(
                                type = SeedkeeperSecretType.DATA,
                                label = curValueLabel.value,
                                data = secret.value
                            )
                        )
                    }
                },
                text = R.string.importButton,
                buttonColor = if (
                    isClickable(
                        secret,
                        curValueLabel
                    )
                ) SatoButtonPurple else SatoButtonPurple.copy(alpha = 0.6f),
            )
        }
    }
}