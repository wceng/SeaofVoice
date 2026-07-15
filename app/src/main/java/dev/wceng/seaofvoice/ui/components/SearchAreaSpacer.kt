package dev.wceng.seaofvoice.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A unified spacer to provide consistent top clearance for the global search bar.
 */
@Composable
fun SearchAreaSpacer() {
    val topInset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    // 16dp (top padding) + 56dp (search bar height) + 16dp (bottom breathing room)
    Spacer(modifier = Modifier.height(topInset + 88.dp))
}
