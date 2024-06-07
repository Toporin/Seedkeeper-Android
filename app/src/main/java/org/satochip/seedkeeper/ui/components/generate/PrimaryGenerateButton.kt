package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.GenerateStatus
import org.satochip.seedkeeper.ui.components.shared.SatoButton

@Composable
fun PrimaryGenerateButton(
    generateStatus: MutableState<GenerateStatus>,
    onClick: () -> Unit
) {
    SatoButton(
        onClick = {
            onClick()
        },
        text = when (generateStatus.value) {
            GenerateStatus.DEFAULT -> {
                R.string.next
            }
            GenerateStatus.MNEMONIC_PHRASE_SECOND_STEP, GenerateStatus.LOGIN_PASSWORD_SECOND_STEP -> {
                R.string.importButton
            }
            GenerateStatus.HOME -> {
                R.string.home
            }
            else -> {
                R.string.generate
            }
        }
    )
}