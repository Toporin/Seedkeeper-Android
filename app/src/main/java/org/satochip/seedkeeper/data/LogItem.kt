package org.satochip.seedkeeper.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.Level

data class LogItem(
    val level: Level,
    val tag: String,
    val msg: String,
) {
    val date: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
}
