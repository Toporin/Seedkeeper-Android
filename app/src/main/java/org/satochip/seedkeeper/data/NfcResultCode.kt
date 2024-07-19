package org.satochip.seedkeeper.data

import org.satochip.seedkeeper.R

enum class  NfcResultCode(val resTitle : Int, val resMsg : Int, val resImage : Int) {
    OK(R.string.nfcTitleSuccess, R.string.nfcOk, R.drawable.icon_check_gif),
    PIN_CHANGED(R.string.nfcTitleSuccess, R.string.nfcOk, R.drawable.icon_check_gif),
    UNKNOWN_ERROR(R.string.nfcTitleWarning, R.string.nfcErrorOccured, R.drawable.error_24px),
    NONE(R.string.scanning, R.string.nfcResultCodeNone, R.drawable.error_24px),
    BUSY(R.string.scanning, R.string.nfcResultCodeBusy, R.drawable.contactless_24px),
    NFC_ERROR(R.string.nfcTitleWarning, R.string.nfcResultCodeNfcError, R.drawable.error_24px),
    REQUIRE_SETUP(R.string.nfcTitleSuccess, R.string.nfcTitleSuccess, R.drawable.error_24px),
    WRONG_PIN(R.string.nfcTitleWarning, R.string.nfcWrongPin, R.drawable.error_24px),
    CARD_BLOCKED(R.string.nfcTitleWarning, R.string.nfcCardBlocked, R.drawable.error_24px),
}