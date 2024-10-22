package org.satochip.seedkeeper.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoLightGrey
import org.satochip.seedkeeper.ui.theme.SatoNfcBlue

@Composable
fun DrawerScreen(
    closeSheet: () -> Unit,
    closeDrawerButton: Boolean = false,
    title: Int? = null,
    message: Int? = null,
    progress: Float? = null,
    image: Int? = null,
    colorFilter: ColorFilter? = null,
    triesLeft: Int? = null
) {
    Column(
        modifier = Modifier
            .height(350.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        title?.let {
            Text(
                text = stringResource(it),
                style = TextStyle(
                    color = SatoLightGrey,
                    fontSize = 26.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // superpose progress bar with gif image
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            image?.let {
                GifImage(
                    modifier = Modifier.size(125.dp),
                    image = image,
                    colorFilter = colorFilter
                )
            }
            progress?.let {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(125.dp), //Modifier.fillMaxWidth(fraction = 0.5f),
                    color = SatoNfcBlue,
                    trackColor = Color.White,
                    strokeWidth = 8.dp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        message?.let {
            Text(
                text = stringResource(message) + if (triesLeft != null && triesLeft > 0) " $triesLeft" else "",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (closeDrawerButton) {
            SatoButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = closeSheet,
                text = R.string.cancel,
                buttonColor = SatoLightGrey,
                textColor = Color.Black,
                shape = RoundedCornerShape(20)
            )
        }
    }
}