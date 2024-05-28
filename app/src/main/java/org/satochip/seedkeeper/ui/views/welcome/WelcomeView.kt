package org.satochip.seedkeeper.ui.views.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.ui.components.shared.NextButton
import org.satochip.seedkeeper.ui.components.shared.SatoButton
import org.satochip.seedkeeper.ui.components.shared.StepCircles
import org.satochip.seedkeeper.ui.components.shared.WelcomeViewTitle

@Composable
fun WelcomeView(
    title: Int,
    text: Int,
    link: String? = null,
    colors: List<Color>? = null,
    backgroundImage: Int,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    when {
                        dragAmount > 0 -> {
                            onBack()
                        }

                        dragAmount < 0 -> {
                            onNext()
                        }
                    }
                    change.consume()
                }
            }
    ) {
        Image(
            painter = painterResource(backgroundImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            WelcomeViewTitle()
            Spacer(modifier = Modifier.height(37.dp))
            WelcomeViewContent(
                title = title,
                text = text,
                link = link
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 55.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    colors?.let {
                        NextButton(
                            onClick = onNext
                        )
                        StepCircles(colors)
                    } ?: run {
                        SatoButton(
                            onClick = onNext,
                            text = R.string.start
                        )
                    }
                }
            }
        }
    }
}
