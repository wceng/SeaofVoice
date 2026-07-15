package dev.wceng.seaofvoice.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.wceng.seaofvoice.data.model.CategoryType
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Discover : Destination
    @Serializable
    data object Browse : Destination
    @Serializable
    data object Library : Destination
    @Serializable
    data object Settings : Destination

    @Serializable
    data object Player : Destination

    @Serializable
    data class CategoryDetail(
        val categoryName: String,
        val categoryType: CategoryType
    ) : Destination
}

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit
