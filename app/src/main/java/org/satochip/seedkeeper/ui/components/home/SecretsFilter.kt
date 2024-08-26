package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.client.seedkeeper.SeedkeeperSecretType
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun SecretsFilter(
    modifier: Modifier = Modifier,
    onClick: (SeedkeeperSecretType) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var selectedLogType by remember {
        mutableStateOf(SeedkeeperSecretType.DEFAULT_TYPE)
    }
    val seedkeeperSecretTypeMap = hashMapOf(
        SeedkeeperSecretType.DEFAULT_TYPE to "All",
        SeedkeeperSecretType.BIP39_MNEMONIC to "Mnemonic",
        SeedkeeperSecretType.PASSWORD to "Password",
        SeedkeeperSecretType.DATA to "Data",
        SeedkeeperSecretType.WALLET_DESCRIPTOR to "Descriptors"
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Text(
//                modifier = Modifier.clickable {
//                    isExpanded = true
//                },
//                textAlign = TextAlign.Start,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color.Black,
//                text = seedkeeperSecretTypeMap.getValue(selectedLogType)
//            )
            Image(
                painter = painterResource(R.drawable.filter),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .satoClickEffect(
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }

        DropdownMenu(
            modifier = Modifier
                .background(
                    color = Color.White,
                ),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .background(
                        color = if (selectedLogType == SeedkeeperSecretType.DEFAULT_TYPE) Color.Gray.copy(
                            alpha = 0.2f
                        ) else Color.White,
                    ),
                onClick = {
                    selectedLogType = SeedkeeperSecretType.DEFAULT_TYPE
                    isExpanded = false
                    onClick(selectedLogType)
                },
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = seedkeeperSecretTypeMap.getValue(SeedkeeperSecretType.DEFAULT_TYPE)
                    )
                }
            )
            DropdownMenuItem(
                modifier = Modifier
                    .background(
                        color = if (selectedLogType == SeedkeeperSecretType.BIP39_MNEMONIC) Color.Gray.copy(
                            alpha = 0.2f
                        ) else Color.White,
                    ),
                onClick = {
                    selectedLogType = SeedkeeperSecretType.BIP39_MNEMONIC
                    isExpanded = false
                    onClick(selectedLogType)
                },
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = seedkeeperSecretTypeMap.getValue(SeedkeeperSecretType.BIP39_MNEMONIC)
                    )
                }
            )
            DropdownMenuItem(
                modifier = Modifier
                    .background(
                        color = if (selectedLogType == SeedkeeperSecretType.PASSWORD) Color.Gray.copy(
                            alpha = 0.2f
                        ) else Color.White,
                    ),
                onClick = {
                    selectedLogType = SeedkeeperSecretType.PASSWORD
                    isExpanded = false
                    onClick(selectedLogType)
                },
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = seedkeeperSecretTypeMap.getValue(SeedkeeperSecretType.PASSWORD)
                    )
                }
            )
            DropdownMenuItem(
                modifier = Modifier
                    .background(
                        color = if (selectedLogType == SeedkeeperSecretType.WALLET_DESCRIPTOR) Color.Gray.copy(
                            alpha = 0.2f
                        ) else Color.White,
                    ),
                onClick = {
                    selectedLogType = SeedkeeperSecretType.WALLET_DESCRIPTOR
                    isExpanded = false
                    onClick(selectedLogType)
                },
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = seedkeeperSecretTypeMap.getValue(SeedkeeperSecretType.WALLET_DESCRIPTOR)
                    )
                }
            )
            DropdownMenuItem(
                modifier = Modifier
                    .background(
                        color = if (selectedLogType == SeedkeeperSecretType.DATA) Color.Gray.copy(
                            alpha = 0.2f
                        ) else Color.White,
                    ),
                onClick = {
                    selectedLogType = SeedkeeperSecretType.DATA
                    isExpanded = false
                    onClick(selectedLogType)
                },
                text = {
                    Text(
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = seedkeeperSecretTypeMap.getValue(SeedkeeperSecretType.DATA)
                    )
                }
            )
        }
    }
}