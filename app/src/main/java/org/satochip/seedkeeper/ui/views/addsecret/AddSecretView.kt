package org.satochip.seedkeeper.ui.views.addsecret

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AddSecretItems
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.views.menu.MenuCard

@Composable
fun AddSecretView(
    onClick: (AddSecretItems) -> Unit,
    webViewAction: (String) -> Unit
) {
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
                titleText = R.string.addSecret,
                onClick = {
                    onClick(AddSecretItems.BACK)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(id = R.string.addSecretMessage),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center
                    )
                )
                Column {
                    // GENERATE A SECRET
                    MenuCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        text = stringResource(R.string.generateASecret),
                        textMessage = stringResource(R.string.generateASecretMessage),
                        textAlign = Alignment.TopStart,
                        color = SatoDarkPurple,
                        drawableId = R.drawable.generate_icon,
                        onClick = {
                            onClick(AddSecretItems.GENERATE_A_SECRET)
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    // IMPORT A SECRET
                    MenuCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        text = stringResource(R.string.importASecret),
                        textMessage = stringResource(R.string.importASecretMessage),
                        textAlign = Alignment.TopStart,
                        color = SatoLightPurple,
                        drawableId = R.drawable.import_icon,
                        onClick = {
                            onClick(AddSecretItems.IMPORT_A_SECRET)
                        }
                    )
                }

                // HOW TO USE SEEDKEEPER
                MenuCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    text = stringResource(R.string.useSeedkeeper),
                    textAlign = Alignment.TopStart,
                    color = SatoLightPurple,
                    drawableId = R.drawable.how_to
                ) {
                    webViewAction("https://satochip.io/setup-use-seedkeeper-on-mobile/")
                }
                Spacer(modifier = Modifier)
            }
        }
    }
}