package org.satochip.seedkeeper.data

import org.satochip.seedkeeper.R

enum class  NfcResultCode(val resTitle : Int, val resMsg : Int, val resImage : Int, var triesLeft: Int? = null) {
    OK(R.string.nfcTitleSuccess, R.string.nfcOk, R.drawable.icon_check_gif, null),
    SECRET_DELETED(R.string.nfcTitleSuccess, R.string.nfcSecretDeleted, R.drawable.icon_check_gif, null),
    PIN_CHANGED(R.string.nfcTitleSuccess, R.string.pinChangedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_RESET(R.string.nfcTitleSuccess, R.string.cardResetSuccess, R.drawable.icon_check_gif, null),
    CARD_RESET_CANCELLED(R.string.nfcTitleInfo, R.string.cardResetCancel, R.drawable.icon_check_gif, null),
    CARD_RESET_SENT(R.string.nfcTitleSuccess, R.string.cardResetSent, R.drawable.icon_check_gif, 0),
    CARD_SCANNED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.cardSuccessfullyScanned, R.drawable.icon_check_gif, null),
    SECRET_IMPORTED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.secretImportedSuccessfully, R.drawable.icon_check_gif, null),
    SECRET_EXPORTED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.secretFetchedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_LABEL_CHANGED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.cardLabelChangedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_LOGS_FETCHED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.cardLogsFetchedSuccessfully, R.drawable.icon_check_gif, null),
    CARD_SETUP_SUCCESSFUL(R.string.nfcTitleSuccess, R.string.cardSetupSuccessful, R.drawable.icon_check_gif, null),
    CARD_SETUP_FOR_BACKUP_SUCCESSFUL(R.string.nfcTitleSuccess, R.string.cardSetupSuccessful, R.drawable.icon_check_gif, null),
    BACKUP_CARD_SCANNED_SUCCESSFULLY(R.string.nfcTitleSuccess, R.string.cardSuccessfullyScanned, R.drawable.icon_check_gif, null),
    SECRETS_EXPORTED_SUCCESSFULLY_FROM_MASTER(R.string.nfcTitleSuccess, R.string.cardSuccessfullyScanned, R.drawable.icon_check_gif, null),
    CARD_SUCCESSFULLY_BACKED_UP(R.string.nfcTitleSuccess, R.string.cardSuccessfullyBackedUp, R.drawable.icon_check_gif, null),
    NONE(R.string.scanning, R.string.nfcResultCodeNone, R.drawable.error_24px, null),
    BUSY(R.string.scanning, R.string.nfcResultCodeBusy, R.drawable.contactless_24px, null),
    NFC_ERROR(R.string.nfcTitleWarning, R.string.nfcResultCodeNfcError, R.drawable.error_24px, null),
    CARD_ERROR(R.string.nfcTitleWarning, R.string.cardError, R.drawable.error_24px, null),
    REQUIRE_SETUP(R.string.nfcTitleInfo, R.string.cardNotInitialized, R.drawable.error_24px, null),
    REQUIRE_SETUP_FOR_BACKUP(R.string.nfcTitleInfo, R.string.backupCardNotInitialized, R.drawable.error_24px, null),
    WRONG_PIN(R.string.nfcTitleWarning, R.string.nfcWrongPin, R.drawable.error_24px, null),
    CARD_BLOCKED(R.string.nfcTitleWarning, R.string.nfcCardBlocked, R.drawable.error_24px, null),
    CARD_MISMATCH(R.string.nfcTitleWarning, R.string.cardMismatch, R.drawable.error_24px, null),
    NO_MEMORY_LEFT(R.string.nfcTitleWarning, R.string.noMemoryLeft, R.drawable.error_24px, null),
    SECRET_TOO_LONG(R.string.nfcTitleWarning, R.string.secretTooLong, R.drawable.error_24px, null),

}