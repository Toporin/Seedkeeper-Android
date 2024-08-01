package org.satochip.seedkeeper.ui.components.shared

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import io.github.g0dkar.qrcode.QRCode

@Composable
fun DataAsQrCode(data: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(10.dp)
    ) {
        val qrCode = QRCode(data).render().getBytes()
        val bitmapQrCode = BitmapFactory.decodeByteArray(qrCode, 0, qrCode.size)
        Image(
            modifier = Modifier
                .background(Color.White)
                .padding(5.dp),
            bitmap = bitmapQrCode.asImageBitmap(),
            contentDescription = null,
        )
    }
}