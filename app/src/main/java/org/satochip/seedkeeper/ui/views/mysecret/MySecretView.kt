package org.satochip.seedkeeper.ui.views.mysecret

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.data.MySecretItems
import org.satochip.seedkeeper.data.MySecretStatus
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.mysecret.GetSpecificSecretInfoFields
import org.satochip.seedkeeper.ui.components.mysecret.NewSeedkeeperPopUpDialog
import org.satochip.seedkeeper.ui.components.mysecret.SecretButtonsField
import org.satochip.seedkeeper.ui.components.mysecret.SecretImageField
import org.satochip.seedkeeper.ui.components.mysecret.SecretInfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple

@Composable
fun MySecretView(
    secret: MutableState<SecretData?>,
    type: String,
    isOldVersion: Boolean,
    onClick: (MySecretItems) -> Unit,
    getSeedQR: (String) -> String,
    copyToClipboard: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val secretText = remember {
        mutableStateOf("")
    }
    val seedQR = remember {
        mutableStateOf("")
    }
    val isSecretShown = remember {
        mutableStateOf(false)
    }
    val mySecretStatus = remember {
        mutableStateOf(MySecretStatus.SEED)
    }
    val isPopUpOpened = remember {
        mutableStateOf(false)
    }
    if (isPopUpOpened.value) {
        NewSeedkeeperPopUpDialog(
            isOpen = isPopUpOpened,
            title = R.string.buySeedkeeper,
            onClick = {
                onClick(MySecretItems.BUY_SEEDKEEPER)
            }
        )
    }
    if (secretText.value.isNotEmpty()) {
        isSecretShown.value = true
    }

    when (SeedkeeperSecretType.valueOf(type)) {
        SeedkeeperSecretType.MASTERSEED, SeedkeeperSecretType.BIP39_MNEMONIC, SeedkeeperSecretType.ELECTRUM_MNEMONIC -> {
            if (secret.value?.subType != 0) {
                secret.value?.mnemonic?.let { mnemonic ->
                    secretText.value = mnemonic
                }
            } else {
                secret.value?.password?.let { password ->
                    secretText.value = password
                }
            }
        }
        SeedkeeperSecretType.PASSWORD -> {
            secret.value?.password?.let { password ->
                secretText.value = password
            }
        }
        SeedkeeperSecretType.DATA, SeedkeeperSecretType.WALLET_DESCRIPTOR -> {
            secret.value?.descriptor?.let { descriptor ->
                secretText.value = descriptor
            }
        }
        SeedkeeperSecretType.PUBKEY, SeedkeeperSecretType.SECRET_2FA -> {
            secret.value?.password?.let { password ->
                secretText.value = password
            }
        }
        else -> {}
    }
    BackHandler {
        onClick(MySecretItems.BACK)
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
                    .padding(bottom = 32.dp)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.manageSecretMessage),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    SecretInfoField(
                        title = R.string.label,
                        text = secret.value?.label ?: ""
                    )
                    GetSpecificSecretInfoFields(
                        type = type,
                        secret = secret
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SecretButtonsField(
                        mySecretStatus = mySecretStatus,
                        type = type,
                        isSecretShown = isSecretShown,
                        onClick = {
                            if (isSecretShown.value && seedQR.value.isEmpty()) {
                                seedQR.value = getSeedQR(secretText.value)
                            }
                        }
                    )
                }
                when (mySecretStatus.value) {
                    MySecretStatus.SEED -> {
                        SecretTextField(
                            curValue = secretText,
                            minHeight = 250.dp,
                            copyToClipboard = {
                                copyToClipboard(secretText.value)
                            }
                        )
                    }
                    MySecretStatus.SEED_QR -> {
                        SecretImageField(
                            minHeight = 250.dp,
                            qrCodeString = seedQR
                        )
                    }
                    MySecretStatus.X_PUB -> {
//                        SecretTextField(
//                            curValue = secretText,
//                            copyToClipboard = {
//                                copyToClipboard(secretText.value)
//                            }
//                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isOldVersion) {
                                isPopUpOpened.value = !isPopUpOpened.value
                            } else {
                                onClick(MySecretItems.DELETE)
                            }
                        },
                        text = R.string.deleteSecret,
                        image = R.drawable.delete_icon,
                        buttonColor = if (isOldVersion) SatoButtonPurple.copy(alpha = 0.6f) else SatoButtonPurple,
                        horizontalPadding = 1.dp
                    )
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (secret.value?.exportRights == SeedkeeperExportRights.EXPORT_ENCRYPTED_ONLY.value.toInt()) {
                                onClick(MySecretItems.ENCRYPTED_EXPORT)
                            } else {
                                onClick(MySecretItems.SHOW)
                            }
                        },
                        text = R.string.showSecret,
                        image = R.drawable.show_password,
                        horizontalPadding = 1.dp
                    )
                }
            }
        }
    }
}

