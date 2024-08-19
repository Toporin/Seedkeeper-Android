package org.satochip.seedkeeper.ui.views.generate

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GeneratePasswordData
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.SeedkeeperPreferences
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.PopUpDialog

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
                when(generateStatus.value) {
                    GenerateStatus.DEFAULT -> {
                        GenerateDefault(
                            generateStatus = generateStatus,
                            typeOfSecret = typeOfSecret,
                            stringResourceMap = stringResourceMap
                        )
                    }
                    GenerateStatus.MNEMONIC_PHRASE -> {
                        GenerateMnemonic(
                            settings = settings,
                            curValueLabel = curValueLabel,
                            passwordOptions = passwordOptions,
                            curValuePassphrase = curValuePassphrase,
                            secret = secret,
                            generateStatus = generateStatus,
                            typeOfSecret = typeOfSecret,
                            curValueLogin = curValueLogin,
                            curValueUrl = curValueUrl,
                            retrievedSet = retrievedSet,
                            onClick = { generateViewItems, secretValue, passOptions ->
                                onClick(generateViewItems,secretValue,passOptions)
                            },
                            onImportSecret = { passwordData ->
                                onImportSecret(passwordData)
                            }
                        )
                    }
                    GenerateStatus.LOGIN_PASSWORD -> {
                        GeneratePassword(
                            settings = settings,
                            curValueLabel = curValueLabel,
                            passwordOptions = passwordOptions,
                            curValuePassphrase = curValuePassphrase,
                            secret = secret,
                            generateStatus = generateStatus,
                            typeOfSecret = typeOfSecret,
                            curValueLogin = curValueLogin,
                            curValueUrl = curValueUrl,
                            retrievedSet = retrievedSet,
                            isPopUpOpened = isPopUpOpened,
                            onClick = { generateViewItems, secretValue, passOptions ->
                                onClick(generateViewItems,secretValue,passOptions)
                            },
                            onImportSecret = { passwordData ->
                                onImportSecret(passwordData)
                            }
                        )
                    }
                    GenerateStatus.HOME -> {
                        GenerateHome(
                            curValueLabel = curValueLabel,
                            onClick = { generateViewItems, secretValue, passOptions ->
                                onClick(generateViewItems,secretValue,passOptions)
                            },
                        )
                    }
                    GenerateStatus.BITCOIN_DESCRIPTOR -> {}
                }
            }
        }
    }
}
