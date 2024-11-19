package org.satochip.seedkeeper.data

enum class PinCodeAction {
    ENTER_PIN_CODE, // ENTER_PIN_CODE -> end
    SETUP_PIN_CODE, // SETUP_PIN_CODE -> CONFIRM_PIN_CODE -> end
    CHANGE_PIN_CODE, // ENTER_PIN_CODE -> CHANGE_PIN_CODE -> CONFIRM_PIN_CODE -> end
    CONFIRM_PIN_CODE,
}