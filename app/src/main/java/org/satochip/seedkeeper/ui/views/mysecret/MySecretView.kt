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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.zIndex
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.MySecretItems
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.mysecret.GetSpecificSecretInfoFields
import org.satochip.seedkeeper.ui.components.mysecret.NewSeedkeeperPopUpDialog
import org.satochip.seedkeeper.ui.components.mysecret.SecretInfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple

@Composable
fun MySecretView(
    secret: MutableState<SecretData?>,
    type: String, // TODO: redundant with secret?
    isOldVersion: Boolean,
    onClick: (MySecretItems) -> Unit, // TODO: integrate directly?
) {
    val scrollState = rememberScrollState()
    val secretText = remember {
        mutableStateOf("")
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
    val showConfirmDeleteMsg = remember {
        mutableStateOf(false)
    }
    val hasUserConfirmedTerms = remember {
        mutableStateOf(false)
    }

    val checkThisBoxToContinue = stringResource(id = R.string.checkThisBoxToContinue)
    val secretResetWarningText = stringResource(id = R.string.secretResetWarningText)
    val stringResourceMap = mapOf(
        SeedkeeperSecretType.MASTERSEED.name to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.BIP39_MNEMONIC.name to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.ELECTRUM_MNEMONIC.name to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.PUBKEY.name to stringResource(id = R.string.pubkey),
        SeedkeeperSecretType.PASSWORD.name to stringResource(id = R.string.password),
        SeedkeeperSecretType.MASTER_PASSWORD.name to stringResource(id = R.string.password),
        SeedkeeperSecretType.DATA.name to stringResource(id = R.string.data),
        SeedkeeperSecretType.WALLET_DESCRIPTOR.name to stringResource(id = R.string.walletDescriptor),
        SeedkeeperSecretType.SECRET_2FA.name to stringResource(id = R.string.secret2FA),
    )

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
        SeedkeeperSecretType.DATA -> {
            secret.value?.data?.let { data ->
                secretText.value = data
            }
        }
        SeedkeeperSecretType.WALLET_DESCRIPTOR -> {
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResourceMap[type] ?: "",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                }
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
                }

                SecretTextField(
                    curValue = secretText,
                    minHeight = 250.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm delete msg
                if (showConfirmDeleteMsg.value) {
                    Text(
                        text = secretResetWarningText,
                        style = TextStyle(
                            color = Color.Red,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            checkThisBoxToContinue,
                            style = TextStyle(
                                color = Color.Red,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        )
                        Checkbox(
                            checked = hasUserConfirmedTerms.value,
                            onCheckedChange = { hasUserConfirmedTerms.value = it }
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // delete button
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isOldVersion) {
                                isPopUpOpened.value = !isPopUpOpened.value
                            } else {
                                // check confirm msg
                                if (showConfirmDeleteMsg.value == false) {
                                    showConfirmDeleteMsg.value = true
                                } else if (hasUserConfirmedTerms.value == true) {
                                    // delete secret
                                    onClick(MySecretItems.DELETE)
                                }
                            }
                        },
                        text = R.string.deleteSecret,
                        image = R.drawable.delete_icon,
                        buttonColor = if (isOldVersion) SatoButtonPurple.copy(alpha = 0.6f) else SatoButtonPurple,
                        horizontalPadding = 1.dp
                    )
                    // Reveal button
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

