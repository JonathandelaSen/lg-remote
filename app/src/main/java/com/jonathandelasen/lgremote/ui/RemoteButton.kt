package com.jonathandelasen.lgremote.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Tras este tiempo pulsado empieza a repetirse, una vez cada [REPEAT_EVERY_MS]. */
private const val REPEAT_AFTER_MS = 400L
private const val REPEAT_EVERY_MS = 110L

/**
 * Botón del mando. Se dispara al tocar (no al soltar), como un mando de verdad,
 * y si [repeats] es `true` se repite mientras se mantiene pulsado.
 */
@Composable
fun RemoteButton(
    description: String,
    onPress: (isRepeat: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = Palette.KeyShape,
    color: Color = Palette.Key,
    repeats: Boolean = false,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val currentOnPress by rememberUpdatedState(onPress)
    var pressed by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .background(if (pressed) lerpToWhite(color) else color)
            .semantics {
                role = Role.Button
                contentDescription = description
                onClick { currentOnPress(false); true }
            }
            .pointerInput(repeats) {
                awaitEachGesture {
                    awaitFirstDown()
                    pressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    currentOnPress(false)
                    val repeater = if (repeats) {
                        scope.launch {
                            delay(REPEAT_AFTER_MS)
                            while (isActive) {
                                currentOnPress(true)
                                delay(REPEAT_EVERY_MS)
                            }
                        }
                    } else {
                        null
                    }
                    waitForUpOrCancellation()
                    repeater?.cancel()
                    pressed = false
                }
            },
    ) {
        content()
    }
}

@Composable
fun KeyLabel(text: String, size: TextUnit = 15.sp, color: Color = Palette.OnKey) {
    Text(text = text, color = color, fontSize = size, fontWeight = FontWeight.SemiBold, maxLines = 1)
}

/** Color del botón mientras está pulsado: un poco más claro (o un velo si es transparente). */
private fun lerpToWhite(color: Color): Color =
    if (color.alpha < 0.5f) Color.White.copy(alpha = 0.10f) else Color(
        red = color.red + (1f - color.red) * 0.18f,
        green = color.green + (1f - color.green) * 0.18f,
        blue = color.blue + (1f - color.blue) * 0.18f,
        alpha = color.alpha,
    )
