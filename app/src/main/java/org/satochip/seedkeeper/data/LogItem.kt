package org.satochip.seedkeeper.data

import java.util.Calendar
import java.util.logging.Level

data class LogItem(
    val level: Level,
    val tag: String,
    val msg: String,
) {
    val date = Calendar.getInstance().time
}
