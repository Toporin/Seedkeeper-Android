package org.satochip.seedkeeper.ui.views.mysecret

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import org.satochip.client.seedkeeper.SeedkeeperExportRights
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.HomeView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AppErrorMsg
import org.satochip.seedkeeper.data.NfcActionType
import org.satochip.seedkeeper.data.SecretData
import org.satochip.seedkeeper.parsers.SecretDataParser
import org.satochip.seedkeeper.ui.components.import.SecretTextField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.mysecret.GetSpecificSecretInfoFields
import org.satochip.seedkeeper.ui.components.mysecret.NewSeedkeeperPopUpDialog
import org.satochip.seedkeeper.ui.components.mysecret.SecretInfoField
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoButtonPurple
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun ShowSecretView(
    context: Context,
    viewModel: SharedViewModel,
    navController: NavHostController,
) {
    val scrollState = rememberScrollState()
    val secretText = remember {
        mutableStateOf("")
    }
    // NFC dialog
    val showNfcDialog = remember { mutableStateOf(false) } // for NfcDialog
    if (showNfcDialog.value) {
        NfcDialog(
            openDialogCustom = showNfcDialog,
            resultCodeLive = viewModel.resultCodeLive,
            isConnected = viewModel.isCardConnected
        )
    }
    // buy card popup
    val buySeedkeeperUrl = stringResource(id = R.string.buySeedkeeperUrl)
    val isPopUpOpened = remember {
        mutableStateOf(false)
    }
    if (isPopUpOpened.value) {
        NewSeedkeeperPopUpDialog(
            isOpen = isPopUpOpened,
            title = R.string.buySeedkeeper,
            onClick = {
                webviewActivityIntent(
                    url = buySeedkeeperUrl,
                    context = context
                )
            }
        )
    }
    // delete secret
    val showConfirmDeleteMsg = remember {
        mutableStateOf(false)
    }
    val hasUserConfirmedTerms = remember {
        mutableStateOf(false)
    }
    // error mgmt
    val showError = remember {
        mutableStateOf(false)
    }
    val appError = remember {
        mutableStateOf(AppErrorMsg.OK)
    }

    // Secret data
    val secret = remember {
        mutableStateOf<SecretData?>(null)
    }
    LaunchedEffect(viewModel.currentSecretHeader) {
        viewModel.currentSecretHeader?.let { currentSecretHeader ->
            secret.value = SecretData(
                    label = currentSecretHeader.label,
                    type = currentSecretHeader.type,
                    exportRights = currentSecretHeader.exportRights.value.toInt(),
                    subType = currentSecretHeader.subtype.toInt(),
                )
        } ?: run {
            navController.navigate(HomeView) {
                popUpTo(0)
            }
        }
    }

    LaunchedEffect(viewModel.currentSecretObject) {
        viewModel.currentSecretObject?.let { secretObject ->
            secret.value = SecretDataParser().parseByType(
                seedkeeperSecretType = secretObject.secretHeader.type,
                secretObject = secretObject
            )
            secret.value?.label = secretObject.secretHeader.label
            secret.value?.subType = secretObject.secretHeader.subtype.toInt()
        }
    }

    fun enableSeedQRCode(secretType: SeedkeeperSecretType, secretSubtype: Int): Boolean {
        if (secretType == SeedkeeperSecretType.BIP39_MNEMONIC){
            return true
        }
        else if (secretType == SeedkeeperSecretType.MASTERSEED &&  secretSubtype == 0x01){
            return true
        }
        return false
    }

    val stringResourceMap = mapOf(
        SeedkeeperSecretType.MASTERSEED to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.BIP39_MNEMONIC to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.ELECTRUM_MNEMONIC to stringResource(id = R.string.masterseed),
        SeedkeeperSecretType.PUBKEY to stringResource(id = R.string.pubkey),
        SeedkeeperSecretType.PASSWORD to stringResource(id = R.string.password),
        SeedkeeperSecretType.MASTER_PASSWORD to stringResource(id = R.string.password),
        SeedkeeperSecretType.DATA to stringResource(id = R.string.data),
        SeedkeeperSecretType.WALLET_DESCRIPTOR to stringResource(id = R.string.walletDescriptor),
        SeedkeeperSecretType.SECRET_2FA to stringResource(id = R.string.secret2FA),
    )

    when (secret.value?.type) {
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
        viewModel.resetCurrentSecretObject()
        navController.popBackStack()
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
                //titleText = stringResource(R.string.mySecret),
                titleText = stringResourceMap[secret.value?.type] ?: "",
                onClick = {
                    viewModel.resetCurrentSecretObject()
                    navController.popBackStack()
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
//                    Text(
//                        text = stringResourceMap[secret.value?.type] ?: "",
//                        style = TextStyle(
//                            color = Color.Black,
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Bold,
//                            textAlign = TextAlign.Center
//                        )
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
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

                    secret.value?.type?.let { type ->
                        GetSpecificSecretInfoFields(
                            secretType = type,
                            secret = secret
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                SecretTextField(
                    curValue = secretText,
                    placeholder = stringResource(id = R.string.secretRevealPlaceholder),
                    isSeedQRCodeEnabled = enableSeedQRCode(secretType= secret.value?.type ?: SeedkeeperSecretType.DEFAULT_TYPE, secret.value?.subType ?: 0),
                    minHeight = 250.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm delete msg
                if (showConfirmDeleteMsg.value) {
                    Text(
                        text = stringResource(R.string.secretResetWarningText),
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
                            text = stringResource(R.string.checkThisBoxToContinue),
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

                // error msg
                if (showError.value) {
                    Text(
                        text = stringResource(appError.value.msg),
                        style = TextStyle(
                            color = Color.Red,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    )
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
                            if (viewModel.getProtocolVersionInt() == 1) {
                                isPopUpOpened.value = !isPopUpOpened.value
                            } else {
                                // check confirm msg
                                if (showConfirmDeleteMsg.value == false) {
                                    showConfirmDeleteMsg.value = true
                                } else if (hasUserConfirmedTerms.value == true) {
                                    // delete secret
                                    showNfcDialog.value = true // NfcDialog
                                    viewModel.scanCardForAction(
                                        activity = context as Activity,
                                        nfcActionType = NfcActionType.DELETE_SECRET
                                    )
                                }
                            }
                        },
                        text = R.string.deleteSecret,
                        image = R.drawable.delete_icon,
                        buttonColor = if (viewModel.getProtocolVersionInt() == 1) SatoButtonPurple.copy(alpha = 0.6f) else SatoButtonPurple,
                        horizontalPadding = 1.dp
                    )
                    // Reveal button
                    SatoButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (secret.value?.exportRights == SeedkeeperExportRights.EXPORT_ENCRYPTED_ONLY.value.toInt()) {
                                appError.value = AppErrorMsg.PLAINTEXT_EXPORT_NOT_ALLOWED
                                showError.value = true
                            } else {
                                showNfcDialog.value = true // NfcDialog
                                viewModel.scanCardForAction(
                                    activity = context as Activity,
                                    nfcActionType = NfcActionType.EXPORT_SECRET
                                )
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