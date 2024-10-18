package org.satochip.seedkeeper.data

enum class PinCodeStatus {
    CURRENT_PIN_CODE,
    INPUT_NEW_PIN_CODE,
    CONFIRM_PIN_CODE,
    WRONG_PIN_CODE
}

enum class PinCodeAction {
    ENTER_PIN_CODE, // ENTER_PIN_CODE -> end
    SETUP_PIN_CODE, // SETUP_PIN_CODE -> CONFIRM_PIN_CODE -> end
    CHANGE_PIN_CODE, // ENTER_PIN_CODE -> CHANGE_PIN_CODE -> CONFIRM_PIN_CODE -> end
    CONFIRM_PIN_CODE,
}