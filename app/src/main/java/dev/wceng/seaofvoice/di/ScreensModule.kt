package dev.wceng.seaofvoice.di

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.ui.NavDisplay
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.wceng.seaofvoice.ui.navigation.Destination
import dev.wceng.seaofvoice.ui.navigation.EntryProviderInstaller
import dev.wceng.seaofvoice.ui.navigation.Navigator
import dev.wceng.seaofvoice.ui.screens.browse.BrowseRoute
import dev.wceng.seaofvoice.ui.screens.category_detail.CategoryDetailRoute
import dev.wceng.seaofvoice.ui.screens.category_detail.CategoryDetailViewModel
import dev.wceng.seaofvoice.ui.screens.discover.DiscoverRoute
import dev.wceng.seaofvoice.ui.screens.library.LibraryRoute
import dev.wceng.seaofvoice.ui.screens.player.PlayerRoute
import dev.wceng.seaofvoice.ui.screens.settings.SettingsRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object ScreensModule {

    @IntoSet
    @Provides
    fun provideDiscoverEntry(): EntryProviderInstaller = {
        entry<Destination.Discover> {
            DiscoverRoute()
        }
    }

    @IntoSet
    @Provides
    fun providePlayerEntry(navigator: Navigator): EntryProviderInstaller = {
        entry<Destination.Player>(
            metadata = NavDisplay.transitionSpec {
                (slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it / 10 }) + fadeIn(animationSpec = tween(500))) togetherWith
                        fadeOut(animationSpec = tween(500))
            } + NavDisplay.popTransitionSpec {
                fadeIn(animationSpec = tween(500)) togetherWith
                        (slideOutVertically(
                            animationSpec = tween(500),
                            targetOffsetY = { it / 10 }) + fadeOut(animationSpec = tween(500)))
            } + NavDisplay.predictivePopTransitionSpec { _ ->
                fadeIn(animationSpec = tween(500)) togetherWith
                        (slideOutVertically(
                            animationSpec = tween(500),
                            targetOffsetY = { it / 10 }) + fadeOut(animationSpec = tween(500)))
            }
        ) {
            PlayerRoute(onBackClick = { navigator.goBack() })
        }
    }

    @IntoSet
    @Provides
    fun provideBrowseEntry(navigator: Navigator): EntryProviderInstaller = {
        entry<Destination.Browse> {
            BrowseRoute(
                onCategoryClick = { category ->
                    navigator.goTo(
                        Destination.CategoryDetail(
                            categoryName = category.name,
                            categoryType = category.type
                        )
                    )
                }
            )
        }
    }

    @IntoSet
    @Provides
    fun provideCategoryDetailEntry(navigator: Navigator): EntryProviderInstaller = {
        entry<Destination.CategoryDetail> { key ->
            val viewModel = hiltViewModel<CategoryDetailViewModel, CategoryDetailViewModel.Factory>(
                key = key.categoryName + key.categoryType.name,
                creationCallback = { factory -> factory.create(key.categoryName, key.categoryType) }
            )
            CategoryDetailRoute(
                onBackClick = { navigator.goBack() },
                viewModel = viewModel
            )
        }
    }

    @IntoSet
    @Provides
    fun provideLibraryEntry(): EntryProviderInstaller = {
        entry<Destination.Library> {
            LibraryRoute()
        }
    }

    @IntoSet
    @Provides
    fun provideSettingsEntry(): EntryProviderInstaller = {
        entry<Destination.Settings> {
            SettingsRoute()
        }
    }
}
