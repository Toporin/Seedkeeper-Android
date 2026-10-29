package org.satochip.seedkeeper.services

import android.util.Log
import org.satochip.seedkeeper.data.LogItem
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

object SatoLog {
    var logList = mutableListOf<LogItem>()
    var isDebugModeActivated = false

    fun addLog(level: Level, tag: String = "", msg: String) {
        val log = LogItem(level= level, tag= tag, msg= msg)
        logList.add(log)
    }
    fun emptyList() {
        logList.clear()
    }

    // satochip-lib logger (java.util.logging), keep a reference so that its configuration is not garbage collected
    private val libraryLogger: Logger = Logger.getLogger("org.satochip.client")
    private var isLibraryHandlerSet = false

    /**
     * Routes satochip-lib logs to SatoLog, so they follow the debug mode like the app logs
     * (logcat and in-app log list in debug mode, nothing otherwise).
     *
     * Detailed library logs are at FINE level. Setting the logger level is not enough on Android:
     * the default JUL handler maps FINE to Log.DEBUG, which logcat drops for this tag by default.
     * Since satochip-lib v0.3.6, no secret or personal data is logged at any level.
     */
    fun configureLibraryLogging() {
        if (!isLibraryHandlerSet) {
            libraryLogger.useParentHandlers = false // avoid duplicates through the default handler
            libraryLogger.addHandler(object : Handler() {
                override fun publish(record: LogRecord) {
                    val tag = record.loggerName ?: "satochip-lib"
                    val msg = record.message ?: return
                    when {
                        record.level.intValue() >= Level.SEVERE.intValue() -> e(tag, msg)
                        record.level.intValue() >= Level.WARNING.intValue() -> w(tag, msg)
                        else -> d(tag, msg)
                    }
                }
                override fun flush() {}
                override fun close() {}
            })
            isLibraryHandlerSet = true
        }
        libraryLogger.level = if (isDebugModeActivated) Level.FINE else Level.WARNING
    }

    fun e(tag: String, msg: String) {
        if (isDebugModeActivated) {
            Log.e(tag, msg)
            this.addLog(level= Level.SEVERE, tag= tag, msg= msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (isDebugModeActivated) {
            Log.w(tag, msg)
            this.addLog(level= Level.WARNING, tag= tag, msg= msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isDebugModeActivated) {
            Log.i(tag, msg)
            this.addLog(level = Level.INFO, tag = tag, msg = msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (isDebugModeActivated) {
            Log.d(tag, msg)
            this.addLog(level = Level.CONFIG, tag = tag, msg = msg)
        }
    }
}