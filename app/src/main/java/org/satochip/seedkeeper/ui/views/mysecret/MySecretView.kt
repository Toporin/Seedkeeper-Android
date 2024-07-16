package org.satochip.seedkeeper.ui.views.mysecret

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretObject
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.MySecretItems
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.mysecret.SecretInfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField

@Composable
fun MySecretView(
    secret: MutableState<GeneratePasswordData?>,
    type: String,
    onClick: (MySecretItems) -> Unit
) {
    val scrollState = rememberScrollState()
    val secretText = remember {
        mutableStateOf("")
    }
    if (type == SeedkeeperSecretType.BIP39_MNEMONIC.name)
        secret.value?.mnemonic?.let { mnemonic ->
            secretText.value = mnemonic
        }
    else
        secret.value?.password?.let { password ->
            secretText.value = password
        }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderAlternateRow(
                titleText = R.string.mySecret,
                onClick = {
                    onClick(MySecretItems.BACK)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp, top = 16.dp)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TitleTextField(
                    title = R.string.menageSecret,
                    text = R.string.menageSecretMessage
                )
                Column {
                    SecretInfoField(
                        title = R.string.label,
                        text = "${secret.value?.label}"
                    )

                    GetSpecificSecretInfoFields(
                        type = type,
                        secret = secret
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        item {
                            SatoButton(
                                modifier = Modifier,
                                onClick = {},
                                text = R.string.seed,
                                image = R.drawable.seed_icon,
                                horizontalPadding = 2.dp
                            )
                        }
                        item {
                            SatoButton(
                                modifier = Modifier,
                                onClick = {},
                                text = R.string.seedQR,
                                image = R.drawable.seedqr_icon,
                                horizontalPadding = 2.dp
                            )
                        }
                        item {
                            SatoButton(
                                modifier = Modifier,
                                onClick = {},
                                text = R.string.xpub,
                                image = R.drawable.xpub_icon,
                                horizontalPadding = 2.dp
                            )
                        }
                    }
                }

                SecretTextField(
                    curValue = secretText,
                    copyToClipboard = {
//                        onClick(GenerateViewItems.COPY_TO_CLIPBOARD, secret.value, null)
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onClick(MySecretItems.DELETE)
                        },
                        text = R.string.deleteSecret,
                        image = R.drawable.delete_icon
                    )
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = R.string.showSecret,
                        image = R.drawable.show_password
                    )
                }
            }
        }
    }
}

@Composable
fun GetSpecificSecretInfoFields(
    type: String,
    secret: MutableState<GeneratePasswordData?>,
) {
    if (type == SeedkeeperSecretType.BIP39_MNEMONIC.name) {
        SecretInfoField(
            title = R.string.mnemonicSize,
            text = "${secret.value?.size}"
        )

        SecretInfoField(
            title = R.string.passphrase,
            text = "${secret.value?.password}"
        )
    } else {
        SecretInfoField(
            title = R.string.login,
            text = "${secret.value?.login}"
        )

        SecretInfoField(
            title = R.string.url,
            text = "${secret.value?.url}"
        )
    }
}