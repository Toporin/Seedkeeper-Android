package org.satochip.seedkeeper.data

enum class NfcActionType {
    DO_NOTHING,
    SCAN_CARD,
    VERIFY_PIN,
    SETUP_CARD,
    GET_SECRETS_LIST,
    GET_SECRET,
    DELETE_SECRET,
    GENERATE_A_SECRET,
}