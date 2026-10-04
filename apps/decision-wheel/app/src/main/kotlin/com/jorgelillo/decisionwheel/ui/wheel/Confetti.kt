package com.jorgelillo.decisionwheel.ui.wheel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.jorgelillo.decisionwheel.ui.theme.WheelColors
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Piece(val angle: Float, val speed: Float, val spin: Float, val colorIndex: Int, val wide: Boolean)

/** A short burst of confetti from the wheel's centre, replayed whenever [key] changes. */
@Composable
fun ConfettiBurst(key: Any?, modifier: Modifier = Modifier) {
    if (key == null) return
    val progress = remember(key) { Animatable(0f) }
    val pieces = remember(key) {
        List(48) {
            Piece(
                angle = Random.nextFloat() * 360f,
                speed = 0.55f + Random.nextFloat() * 0.6f,
                spin = Random.nextFloat() * 720f - 360f,
                colorIndex = Random.nextInt(WheelColors.Segments.size),
                wide = Random.nextBoolean(),
            )
        }
    }
    LaunchedEffect(key) { progress.animateTo(1f, tween(1400, easing = LinearEasing)) }

    Canvas(modifier) {
        val t = progress.value
        if (t >= 1f) return@Canvas
        val reach = size.minDimension * 0.6f
        val center = Offset(size.width / 2f, size.height / 2f)
        val gravity = size.height * 0.35f * t * t
        pieces.forEach { piece ->
            val rad = Math.toRadians(piece.angle.toDouble())
            val distance = reach * piece.speed * (1f - (1f - t) * (1f - t))
            val position = Offset(
                center.x + cos(rad).toFloat() * distance,
                center.y + sin(rad).toFloat() * distance + gravity,
            )
            val w = if (piece.wide) 10.dp.toPx() else 6.dp.toPx()
            rotate(piece.spin * t, pivot = position) {
                drawRect(
                    color = WheelColors.Segments[piece.colorIndex].copy(alpha = 1f - t * t),
                    topLeft = Offset(position.x - w / 2, position.y - 3.dp.toPx()),
                    size = Size(w, 6.dp.toPx()),
                )
            }
        }
    }
}
