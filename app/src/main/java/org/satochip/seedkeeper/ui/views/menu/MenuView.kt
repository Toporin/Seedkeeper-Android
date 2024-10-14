package org.satochip.seedkeeper.ui.views.menu

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.BackupView
import org.satochip.seedkeeper.CardInformation
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.SettingsView
import org.satochip.seedkeeper.data.MenuItems
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.InfoPopUpDialog
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.webviewActivityIntent
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun MenuView(
    context: Context,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    // INFO DIALOG
    val showInfoDialog = remember { mutableStateOf(false) } // for infoDialog
    if (showInfoDialog.value) {
        InfoPopUpDialog(
            isOpen = showInfoDialog,
            title = R.string.cardNeedToBeScannedTitle,
            message = R.string.cardNeedToBeScannedMessage
        )
    }

    val scrollState = rememberScrollState()
    val howToUseSeedkeeperUrl = stringResource(id = R.string.howToUseSeedkeeper)
    val termsOfServiceUrl = stringResource(id = R.string.termsOfServiceUrl)
    val privacyPolicyUrl = stringResource(id = R.string.privacyPolicyUrl)
    val allOurProducsUrl = stringResource(id = R.string.allOurProducsUrl)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = {
                    navController.popBackStack()
                }
            )
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .verticalScroll(state = scrollState)
    ) {
        WelcomeViewTitle()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp
                )
                .fillMaxWidth()
        ) {
            // CARD INFO
            MenuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                text = stringResource(R.string.cardInfo),
                textAlign = Alignment.TopStart,
                color = SatoDarkPurple,
                drawableId = R.drawable.cards_info,
                onClick = {
                    if (viewModel.isCardDataAvailable) {
                        navController.navigate(CardInformation)
                    } else {
                        showInfoDialog.value = !showInfoDialog.value
                    }
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp
                )
                .fillMaxWidth()
        ) {
            // MAKE A BACKUP
            MenuCard(
                modifier = Modifier
                    .weight(3f)
                    .heightIn(min = 110.dp),
                text = stringResource(R.string.makeBackup),
                textAlign = Alignment.TopStart,
                color = SatoLightPurple,
                drawableId = R.drawable.make_backup,
                onClick = {
                    if (viewModel.isCardDataAvailable) {
                        navController.navigate(BackupView)
                    } else {
                        showInfoDialog.value = !showInfoDialog.value
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            // SETTINGS
            MenuCard(
                modifier = Modifier
                    .weight(2f)
                    .heightIn(min = 110.dp),
                text = stringResource(R.string.settings),
                textAlign = Alignment.TopStart,
                color = SatoDarkPurple,
                drawableId = R.drawable.settings,
                onClick = {
                    navController.navigate(SettingsView)
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp
                )
                .fillMaxWidth()
        ) {
            // HOW TO USE SEEDKEEPER
            MenuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                text = stringResource(R.string.useSeedkeeper),
                textAlign = Alignment.TopStart,
                color = SatoLightPurple,
                drawableId = R.drawable.how_to
            ) {
                webviewActivityIntent(
                    url = howToUseSeedkeeperUrl,
                    context = context
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp
                )
                .fillMaxWidth()
        ) {
            // TERMS OF SERVICE
            MenuCard(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 60.dp),
                text = stringResource(R.string.tos),
                textAlign = Alignment.Center,
                color = SatoDarkPurple,
            ) {
                webviewActivityIntent(
                    url = termsOfServiceUrl,
                    context = context
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // PRIVACY POLICY
            MenuCard(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 60.dp),
                text = stringResource(R.string.privacyPolicy),
                textAlign = Alignment.Center,
                color = SatoDarkPurple,
            ) {
                webviewActivityIntent(
                    url = privacyPolicyUrl,
                    context = context
                )
            }
        }
        Spacer(
            modifier = Modifier
                .padding(20.dp)
                .height(2.dp)
                .width(150.dp)
                .background(SatoDividerPurple),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp)
                .padding(bottom = 50.dp)
                .background(
                    color = SatoPurple.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable {
                    webviewActivityIntent(
                        url = allOurProducsUrl,
                        context = context
                    )
                }
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 20.dp, start = 15.dp, bottom = 15.dp, end = 225.dp)
                    .align(Alignment.TopStart),
                color = Color.White,
                fontSize = 22.sp,
                text = stringResource(R.string.allOurProducs)
            )
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.all_our_products),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}