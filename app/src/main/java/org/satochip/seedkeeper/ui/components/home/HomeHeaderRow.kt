package org.satochip.seedkeeper.ui.components.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.satochip.seedkeeper.CardAuthenticity
import org.satochip.seedkeeper.MenuView
import org.satochip.seedkeeper.PinCodeView
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.ui.components.shared.InfoPopUpDialog
import org.satochip.seedkeeper.ui.theme.SatoGreen
import org.satochip.seedkeeper.viewmodels.SharedViewModel

@Composable
fun HomeHeaderRow(
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

    // Logo
    val logoColor = remember {
        mutableStateOf(Color.Black)
    }
    logoColor.value = when (viewModel.authenticityStatus) {
        AuthenticityStatus.AUTHENTIC -> {
            SatoGreen
        }
        AuthenticityStatus.NOT_AUTHENTIC -> {
            Color.Red
        }
        AuthenticityStatus.UNKNOWN -> {
            Color.Black
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 5.dp, start = 20.dp, end = 5.dp)
            .height(50.dp)
    ) {
        // LOGO
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = {
                if (viewModel.isCardDataAvailable) {
                    navController.navigate(CardAuthenticity)
                } else {
                    showInfoDialog.value = !showInfoDialog.value
                }
            },
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sato_small),
                contentDescription = "logo",
                modifier = Modifier
                    .size(45.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(logoColor.value)
            )
        }
        // TITLE
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.seedkeeper),
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                lineHeight = 34.sp,
            ),
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            if (viewModel.isCardDataAvailable) {
                // RESCAN BUTTON
                IconButton(
                    onClick = {
                        //viewModel.setIsReadyForPinCode()
                        viewModel.setResultCodeLiveTo()
                        navController.navigate(
                            PinCodeView(
                                title = R.string.pinCode,
                                messageTitle = R.string.pinCode,
                                message = R.string.enterPinCodeText,
                                placeholderText = R.string.enterPinCode,
                            )
                        )
                    },
                ) {
                    Image(
                        painter = painterResource(R.drawable.refresh_button),
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(24.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
            }
            // MENU BUTTON
            IconButton(onClick = {
                navController.navigate(MenuView)
            }) {
                Icon(Icons.Default.MoreVert, "", tint = Color.Black)
            }
        }
    }
}