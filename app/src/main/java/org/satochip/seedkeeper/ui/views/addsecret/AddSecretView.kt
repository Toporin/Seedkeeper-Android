package org.satochip.seedkeeper.ui.views.addsecret

import android.content.Context
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
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.ImportSecretView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.ImportMode
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.views.menu.MenuCard
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun AddSecretView(
    context: Context,
    viewModel: SharedViewModel,
    navController: NavHostController,
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
                titleText = R.string.blankTextField,
                onClick = {
                    navController.popBackStack()
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                     modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.addSecret),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.addSecretMessage),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.ExtraLight,
                            textAlign = TextAlign.Center
                        )
                    )
                }
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
                            navController.navigate(
                                ImportSecretView(
                                    importMode = ImportMode.GENERATE_A_SECRET.name
                                )
                            )
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
                            navController.navigate(
                                ImportSecretView(
                                    importMode = ImportMode.IMPORT_A_SECRET.name
                                )
                            )
                        }
                    )
                }

                // HOW TO USE SEEDKEEPER
                val howToUseSeedkeeperUri = stringResource(id = R.string.howToUseSeedkeeper)
                MenuCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    text = stringResource(R.string.useSeedkeeper),
                    textAlign = Alignment.TopStart,
                    color = SatoLightPurple,
                    drawableId = R.drawable.how_to
                ) {
                    webviewActivityIntent(
                        url = howToUseSeedkeeperUri,
                        context = context
                    )
                }
                Spacer(modifier = Modifier)
            }
        }
    }
}