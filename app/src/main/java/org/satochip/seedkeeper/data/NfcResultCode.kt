package org.satochip.seedkeeper.data

import org.satochip.seedkeeper.R

enum class  NfcResultCode(val resTitle : Int, val resMsg : Int, val resImage : Int, var triesLeft: Int? = null) {
    OK(R.string.nfcTitleSuccess, R.string.nfcOk, R.drawable.icon_check_gif, null),
    SECRET_DELETED(R.string.nfcTitleSuccess, R.string.nfcSecretDeleted, R.drawable.icon_check_gif, null),
    PIN_CHANGED(R.string.nfcTitleSuccess, R.string.nfcOk, R.drawable.icon_check_gif, null),
    PIN_VERIFIED(R.string.nfcTitleSuccess, R.string.pinVerified, R.drawable.icon_check_gif, null),
    SECRET_HEADER_LIST_SET(R.string.nfcTitleSuccess, R.string.secretHeaderListSet, R.drawable.icon_check_gif, null),
    SECRET_IMPORTED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.secretImportedSuccessfully, R.drawable.icon_check_gif, null),
    SECRET_FETCHED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.secretFetchedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_LABEL_CHANGED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.cardLabelChangedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_SETUP_SUCCESSFUL(R.string.nfcTitleSuccess, R.string.cardSetupSuccessful, R.drawable.icon_check_gif, null),
    CARD_SUCCESSFULLY_SCANNED(R.string.nfcTitleSuccess, R.string.cardSuccessfullyScanned, R.drawable.icon_check_gif, null),
    CARD_SUCCESSFULLY_BACKED_UP(R.string.nfcTitleSuccess, R.string.cardSuccessfullyBackedUp, R.drawable.icon_check_gif, null),
    UNKNOWN_ERROR(R.string.nfcTitleWarning, R.string.nfcErrorOccured, R.drawable.error_24px, null),
    NONE(R.string.scanning, R.string.nfcResultCodeNone, R.drawable.error_24px, null),
    BUSY(R.string.scanning, R.string.nfcResultCodeBusy, R.drawable.contactless_24px, null),
    NFC_ERROR(R.string.nfcTitleWarning, R.string.nfcResultCodeNfcError, R.drawable.error_24px, null),
    REQUIRE_SETUP(R.string.nfcTitleSuccess, R.string.nfcTitleSuccess, R.drawable.error_24px, null),
    WRONG_PIN(R.string.nfcTitleWarning, R.string.nfcWrongPin, R.drawable.error_24px, null),
    CARD_BLOCKED(R.string.nfcTitleWarning, R.string.nfcCardBlocked, R.drawable.error_24px, null),
}