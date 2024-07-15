package org.satochip.seedkeeper.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import org.satochip.seedkeeper.data.PasswordOptions
import org.satochip.seedkeeper.data.StringConstants
import java.io.BufferedReader
import java.io.InputStreamReader

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

    fun generateMemorablePassword(options: PasswordOptions, context: Context): String {
        val password = StringBuilder()
        val wordList = getWordList(context)

        for (i in 0 until options.passwordLength) {
            var randomMnemonic = wordList.random()
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


    private fun getWordList(context: Context): List<String> {
        val wordList = mutableListOf<String>()
        context.assets.open("password-replacement.txt").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.forEachLine { line ->
                    wordList.add(line)
                }
            }
        }
        return wordList
    }
}