package org.satochip.seedkeeper.utils

fun String.countWords(): Int {
    return this.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
}

fun String.toMnemonicList(): List<String?> {
    return this.split("\\s+".toRegex())
}