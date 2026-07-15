package dev.wceng.seaofvoice.data.db.entity

import androidx.room.Entity
import dev.wceng.seaofvoice.data.model.Category
import dev.wceng.seaofvoice.data.model.CategoryType

@Entity(
    tableName = "categories",
    primaryKeys = ["name", "type"]
)
data class CategoryEntity(
    val name: String,
    val stationCount: Int,
    val type: CategoryType,
    val isoCode: String? = null
)

fun CategoryEntity.toModel(): Category = Category(
    name = name,
    stationCount = stationCount,
    type = type,
    isoCode = isoCode
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    name = name,
    stationCount = stationCount,
    type = type,
    isoCode = isoCode
)
