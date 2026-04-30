package com.example.jedicouncilchallenge.presentation.splash

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jedicouncilchallenge.R
import com.example.jedicouncilchallenge.presentation.theme.StarWarsBackground
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val LightsaberBlue = Color(0xFF29A8FF)
private val LightsaberRed = Color(0xFFFF2828)

private const val LightsaberImageAspectRatio = 840f / 859f
private const val LightsaberEmitterX = 839f / 840f
private const val LightsaberEmitterY = 428f / 859f
private const val LeftLightsaberRotation = -45f
private const val RightLightsaberRotation = -135f
private const val BladeLengthMultiplier = 2f

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SplashScreen(
    isDarthVaderMode: Boolean,
    onSplashComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400L)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
        delay(700L)
        onSplashComplete()
    }

    val p = progress.value

    StarWarsBackground(isDarthVaderMode = isDarthVaderMode) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val logoOffsetY = -(maxHeight.value * 0.26f * p)

            // Star Wars logo — slides upward as blades extend
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = logoOffsetY.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_starwars),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.82f)
                )
            }

            // Left lightsaber hilt — rotated 45°
            Image(
                painter = painterResource(R.drawable.ic_lightsaber),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 48.dp)
                    .height(140.dp)
                    .graphicsLayer { rotationZ = -45f }
            )

            // Right lightsaber hilt — rotated 135°
            Image(
                painter = painterResource(R.drawable.ic_lightsaber),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 48.dp)
                    .height(140.dp)
                    .graphicsLayer { rotationZ = -135f }
            )

            // Animated blades — extend from hilt positions toward the crossing point
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (p > 0f) {
                    val leftHilt = lightsaberEmitterOffset(
                        isStartAligned = true,
                        rotationDegrees = LeftLightsaberRotation
                    )
                    val rightHilt = lightsaberEmitterOffset(
                        isStartAligned = false,
                        rotationDegrees = RightLightsaberRotation
                    )
                    val crossPt = lineIntersection(
                        originA = leftHilt,
                        directionA = unitVector(LeftLightsaberRotation),
                        originB = rightHilt,
                        directionB = unitVector(RightLightsaberRotation)
                    ) ?: Offset(size.width * 0.50f, size.height * 0.74f)

                    drawBlade(leftHilt, crossPt.extendedFrom(leftHilt), LightsaberBlue, p)
                    drawBlade(rightHilt, crossPt.extendedFrom(rightHilt), LightsaberRed, p)
                }
            }
        }
    }
}

private fun DrawScope.drawBlade(
    hilt: Offset,
    crossPoint: Offset,
    color: Color,
    progress: Float
) {
    val dx = crossPoint.x - hilt.x
    val dy = crossPoint.y - hilt.y
    val fullLen = sqrt(dx * dx + dy * dy)
    val nx = dx / fullLen
    val ny = dy / fullLen
    val tip = Offset(hilt.x + nx * fullLen * progress, hilt.y + ny * fullLen * progress)

    drawLine(
        color.copy(alpha = 0.20f),
        hilt,
        tip,
        strokeWidth = 32.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color.copy(alpha = 0.45f),
        hilt,
        tip,
        strokeWidth = 16.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(color, hilt, tip, strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)
    drawLine(
        Color.White.copy(alpha = 0.80f),
        hilt,
        tip,
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.lightsaberEmitterOffset(
    isStartAligned: Boolean,
    rotationDegrees: Float
): Offset {
    val hiltHeight = 140.dp.toPx()
    val hiltWidth = hiltHeight * LightsaberImageAspectRatio
    val horizontalPadding = 24.dp.toPx()
    val bottomPadding = 48.dp.toPx()
    val imageLeft = if (isStartAligned) {
        horizontalPadding
    } else {
        size.width - horizontalPadding - hiltWidth
    }
    val imageTop = size.height - bottomPadding - hiltHeight
    val center = Offset(
        x = imageLeft + hiltWidth / 2f,
        y = imageTop + hiltHeight / 2f
    )
    val emitter = Offset(
        x = imageLeft + hiltWidth * LightsaberEmitterX,
        y = imageTop + hiltHeight * LightsaberEmitterY
    )

    return emitter.rotateAround(center, rotationDegrees)
}

private fun unitVector(rotationDegrees: Float): Offset {
    val radians = rotationDegrees.toRadians()
    return Offset(
        x = cos(radians).toFloat(),
        y = sin(radians).toFloat()
    )
}

private fun Offset.rotateAround(center: Offset, rotationDegrees: Float): Offset {
    val radians = rotationDegrees.toRadians()
    val translatedX = x - center.x
    val translatedY = y - center.y
    val cos = cos(radians).toFloat()
    val sin = sin(radians).toFloat()

    return Offset(
        x = center.x + translatedX * cos - translatedY * sin,
        y = center.y + translatedX * sin + translatedY * cos
    )
}

private fun lineIntersection(
    originA: Offset,
    directionA: Offset,
    originB: Offset,
    directionB: Offset
): Offset? {
    val determinant = directionA.cross(directionB)
    if (determinant == 0f) return null

    val distanceAlongA = (originB - originA).cross(directionB) / determinant
    return originA + directionA * distanceAlongA
}

private fun Offset.cross(other: Offset): Float = x * other.y - y * other.x

private fun Offset.extendedFrom(origin: Offset): Offset =
    origin + (this - origin) * BladeLengthMultiplier

private fun Float.toRadians(): Double = this * PI / 180.0
