package org.satochip.seedkeeper.data

enum class NfcActionType {
    DO_NOTHING,
    SCAN_CARD,
    SETUP_CARD,
    SETUP_CARD_FOR_BACKUP,
    CHANGE_PIN,
    EXPORT_SECRET,
    DELETE_SECRET,
    IMPORT_SECRET,
    EDIT_CARD_LABEL,
    CARD_LOGS,
    SCAN_BACKUP_CARD,
    EXPORT_SECRETS_FROM_MASTER,
    TRANSFER_TO_BACKUP,
    RESET_CARD,
}