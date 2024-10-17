package org.satochip.seedkeeper.data

enum class NfcActionType {
    DO_NOTHING,
    SCAN_CARD,
    SETUP_CARD,
    SETUP_CARD_FOR_BACKUP,
    CHANGE_PIN,
    GET_SECRET,
    DELETE_SECRET,
    GENERATE_A_SECRET,
    SCAN_BACKUP_CARD,//TODO rename
    SCAN_MASTER_CARD,// TODO rename
    TRANSFER_TO_BACKUP,
    RESET_CARD,
    EDIT_CARD_LABEL
}