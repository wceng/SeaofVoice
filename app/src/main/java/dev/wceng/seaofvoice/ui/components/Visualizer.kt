package dev.wceng.seaofvoice.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

/**
 * A linear visualizer that mimics an ECG/heartbeat style.
 * Displays bars horizontally based on live frequency data.
 */
@Composable
fun LinearVisualizer(
    magnitudes: FloatArray,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val barCount = magnitudes.size

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / barCount
        val centerY = height / 2

        for (i in 0 until barCount) {
            val magnitude = magnitudes[i]
            // Calculate height (symmetric around center for ECG look)
            val barHeight = (height * 0.8f * magnitude).coerceAtLeast(4.dp.toPx())
            
            val x = i * barWidth + barWidth / 2
            
            drawLine(
                color = color.copy(alpha = 0.4f + (magnitude * 0.6f)),
                start = Offset(x, centerY - barHeight / 2),
                end = Offset(x, centerY + barHeight / 2),
                strokeWidth = (barWidth * 0.6f).coerceAtMost(4.dp.toPx()),
                cap = StrokeCap.Round
            )
        }
    }
}
