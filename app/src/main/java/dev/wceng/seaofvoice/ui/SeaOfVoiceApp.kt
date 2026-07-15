package dev.wceng.seaofvoice.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.wceng.seaofvoice.ui.components.GlassMiniPlayer
import dev.wceng.seaofvoice.ui.navigation.Destination
import dev.wceng.seaofvoice.ui.navigation.EntryProviderInstaller
import dev.wceng.seaofvoice.ui.navigation.MainTab
import dev.wceng.seaofvoice.ui.navigation.Navigator
import dev.wceng.seaofvoice.ui.navigation.toRoute
import dev.wceng.seaofvoice.ui.screens.search.SearchOverlay

@Composable
fun SeaOfVoiceApp(
    navigator: Navigator,
    entryProviderScopes: Set<EntryProviderInstaller>,
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentDestination = navigator.backStack.lastOrNull() ?: Destination.Discover
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    val selectedTab = remember(currentDestination) {
        when (currentDestination) {
            is Destination.Discover -> MainTab.Discover
            is Destination.Browse -> MainTab.Browse
            is Destination.Library -> MainTab.Library
            is Destination.Settings -> MainTab.Settings
            else -> null
        }
    }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = if (selectedTab != null) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    } else {
        NavigationSuiteType.None
    }

    val isTopLevelDestination = currentDestination is Destination.Discover ||
            currentDestination is Destination.Browse ||
            currentDestination is Destination.Library ||
            currentDestination is Destination.Settings

    SearchOverlay(
        showSearchBar = isTopLevelDestination
    ) {
        NavigationSuiteScaffold(
            layoutType = layoutType,
            navigationSuiteItems = {
                MainTab.entries.forEach { tab ->
                    item(
                        selected = selectedTab == tab,
                        onClick = {
                            navigator.navigateToTopLevelDestination(tab.toRoute())
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content
                NavDisplay(
                    backStack = navigator.backStack,
                    modifier = Modifier.fillMaxSize(),
                    onBack = { navigator.goBack() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator ()
                    ),
                    entryProvider = entryProvider {
                        entryProviderScopes.forEach { builder -> this.builder() }
                    },
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    popTransitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    },
                    predictivePopTransitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith
                                fadeOut(animationSpec = tween(500))
                    }
                )

                // Global Mini Player
                if (currentDestination != Destination.Player) {
                    GlassMiniPlayer(
                        playbackState = playbackState,
                        onTogglePlayPause = viewModel::togglePlayPause,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .clickable { 
                                if (playbackState.currentStation != null) {
                                    navigator.goTo(Destination.Player)
                                }
                            }
                    )
                }
            }
        }
    }
}
