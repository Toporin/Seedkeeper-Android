package org.satochip.seedkeeper.data

import org.satochip.seedkeeper.R

enum class AppErrorMsg(val msg : Int) {
    OK(R.string.nfcOk),
    LABEL_EMPTY(R.string.errorLabelEmpty),
    PASSWORD_EMPTY(R.string.errorPasswordEmpty),
    MNEMONIC_EMPTY(R.string.errorMnemonicEmpty),
    DESCRIPTOR_EMPTY(R.string.errorDescriptorEmpty),
    DATA_EMPTY(R.string.errorDataEmpty),
    CARD_LABEL_TOO_LONG(R.string.errorCardLabelTooLong), // TODO
    LABEL_TOO_LONG(R.string.errorLabelTooLong),
    PASSWORD_TOO_LONG(R.string.errorPasswordTooLong),
    MNEMONIC_TOO_LONG(R.string.errorMnemonicTooLong),
    DESCRIPTOR_TOO_LONG(R.string.errorDescriptorTooLong),
    DATA_TOO_LONG(R.string.errorDataTooLong),
    LOGIN_TOO_LONG(R.string.errorLoginTooLong),
    URL_TOO_LONG(R.string.errorUrlTooLong),
    PASSPHRASE_TOO_LONG(R.string.errorPassphraseTooLong),
    MNEMONIC_WRONG_FORMAT(R.string.errorMnemonicWrongFormat),
    SECRET_TOO_LONG_FOR_V1(R.string.errorSecretTooLongForV1),
    NO_CHAR_SELECTED(R.string.errorNoCharSelected),
    PLAINTEXT_EXPORT_NOT_ALLOWED(R.string.errorPlaintextExportNotAllowed),
    PIN_WRONG_FORMAT(R.string.errorPinWrongFormat),
}