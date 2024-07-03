package org.satochip.seedkeeper.viewmodels

import androidx.lifecycle.ViewModel
import org.bitcoinj.crypto.MnemonicCode
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.StringConstants

class SharedViewModel : ViewModel() {

    fun generatePassword(options: PasswordOptions): String? {
        var characterSet = ""
        val password = StringBuilder()

        if (options.isLowercaseSelected)
            characterSet += StringConstants.LOWERCASE.value

        if (options.isUppercaseSelected)
            characterSet += StringConstants.UPPERCASE.value

        if (options.isNumbersSelected)
            characterSet += StringConstants.NUMBERS.value

        if (options.isSymbolsSelected)
            characterSet += StringConstants.SYMBOLS.value

        if (characterSet.isEmpty())
            return null


        for (i in 0 until options.passwordLength) {
            val randomIndex = (characterSet.indices).random()
            password.append(characterSet[randomIndex])
        }

        return password.toString()
    }

    fun generateMemorablePassword(options: PasswordOptions): String {
        val password = StringBuilder()

        for (i in 0 until options.passwordLength) {
            var randomMnemonic = MnemonicCode.INSTANCE.wordList.random()
            if (options.isUppercaseSelected) {
                randomMnemonic = randomMnemonic.capitalize()
            }
            password.append(randomMnemonic)
            if (i != options.passwordLength - 1) {
                if (options.isNumbersSelected)
                    password.append(StringConstants.NUMBERS.value.random())
                if (options.isSymbolsSelected)
                    password.append(StringConstants.SYMBOLS.value.random())
                if (!options.isNumbersSelected && !options.isSymbolsSelected)
                    password.append("-")
            }
        }

        return password.toString()
    }
}