package org.satochip.seedkeeper.ui.views.import

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.data.TypeOfSecret
import org.satochip.seedkeeper.ui.components.generate.SelectField
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.TitleTextField

@Composable
fun ImportDefault(
    stringResourceMap: Map<Int, String>,
    typeOfSecret: MutableState<TypeOfSecret>,
    generateStatus: MutableState<GenerateStatus>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TitleTextField(
            title = R.string.importASecret,
            text = R.string.importASecretMessage
        )
        Spacer(modifier = Modifier.height(8.dp))
        SelectField(
            selectList = listOf(
                SelectFieldItem(prefix = null, text = R.string.typeOfSecret),
                SelectFieldItem(prefix = null, text = R.string.mnemonicPhrase),
                SelectFieldItem(prefix = null, text = R.string.loginPassword),
                SelectFieldItem(prefix = null, text = R.string.bitcoinDescriptor),
            ),
            onClick = { item ->
                stringResourceMap[item]?.let { resourceItem ->
                    typeOfSecret.value = TypeOfSecret.valueOfKey(resourceItem)
                }
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
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
                        TypeOfSecret.BITCOIN_DESCRIPTOR -> {
                            generateStatus.value =
                                GenerateStatus.BITCOIN_DESCRIPTOR
                        }
                        else -> {}
                    }
                },
                text = R.string.next
            )
        }
    }
}