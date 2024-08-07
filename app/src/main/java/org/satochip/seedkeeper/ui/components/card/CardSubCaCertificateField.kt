package org.satochip.seedkeeper.ui.components.card

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.AuthenticityStatus
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.theme.SatoGreen
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun CardSubCaCertificateField(
    certificates: List<String>,
    authenticityStatus: AuthenticityStatus,
    copyToClipboard: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp, top = 16.dp)
            .border(
                color = if (authenticityStatus == AuthenticityStatus.AUTHENTIC) SatoGreen else Color.Red,
                width = 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.copyToClipboard) + " ",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            )
            GifImage(
                modifier = Modifier
                    .size(16.dp)
                    .satoClickEffect(
                        onClick = {
                            copyToClipboard(certificates[2])
                        }
                    ),
                colorFilter = ColorFilter.tint(Color.Black),
                image = R.drawable.copy_icon
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        //CARD CERT
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.subcaInfo),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = certificates[2] ?: "",
            style = TextStyle(
                color = Color.Black,
                fontSize = 12.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}