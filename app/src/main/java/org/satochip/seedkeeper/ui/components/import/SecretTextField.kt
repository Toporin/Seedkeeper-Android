package org.satochip.seedkeeper.ui.components.import

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.g0dkar.qrcode.QRCode
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.parsers.TAG
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.shared.DataAsQrCode
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.utils.satoClickEffect
import org.satochip.seedkeeper.utils.getSeedQr

@Composable
fun SecretTextField(
    isEditable: Boolean = false,
    curValue: MutableState<String>,
    placeholder: String = "",
    containerColor: Color = SatoDividerPurple.copy(alpha = 0.2f),
    isQRCodeEnabled: Boolean = true,
    isSeedQRCodeEnabled: Boolean = false,
    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
    minHeight: Dp = 150.dp
) {
    val context = LocalContext.current as Activity
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordVisibility = remember {
        mutableStateOf(visualTransformation == PasswordVisualTransformation())
    }
    val isQRCodeSelected = remember {
        mutableStateOf(false)
    }
    val isSeedQRCodeSelected = remember {
        mutableStateOf(false)
    }
    val copyText = stringResource(id = R.string.copiedToClipboard)

    Box(
        modifier = Modifier
            .heightIn(
                min = minHeight
            )
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {

        if (curValue.value != "") {

            // UPPER-LEFT QR descriptions
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .zIndex(1f)
                    .align(Alignment.TopStart)
            ) {
                if (isSeedQRCodeEnabled) {
                    if (isSeedQRCodeSelected.value) {
                        Text(
                            text = "SeedQR",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraLight,
                                textAlign = TextAlign.Start
                            )
                        )
                    }
                }
                if (isQRCodeEnabled) {
                    if (isQRCodeSelected.value) {
                        Text(
                            text = "QRcode",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraLight,
                                textAlign = TextAlign.Start
                            )
                        )
                    }
                }
            }

            // UPPER-RIGHT ICONS
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    //.width(if (isQRCodeEnabled) 96.dp else 64.dp)
                    .zIndex(1f)
                    .align(Alignment.TopEnd)
            ) {
                if (isSeedQRCodeEnabled) {
                    // SeedQR code image
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                            .satoClickEffect(
                                onClick = {
                                    isSeedQRCodeSelected.value = true //!isSeedQRCodeSelected.value
                                    isQRCodeSelected.value = false
                                }
                            ),
                        painter = painterResource(id = R.drawable.pill),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                if (isQRCodeEnabled) {
                    // QR code image
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                            .satoClickEffect(
                                onClick = {
                                    isSeedQRCodeSelected.value = false
                                    isQRCodeSelected.value = true // !isQRCodeSelected.value
                                }
                            ),
                        painter = painterResource(id = R.drawable.seedqr_icon),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                // Copy-to-clipboard
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp)
                        .satoClickEffect(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(curValue.value))
                                Toast
                                    .makeText(context, copyText, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        ),
                    painter = painterResource(id = R.drawable.copy_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                // Show/hide password
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp)
                        .satoClickEffect(
                            onClick = {
                                isQRCodeSelected.value = false
                                isSeedQRCodeSelected.value = false
                                passwordVisibility.value = !passwordVisibility.value
                            }
                        ),
                    painter = painterResource(id = if (passwordVisibility.value) R.drawable.show_password else R.drawable.hide_password),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        if (isQRCodeSelected.value) {
            // QR-CODE
            val qrCodeBytes = QRCode(curValue.value).render().getBytes()
            DataAsQrCode(
                qrCodeBytes = qrCodeBytes
            )
        } else if (isSeedQRCodeSelected.value) {
            // SEEDQR
            var qrCodeString: String? = null
            try {
                qrCodeString = getSeedQr(curValue.value)
            } catch (e: Exception) {
                SatoLog.e(TAG, "Failed to generate SeedQR: ${e}")
            }
            qrCodeString?.also { qrCodeString ->
               val qrCodeBytes = QRCode(qrCodeString).render().getBytes()
                DataAsQrCode(
                    qrCodeBytes = qrCodeBytes
                )
            } ?: run {
                Text(
                    text = "Failed to generate SeedQR",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                        .zIndex(0.5f),
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }

        } else {
            // SECRET TEXT
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp, horizontal = 16.dp)
                    .zIndex(0.5f),
                enabled = isEditable,
                value = curValue.value,
                onValueChange = {
                    curValue.value = it
                },
                placeholder = {Text(placeholder)},
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text
                ),
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else visualTransformation,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledTextColor = Color.Black,
                ),
                minLines = 1,
            )
        }
    }
}