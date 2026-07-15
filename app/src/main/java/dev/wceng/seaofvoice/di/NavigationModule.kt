package dev.wceng.seaofvoice.di

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import dev.wceng.seaofvoice.ui.navigation.Destination
import dev.wceng.seaofvoice.ui.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {

    @Provides
    @ActivityRetainedScoped
    fun provideNavBackStack(): NavBackStack<NavKey> = NavBackStack(Destination.Discover)

    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(backStack: NavBackStack<NavKey>): Navigator = Navigator(backStack)
}
