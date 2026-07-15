package dev.wceng.seaofvoice.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

enum class MainTab(
    val icon: ImageVector,
    val title: String
) {
    Discover(Icons.Default.AutoAwesome, "发现"),
    Browse(Icons.Default.Search, "分类"),
    Library(Icons.Default.LibraryMusic, "心愿"),
    Settings(Icons.Default.Settings, "设置")
}

fun MainTab.toRoute(): NavKey = when (this) {
    MainTab.Discover -> Destination.Discover
    MainTab.Browse -> Destination.Browse
    MainTab.Library -> Destination.Library
    MainTab.Settings -> Destination.Settings
}
