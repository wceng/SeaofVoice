package dev.wceng.seaofvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.wceng.seaofvoice.data.datastore.UserPreferencesDataSource
import dev.wceng.seaofvoice.data.model.DarkModeConfig
import dev.wceng.seaofvoice.ui.SeaOfVoiceApp
import dev.wceng.seaofvoice.ui.navigation.EntryProviderInstaller
import dev.wceng.seaofvoice.ui.navigation.Navigator
import dev.wceng.seaofvoice.ui.theme.SeaOfVoiceTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var userPreferencesDataSource: UserPreferencesDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences by userPreferencesDataSource.userPreferencesFlow
                .collectAsStateWithLifecycle(initialValue = null)

            val darkTheme = when (userPreferences?.darkModeConfig) {
                DarkModeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                DarkModeConfig.LIGHT -> false
                DarkModeConfig.DARK -> true
                null -> isSystemInDarkTheme()
            }

            SeaOfVoiceTheme(
                darkTheme = darkTheme,
                dynamicColor = userPreferences?.useDynamicColor ?: true
            ) {
                SeaOfVoiceApp(
                    navigator = navigator,
                    entryProviderScopes = entryProviderScopes
                )
            }
        }
    }
}
