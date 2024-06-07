package org.satochip.seedkeeper.ui.views.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.GenerateViewItems
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.components.generate.PasswordLengthField
import org.satochip.seedkeeper.ui.components.generate.PrimaryGenerateButton
import org.satochip.seedkeeper.ui.components.generate.SecondaryGenerateButton
import org.satochip.seedkeeper.ui.components.generate.SecretTextField
import org.satochip.seedkeeper.ui.components.generate.SelectField
import org.satochip.seedkeeper.ui.components.home.NfcDialog
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.TitleTextField
import org.satochip.seedkeeper.ui.theme.SatoPurple

@Composable
fun GenerateView(
    onClick: (GenerateViewItems, String?) -> Unit
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
        val showNfcDialog = remember { mutableStateOf(false) }
        val generateStatus = remember {
            mutableStateOf(GenerateStatus.DEFAULT)
        }
        val typeOfSecret = remember {
            mutableStateOf(TypeOfSecret.TYPE_OF_SECRET)
        }

        if (showNfcDialog.value) {
            NfcDialog(
                openDialogCustom = showNfcDialog,
            )
        }
        Image(
            painter = painterResource(R.drawable.seedkeeper_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderAlternateRow(
                onClick = {
                    onClick(GenerateViewItems.BACK, null)
                },
                titleText = R.string.generate
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
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

                    Spacer(modifier = Modifier.height(28.dp))
                    when (generateStatus.value) {
                        GenerateStatus.DEFAULT -> {
                            SelectField(
                                selectList = listOf(
                                    SelectFieldItem (prefix = null, text = R.string.typeOfSecret),
                                    SelectFieldItem (prefix = null, text = R.string.mnemonicPhrase),
                                    SelectFieldItem (prefix = null, text = R.string.loginPassword),
                                ),
                                onClick = { item ->
                                    stringResourceMap[item]?.let { resourceItem ->
                                        typeOfSecret.value = TypeOfSecret.valueOfKey(resourceItem)
                                    }
                                }
                            )
                        }

                        GenerateStatus.MNEMONIC_PHRASE_FIRST_STEP,
                        GenerateStatus.MNEMONIC_PHRASE_SECOND_STEP -> {
                            val curValueLabel = remember {
                                mutableStateOf("")
                            }
                            val curValuePassphrase = remember {
                                mutableStateOf("")
                            }
                            InputField(
                                curValue = curValueLabel,
                                placeHolder = R.string.label,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            SelectField(
                                selectList = listOf(
                                    SelectFieldItem (prefix = null, text = R.string.mnemonicSize),
                                    SelectFieldItem (prefix = 12, text = R.string.mnemonicWords),
                                    SelectFieldItem (prefix = 18, text = R.string.mnemonicWords),
                                    SelectFieldItem (prefix = 24, text = R.string.mnemonicWords),
                                ),
                                onClick = { }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            InputField(
                                curValue = curValuePassphrase,
                                placeHolder = R.string.passphrase,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                        }

                        GenerateStatus.LOGIN_PASSWORD_FIRST_STEP,
                        GenerateStatus.LOGIN_PASSWORD_SECOND_STEP -> {
                            val curValueLabel = remember {
                                mutableStateOf("")
                            }
                            val curValueLogin = remember {
                                mutableStateOf("")
                            }
                            val curValueUrl = remember {
                                mutableStateOf("")
                            }
                            InputField(
                                curValue = curValueLabel,
                                placeHolder = R.string.label,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            InputField(
                                curValue = curValueLogin,
                                placeHolder = R.string.loginOptional,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            InputField(
                                curValue = curValueUrl,
                                placeHolder = R.string.urlOptional,
                                containerColor = SatoPurple.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            PasswordLengthField()
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        GenerateStatus.HOME -> {
                            val curValue = remember {
                                mutableStateOf("Seedphrase - Satochip HW")  //placeholder
                            }
                            InputField(
                                isEditable = false,
                                curValue = curValue,
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
                    val secret = remember {
                        mutableStateOf("")
                    }

                    when (generateStatus.value) {
                        GenerateStatus.DEFAULT, GenerateStatus.HOME -> {}
                        GenerateStatus.LOGIN_PASSWORD_FIRST_STEP, GenerateStatus.LOGIN_PASSWORD_SECOND_STEP -> {
                            SecretTextField(
                                modifier = Modifier.height(150.dp),
                                curValue = secret,
                                copyToClipboard = {
                                    onClick(GenerateViewItems.COPY_TO_CLIPBOARD, secret.value)
                                }
                            )
                        }
                        else -> {
                            SecretTextField(
                                curValue = secret,
                                copyToClipboard = {
                                    onClick(GenerateViewItems.COPY_TO_CLIPBOARD, secret.value)
                                }
                            )
                        }
                    }

                    if (!(generateStatus.value == GenerateStatus.DEFAULT || generateStatus.value == GenerateStatus.HOME)) {
                        SecondaryGenerateButton(generateStatus = generateStatus)
                    }
                    PrimaryGenerateButton(
                        generateStatus = generateStatus,
                        onClick = {
                            when (generateStatus.value) {
                                GenerateStatus.DEFAULT -> {
                                    when (typeOfSecret.value) {
                                        TypeOfSecret.MNEMONIC_PHRASE -> {
                                            generateStatus.value =
                                                GenerateStatus.MNEMONIC_PHRASE_FIRST_STEP
                                        }

                                        TypeOfSecret.LOGIN_PASSWORD -> {
                                            generateStatus.value =
                                                GenerateStatus.LOGIN_PASSWORD_FIRST_STEP
                                        }

                                        else -> {}
                                    }
                                }

                                GenerateStatus.MNEMONIC_PHRASE_FIRST_STEP -> {
                                    generateStatus.value =
                                        GenerateStatus.MNEMONIC_PHRASE_SECOND_STEP
                                }

                                GenerateStatus.LOGIN_PASSWORD_FIRST_STEP -> {
                                    generateStatus.value = GenerateStatus.LOGIN_PASSWORD_SECOND_STEP
                                }

                                GenerateStatus.MNEMONIC_PHRASE_SECOND_STEP,
                                GenerateStatus.LOGIN_PASSWORD_SECOND_STEP -> {
                                    generateStatus.value = GenerateStatus.HOME
                                    showNfcDialog.value = !showNfcDialog.value
                                }

                                GenerateStatus.HOME -> {
                                    onClick(GenerateViewItems.BACK, null)
                                }
                            }
                        }
                    )
                }
            }

        }
    }
}