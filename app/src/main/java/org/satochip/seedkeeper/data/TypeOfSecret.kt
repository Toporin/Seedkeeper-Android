package org.satochip.seedkeeper.data

enum class TypeOfSecret(val value: String) {
    TYPE_OF_SECRET("typeOfSecret"),
    MNEMONIC_PHRASE("mnemonicPhrase"),
    LOGIN_PASSWORD("loginPassword");

    companion object {
        fun valueOfKey(value: String) = TypeOfSecret.values().first { it.value == value }
    }
}