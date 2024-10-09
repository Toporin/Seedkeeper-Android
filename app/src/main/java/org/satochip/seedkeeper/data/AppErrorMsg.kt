package org.satochip.seedkeeper.data

import org.satochip.seedkeeper.R

enum class AppErrorMsg(val msg : Int) {
    OK(R.string.nfcOk),
    PLAINTEXT_EXPORT_NOT_ALLOWED(R.string.errorPlaintextExportNotAllowed),
}