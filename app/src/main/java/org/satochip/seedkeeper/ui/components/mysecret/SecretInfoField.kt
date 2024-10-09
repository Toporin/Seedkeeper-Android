package org.satochip.seedkeeper.ui.components.mysecret

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.theme.SatoPurple
import org.satochip.seedkeeper.utils.satoClickEffect

@Composable
fun SecretInfoField(
    title: Int,
    optional: Int? = null,
    text: String
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current as Activity
    val copyText = stringResource(id = R.string.copiedToClipboard)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicText(
            text = buildAnnotatedString {
                append(stringResource(id = title))
                optional?.let {

                    withStyle(
                        style = SpanStyle(
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                        )
                    ) {
                        append(" ")
                        append(stringResource(id = optional))
                    }
                }
                append(":")
            },
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraLight,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = SatoPurple.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            )

            Spacer(modifier = Modifier)

            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp)
                    .satoClickEffect(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, copyText, Toast.LENGTH_SHORT).show()
                        }
                    ),
                painter = painterResource(id = R.drawable.copy_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White)
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}