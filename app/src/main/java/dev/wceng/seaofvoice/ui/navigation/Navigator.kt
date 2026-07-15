package dev.wceng.seaofvoice.ui.navigation

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject

@Stable
class Navigator @Inject constructor(
    val backStack: NavBackStack<NavKey>
) {
    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    fun navigateToTopLevelDestination(destination: NavKey) {
        if (backStack.lastOrNull() != destination) {
            backStack.clear()
            backStack.add(destination)
        }
    }
}
