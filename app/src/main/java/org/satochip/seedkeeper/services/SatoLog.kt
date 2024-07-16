package org.satochip.seedkeeper.services

import android.util.Log
import org.satochip.seedkeeper.data.LogItem
import java.util.logging.Level

object SatoLog {
    var logList = mutableListOf<LogItem>()

    fun addLog(level: Level, tag: String = "", msg: String) {
        val log = LogItem(level= level, tag= tag, msg= msg)
        logList.add(log)
    }
    fun emptyList() {
        logList.clear()
    }

    fun e(tag: String, msg: String) {
        Log.e(tag, msg)
        this.addLog(level= Level.SEVERE, tag= tag, msg= msg)
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
        this.addLog(level= Level.WARNING, tag= tag, msg= msg)
    }

    fun i(tag: String, msg: String) {
        Log.i(tag, msg)
        this.addLog(level = Level.INFO, tag = tag, msg = msg)
    }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
        this.addLog(level = Level.CONFIG, tag = tag, msg = msg)
    }
}