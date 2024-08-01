package org.satochip.seedkeeper.ui.views.showlogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
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
import org.satochip.seedkeeper.services.SatoLog
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.utils.satoClickEffect

private const val TAG = "ShowLogsView"

@Composable
fun ShowLogsView(
    onClick: () -> Unit,
    copyToClipboard: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderAlternateRow(
            onClick = {
                onClick()
            },
            titleText = R.string.logs
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .verticalScroll(state = scrollState)
                .border(
                    color = Color.Black,
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
                    text = stringResource(id = R.string.logsEntriesnumber) + " ${SatoLog.logList.size} ",
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
                                var logsText = ""
                                for (log in SatoLog.logList) {
                                    val logString =
                                        "${log.date}; ${log.level.name}; ${log.tag}; ${log.msg} \n"
                                    logsText = logsText + logString
                                }
                                copyToClipboard(logsText)

                            }
                        ),
                    colorFilter = ColorFilter.tint(Color.Black),
                    image = R.drawable.copy_icon
                )
            }
            Spacer(modifier = Modifier.height(35.dp))
            SatoLog.logList.forEach { log ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.Black,
                        text = "${log.date} - ${log.level}"
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = log.tag
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = log.msg
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}