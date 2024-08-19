package org.satochip.seedkeeper.ui.views.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.satochip.client.seedkeeper.SeedkeeperSecretHeader
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.home.SearchSecretsField
import org.satochip.seedkeeper.ui.components.home.SecretButton
import org.satochip.seedkeeper.ui.components.home.SecretsFilter

@Composable
fun SecretsList(
    cardLabel: String,
    secretHeaders: SnapshotStateList<SeedkeeperSecretHeader?>,
    addNewSecret: () -> Unit,
    onSecretClick: (SeedkeeperSecretHeader) -> Unit,
) {
    val curValue = remember {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    val searchQueryState = rememberUpdatedState(curValue.value)
    var filteredList by remember {
        mutableStateOf(secretHeaders.toList())
    }
    val scrollState = rememberScrollState()

    var isFilterFieldNeeded by remember {
        mutableStateOf(false)
    }
    val itemHeight = 50.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val totalHeight = (secretHeaders.size * (itemHeight + 10.dp)) + itemHeight
    isFilterFieldNeeded = totalHeight >= (screenHeight / 2)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = cardLabel,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 24.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.homeAuthenticatedText),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (isFilterFieldNeeded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.search),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center
                    )
                )
                SearchSecretsField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    curValue = curValue,
                    onValueChange = {
                        coroutineScope.launch {
                            val searchQueryFlow = MutableStateFlow(searchQueryState.value)
                            searchQueryFlow
                                .debounce(500)
                                .distinctUntilChanged()
                                .collect { query ->
                                    filteredList = if (curValue.value.isEmpty()) {
                                        secretHeaders.toList()
                                    } else {
                                        secretHeaders.toList().filter {
                                            it?.label?.contains(
                                                curValue.value,
                                                ignoreCase = true
                                            ) == true
                                        }
                                    }
                                }
                        }
                    }
                )
                SecretsFilter(
                    onClick = { filter ->
                        when (filter) {
                            SeedkeeperSecretType.DEFAULT_TYPE -> {
                                filteredList = secretHeaders.toList()
                            }

                            SeedkeeperSecretType.PASSWORD -> {
                                filteredList = secretHeaders.toList()
                                filteredList = secretHeaders.toList().filter {
                                    it?.type == filter
                                }
                            }

                            SeedkeeperSecretType.BIP39_MNEMONIC -> {
                                filteredList = secretHeaders.toList()
                                filteredList = secretHeaders.toList().filter {
                                    it?.type == filter
                                }
                            }
                            SeedkeeperSecretType.DATA -> {
                                filteredList = secretHeaders.toList()
                                filteredList = secretHeaders.toList().filter {
                                    it?.type == filter
                                }
                            }

                            else -> {}
                        }
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.mySecretList),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            SecretButton(
                onClick = {
                    addNewSecret()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            filteredList.forEach { secret ->
                SecretButton(
                    secretHeader = secret,
                    onClick = {
                        secret?.let {
                            onSecretClick(secret)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }
}