package org.satochip.seedkeeper.data

enum class NfcActionType {
    DO_NOTHING,
    SCAN_CARD,
    SETUP_CARD,
    VERIFY_PIN,
    CHANGE_PIN,
    GET_SECRETS_LIST,
    GET_SECRET,
    DELETE_SECRET,
    GENERATE_A_SECRET,
    EDIT_CARD_LABEL
}