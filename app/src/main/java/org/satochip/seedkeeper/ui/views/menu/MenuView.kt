package org.satochip.seedkeeper.ui.views.menu

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle
import org.satochip.seedkeeper.ui.theme.SatoCardPurple
import org.satochip.seedkeeper.ui.theme.SatoDarkPurple
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoLightPurple

@Composable
fun MenuView(
    onClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
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
                .fillMaxSize()
        ) {
            HeaderAlternateRow(
                onClick = onClick
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
                drawableId = R.drawable.cards_info
            ) {
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
            // MAKE A BACKUP
            MenuCard(
                modifier = Modifier
                    .width(220.dp)
                    .height(110.dp),
                text = stringResource(R.string.makeBackup),
                textAlign = Alignment.TopStart,
                color = SatoLightPurple,
                drawableId = R.drawable.make_backup
            ) {
            }
            // SETTINGS
            MenuCard(
                modifier = Modifier
                    .width(150.dp)
                    .height(110.dp),
                text = stringResource(R.string.settings),
                textAlign = Alignment.TopStart,
                color = SatoDarkPurple,
                drawableId = R.drawable.settings
            ) {
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
//                    webviewActivityIntent(
//                        url = "https://satochip.io/setup-use-satodime-on-mobile/",
//                        context = context
//                    )
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
                    .width(180.dp)
                    .height(60.dp),
                text = stringResource(R.string.tos),
                textAlign = Alignment.Center,
                color = SatoDarkPurple,
            ) {
//                    webviewActivityIntent(
//                        url = "https://satochip.io/terms-of-service/",
//                        context = context
//                    )
            }
            // PRIVACY POLICY
            MenuCard(
                modifier = Modifier
                    .width(180.dp)
                    .height(60.dp),
                text = stringResource(R.string.privacyPolicy),
                textAlign = Alignment.Center,
                color = SatoDarkPurple,
            ) {
//                    webviewActivityIntent(
//                        url = "https://satochip.io/privacy-policy/",
//                        context = context
//                    )
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
                    color = SatoCardPurple,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable {
//                        webviewActivityIntent(
//                            url = "https://satochip.io/shop/",
//                            context = context
//                        )
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