package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.MySecretStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonBlue
import org.satochip.seedkeeper.ui.theme.SatoInactiveTracer

@Composable
fun SecretButtonsField(
    mySecretStatus: MutableState<MySecretStatus>,
    isSecretShown: MutableState<Boolean>,
    type: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val secretType = SeedkeeperSecretType.valueOf(type)
        when (secretType) {
            SeedkeeperSecretType.MASTERSEED, SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC  -> {
                SatoButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        mySecretStatus.value = MySecretStatus.SEED
                    },
                    text = R.string.seed,
                    image = R.drawable.seed_icon,
                    horizontalPadding = 1.dp
                )
                SatoButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onClick()
                        if (isSecretShown.value) {
                            mySecretStatus.value = MySecretStatus.SEED_QR
                        }
                    },
                    text = R.string.seedQR,
                    image = R.drawable.seedqr_icon,
                    horizontalPadding = 1.dp,
                    buttonColor = if (isSecretShown.value) SatoButtonBlue else SatoInactiveTracer
                )
//                todo: logic should be changed
//                SatoButton(
//                    modifier = Modifier,
//                    onClick = {
//                        mySecretStatus.value = MySecretStatus.X_PUB
//                    },
//                    text = R.string.xpub,
//                    image = R.drawable.xpub_icon,
//                    horizontalPadding = 2.dp
//                )
            }
//            SeedkeeperSecretType.PASSWORD -> {
//
//            }
//            SeedkeeperSecretType.DATA -> {
//
//            }
            else -> {}
        }
    }
}