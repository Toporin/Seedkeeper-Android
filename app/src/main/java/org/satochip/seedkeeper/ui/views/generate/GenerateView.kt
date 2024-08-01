package org.satochip.seedkeeper.ui.views.generate

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.PasswordLengthField
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.generate.SelectField
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.PopUpDialog
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoActiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.getType
import org.satochip.seedkeeper.utils.isClickable
import org.satochip.seedkeeper.utils.isEmailCorrect

@Composable
fun GenerateView(
    settings: SharedPreferences,
    isImportDone: MutableState<Boolean>,
    onClick: (GenerateViewItems, String?, PasswordOptions?) -> String,
    onImportSecret: (GeneratePasswordData) -> Unit
) {
    val stringResourceMap = mapOf(
        R.string.loginPassword to "loginPassword",
        R.string.typeOfSecret to "typeOfSecret",
        R.string.mnemonicPhrase to "mnemonicPhrase"
    )
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val generateStatus = remember {
            mutableStateOf(GenerateStatus.DEFAULT)
        }
        val typeOfSecret = remember {
            mutableStateOf(TypeOfSecret.TYPE_OF_SECRET)
        }
        val secret = remember {
            mutableStateOf("")
        }
        val curValueLabel = remember {
            mutableStateOf("")
        }
        val curValuePassphrase = remember {
            mutableStateOf("")
        }
        val curValueLogin = remember {
            mutableStateOf("")
        }
        val curValueUrl = remember {
            mutableStateOf("")
        }
        val passwordOptions = remember {
            mutableStateOf(
                PasswordOptions()
            )
        }
        val isPopUpOpened = remember {
            mutableStateOf(false)
        }
        val retrievedSet = remember {
            mutableStateOf<Set<String>>(emptySet())
        }
        retrievedSet.value = settings.getStringSet(
            SeedkeeperPreferences.USED_LOGINS.name,
            emptySet()
        ) ?: emptySet()

        if (isImportDone.value) {
            generateStatus.value = GenerateStatus.HOME
        }

        if (isPopUpOpened.value) {
            PopUpDialog(
                isOpen = isPopUpOpened,
                curValueLogin = curValueLogin,
                title = R.string.emailListTitle,
                list = retrievedSet.value.toList(),
                onClick = { email ->
                    val currentSet =
                        settings.getStringSet(SeedkeeperPreferences.USED_LOGINS.name, emptySet())
                            ?.toMutableSet() ?: mutableSetOf()
                    if (currentSet.remove(email)) {
                        settings.edit()
                            .putStringSet(SeedkeeperPreferences.USED_LOGINS.name, currentSet)
                            .apply()
                    }
                    retrievedSet.value = settings.getStringSet(
                        SeedkeeperPreferences.USED_LOGINS.name,
                        emptySet()
                    ) ?: emptySet()
                    if (retrievedSet.value.isEmpty()) {
                        isPopUpOpened.value = false
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick(GenerateViewItems.BACK, null, null)
                },
                titleText = R.string.generate
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
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (generateStatus.value) {
                        GenerateStatus.HOME -> {
                            TitleTextField(
                                title = R.string.congratulations,
                                text = R.string.generateSuccessful
                            )
                        }

                        else -> {
                            TitleTextField(
                                title = R.string.generateASecret,
                                text = R.string.generateExplanation
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    when (generateStatus.value) {
                        GenerateStatus.DEFAULT -> {
                            SelectField(
                                selectList = listOf(
                                    SelectFieldItem(prefix = null, text = R.string.typeOfSecret),
                                    SelectFieldItem(prefix = null, text = R.string.mnemonicPhrase),
                                    SelectFieldItem(prefix = null, text = R.string.loginPassword),
                                ),
                                onClick = { item ->
                                    stringResourceMap[item]?.let { resourceItem ->
                                        typeOfSecret.value = TypeOfSecret.valueOfKey(resourceItem)
                                    }
                                }
                            )
                        }

                        GenerateStatus.MNEMONIC_PHRASE -> {
                            InputField(
                                curValue = curValueLabel,
                                placeHolder = R.string.label,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            SelectField(
                                selectList = listOf(
                                    SelectFieldItem(prefix = null, text = R.string.mnemonicSize),
                                    SelectFieldItem(prefix = 12, text = R.string.mnemonicWords),
                                    SelectFieldItem(prefix = 18, text = R.string.mnemonicWords),
                                    SelectFieldItem(prefix = 24, text = R.string.mnemonicWords),
                                ),
                                onClick = { length ->
                                    passwordOptions.value.passwordLength = length
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            InputField(
                                curValue = curValuePassphrase,
                                placeHolder = R.string.passphrase,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                        }

                        GenerateStatus.LOGIN_PASSWORD -> {

                            InputField(
                                curValue = curValueLabel,
                                placeHolder = R.string.label,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            InputField(
                                isEditable = retrievedSet.value.isEmpty(),
                                curValue = curValueLogin,
                                placeHolder = R.string.loginOptional,
                                containerColor = SatoPurple.copy(alpha = 0.5f),
                                isEmail = true,
                                onClick = {
                                    if (retrievedSet.value.isNotEmpty()) {
                                        isPopUpOpened.value = !isPopUpOpened.value
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            InputField(
                                curValue = curValueUrl,
                                placeHolder = R.string.urlOptional,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PasswordLengthField(
                                passwordOptions = passwordOptions
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        GenerateStatus.HOME -> {
                            InputField(
                                isEditable = false,
                                curValue = curValueLabel,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GifImage(
                                    modifier = Modifier
                                        .size(300.dp)
                                        .align(Alignment.Center),
                                    image = R.drawable.vault
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (generateStatus.value) {
                        GenerateStatus.DEFAULT, GenerateStatus.HOME -> {}
                        else -> {
                            SecretTextField(
                                curValue = secret,
                                copyToClipboard = {
                                    onClick(GenerateViewItems.COPY_TO_CLIPBOARD, secret.value, null)
                                }
                            )
                        }
                    }

                    if (!(generateStatus.value == GenerateStatus.DEFAULT || generateStatus.value == GenerateStatus.HOME)) {
                        //Back
                        SatoButton(
                            onClick = {
                                secret.value = ""
                                generateStatus.value = GenerateStatus.DEFAULT
                                typeOfSecret.value = TypeOfSecret.TYPE_OF_SECRET
                                passwordOptions.value.passwordLength = 4

                            },
                            buttonColor = Color.Transparent,
                            textColor = Color.Black,
                            text = R.string.back
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (generateStatus.value) {
                            GenerateStatus.DEFAULT -> {
                                //Next
                                SatoButton(
                                    onClick = {
                                        when (typeOfSecret.value) {
                                            TypeOfSecret.MNEMONIC_PHRASE -> {
                                                generateStatus.value =
                                                    GenerateStatus.MNEMONIC_PHRASE
                                            }

                                            TypeOfSecret.LOGIN_PASSWORD -> {
                                                generateStatus.value =
                                                    GenerateStatus.LOGIN_PASSWORD
                                            }

                                            else -> {}
                                        }
                                    },
                                    text = R.string.next
                                )
                            }

                            GenerateStatus.HOME -> {
                                //Home
                                SatoButton(
                                    onClick = {
                                        onClick(GenerateViewItems.HOME, null, null)
                                    },
                                    text = R.string.home
                                )
                            }

                            else -> {
                                //Generate
                                SatoButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {
                                        when (generateStatus.value) {
                                            GenerateStatus.MNEMONIC_PHRASE -> {
                                                secret.value = onClick(
                                                    GenerateViewItems.GENERATE_MNEMONIC_PHRASE,
                                                    null,
                                                    passwordOptions.value
                                                )
                                            }

                                            GenerateStatus.LOGIN_PASSWORD -> {
                                                secret.value = onClick(
                                                    GenerateViewItems.GENERATE_A_PASSWORD,
                                                    null,
                                                    passwordOptions.value
                                                )
                                            }

                                            else -> {}
                                        }
                                    },
                                    text = if(secret.value.isNotEmpty()) R.string.regenerate else R.string.generate,
                                )
                                //Import
                                SatoButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {
                                        if (isClickable(secret, curValueLabel)) {
                                            val type = getType(generateStatus.value)
                                            var password: String = ""
                                            var mnemonic: String? = null
                                            if (type == SeedkeeperSecretType.BIP39_MNEMONIC || type == SeedkeeperSecretType.MASTERSEED) {
                                                mnemonic = secret.value
                                                password = curValuePassphrase.value
                                            } else {
                                                password = secret.value
                                            }
                                            onImportSecret(
                                                GeneratePasswordData(
                                                    size = passwordOptions.value.passwordLength,
                                                    type = getType(generateStatus.value),
                                                    password = password,
                                                    label = curValueLabel.value,
                                                    login = curValueLogin.value,
                                                    url = curValueUrl.value,
                                                    mnemonic = mnemonic
                                                )
                                            )
                                        }
                                        if (curValueLogin.value.isNotEmpty()) {
                                            val stringSet = listOf(curValueLogin.value).toSet()
                                            retrievedSet.value += stringSet
                                            settings.edit().putStringSet(
                                                SeedkeeperPreferences.USED_LOGINS.name,
                                                retrievedSet.value
                                            ).apply()
                                        }
                                    },
                                    text = R.string.importButton,
                                    textColor = if (
                                        isClickable(
                                            secret,
                                            curValueLabel
                                        )
                                    ) Color.White else SatoActiveTracer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
