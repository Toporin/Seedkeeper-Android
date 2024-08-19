package org.satochip.seedkeeper.ui.views.showcardlogs

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
import org.satochip.client.seedkeeper.SeedkeeperLog
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.GifImage
import org.satochip.seedkeeper.ui.components.shared.HeaderAlternateRow
import org.satochip.seedkeeper.utils.instructionsMap
import org.satochip.seedkeeper.utils.satoClickEffect

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ShowCardLogsView(
    onClick: () -> Unit,
    cardLogs: List<SeedkeeperLog>,
    copyToClipboard: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val filteredLogs = cardLogs.filter { log -> log.sw != 0 }

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
            titleText = R.string.cardLogs
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
                    text = stringResource(id = R.string.logsEntriesnumber) + " ${filteredLogs.size} ",
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
                                for (log in filteredLogs) {
                                    val logString =
                                        "${instructionsMap[log.ins]}; ${log.sw.toHexString()}; ${log.sid1}; ${log.sid2} \n"
                                    logsText += logString
                                }
                                copyToClipboard(logsText)
                            }
                        ),
                    colorFilter = ColorFilter.tint(Color.Black),
                    image = R.drawable.copy_icon
                )
            }
            Spacer(modifier = Modifier.height(35.dp))
            filteredLogs.forEach { log ->
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
                        text = "${instructionsMap[log.ins]}"
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = "sw: ${log.sw.toHexString()}"
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        text = "sid1: ${log.sid1} \n sid2: ${log.sid2}"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}