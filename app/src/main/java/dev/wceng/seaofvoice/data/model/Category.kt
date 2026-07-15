package dev.wceng.seaofvoice.data.model

data class Category(
    val name: String,
    val stationCount: Int,
    val type: CategoryType,
    val isoCode: String? = null
)

enum class CategoryType {
    Country, Language, Tag
}
