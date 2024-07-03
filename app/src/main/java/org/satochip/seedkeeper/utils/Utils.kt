package org.satochip.seedkeeper.utils

import android.util.Patterns
import androidx.compose.runtime.MutableState
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.data.SeedkeeperPreferences

//Generate view
fun isClickable(
    secret: MutableState<String>,
    curValueLogin: MutableState<String>,
    curValueLabel: MutableState<String>
): Boolean {
    return secret.value.isNotEmpty() && curValueLabel.value.isNotEmpty() &&
            (curValueLogin.value.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(curValueLogin.value)
                .matches())
}

fun isEmailCorrect(
    curValueLogin: MutableState<String>
): Boolean {
    return curValueLogin.value.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(curValueLogin.value).matches()
}