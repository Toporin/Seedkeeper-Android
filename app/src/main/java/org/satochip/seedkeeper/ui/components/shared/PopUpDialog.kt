package org.satochip.seedkeeper.ui.components.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.CardInformationItems
import org.satochip.seedkeeper.ui.components.generate.InputField
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.ui.theme.SatoToggleGray
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun PopUpDialog(
    isOpen: MutableState<Boolean>,
    curValueLogin: MutableState<String>,
    title: Int,
    list: List<String>,
    onClick: (String) -> Unit
) {
    if (!isOpen.value) return
    var filteredList by remember {
        mutableStateOf(list)
    }
    val coroutineScope = rememberCoroutineScope()
    val searchQueryState = rememberUpdatedState(curValueLogin.value)

    Dialog(
        onDismissRequest = {
            isOpen.value = !isOpen.value
        },
        properties = DialogProperties()
    ) {
        Column (
            modifier = Modifier
                .width(350.dp)
                .height(350.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SatoPurple.copy(alpha = 0.5f)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = title),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .satoClickEffect(
                            onClick = {
                                isOpen.value = !isOpen.value
                            }
                        ),
                    text = stringResource(id = R.string.done),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = stringResource(id = R.string.emailListWriteNewOne),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                modifier = Modifier.padding(horizontal = 8.dp),
                curValue = curValueLogin,
                placeHolder = R.string.loginOptional,
                containerColor = SatoPurple.copy(alpha = 0.5f),
                onValueChange = {
                    coroutineScope.launch {
                        val searchQueryFlow = MutableStateFlow(searchQueryState.value)
                        searchQueryFlow
                            .debounce(500)
                            .distinctUntilChanged()
                            .collect { query ->
                                filteredList = if (curValueLogin.value.isEmpty()) {
                                    list
                                } else {
                                    list.filter { it.contains(curValueLogin.value, ignoreCase = true) }
                                }
                            }
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = stringResource(id = R.string.emailListUseExisting),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Divider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                horizontalAlignment = Alignment.Start
            ) {
                filteredList.forEach { email ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    curValueLogin.value = email
                                    isOpen.value = false
                                }
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier,
                                text = email,
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            )

                            Image(
                                modifier = Modifier
                                    .background(Color.Transparent, shape = CircleShape)
                                    .satoClickEffect(
                                        onClick = {
                                            onClick(email)
                                            filteredList = filteredList.toMutableList().apply {
                                                remove(email)
                                            }
                                        }
                                    )
                                    .padding(
                                        start = 12.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .width(16.dp),
                                painter = painterResource(R.drawable.cancel),
                                contentDescription = stringResource(id = R.string.back),
                                colorFilter = ColorFilter.tint(Color.Black),
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}