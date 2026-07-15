package dev.wceng.seaofvoice.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AtmosphereBackground(
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    if (colors.isEmpty()) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {}
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "AtmosphereRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAngle"
    )

    val animatedColors = colors.map { color ->
        animateColorAsState(
            targetValue = color,
            animationSpec = tween(2000),
            label = "ColorTransition"
        ).value
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.8f) // 80% strength as per design
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2

            // Create a fluid mixed animation using multiple gradients
            animatedColors.forEachIndexed { index, color ->
                val offsetAngle = angle + (index * PI.toFloat() * 2 / animatedColors.size)
                val radius = canvasWidth * 0.3f // Movement radius
                val x = centerX + radius * cos(offsetAngle)
                val y = centerY + radius * sin(offsetAngle)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(x, y),
                        radius = canvasWidth * 0.9f
                    ),
                    radius = canvasWidth * 0.9f,
                    center = Offset(x, y)
                )
            }
        }
        
        // Add a frost/blur effect overlay
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ) {}
    }
}
