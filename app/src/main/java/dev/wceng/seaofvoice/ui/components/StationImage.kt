package dev.wceng.seaofvoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

@Composable
fun StationImage(
    url: String?,
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (url.isNullOrBlank() || isError) {
            LetterAvatar(name = name)
        } else {
            AsyncImage(
                model = url,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (isLoading) Modifier.shimmer() else Modifier),
                contentScale = contentScale,
                onState = { state ->
                    isLoading = state is AsyncImagePainter.State.Loading
                    isError = state is AsyncImagePainter.State.Error
                }
            )
        }
    }
}

@Composable
private fun LetterAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val firstLetter = name.trim().firstOrNull()?.uppercaseChar() ?: '?'
    val backgroundColor = remember(name) {
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC),
            Color(0xFF7E57C2), Color(0xFF5C6BC0), Color(0xFF42A5F5),
            Color(0xFF29B6F6), Color(0xFF26C6DA), Color(0xFF26A69A),
            Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFD4E157),
            Color(0xFFFFEE58), Color(0xFFFFCA28), Color(0xFFFFA726),
            Color(0xFFFF7043)
        )
        colors[Math.abs(name.hashCode()) % colors.size]
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        // Calculate font size relative to container size (approx 45% of smaller dimension)
        val containerSize = minOf(maxWidth, maxHeight)
        val fontSize = with(LocalDensity.current) { (containerSize.toPx() * 0.45f).toSp() }

        Text(
            text = firstLetter.toString(),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize
        )
    }
}
