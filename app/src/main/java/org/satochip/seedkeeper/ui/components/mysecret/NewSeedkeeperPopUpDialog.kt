package org.satochip.seedkeeper.ui.components.mysecret

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.MySecretItems
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.theme.SatoInactiveTracer
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun NewSeedkeeperPopUpDialog(
    isOpen: MutableState<Boolean>,
    title: Int,
    onClick: () -> Unit
) {
    if (!isOpen.value) return

    Dialog(
        onDismissRequest = {
            isOpen.value = !isOpen.value
        },
        properties = DialogProperties()
    ) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .heightIn(min = 250.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SatoPurple.copy(alpha = 0.5f)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = title),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    modifier = Modifier
                        .background(Color.Transparent, shape = CircleShape)
                        .satoClickEffect(
                            onClick = {
                                isOpen.value = !isOpen.value
                            }
                        )
                        .padding(16.dp)
                        .width(24.dp),
                    painter = painterResource(R.drawable.cancel),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    text = stringResource(id = R.string.oldSeedkeeper),
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                SatoButton(
                    modifier = Modifier,
                    onClick = {
                        onClick()
                    },
                    text = R.string.buySeedkeeper,
                    image = R.drawable.cart,
                )
            }
        }
    }
}