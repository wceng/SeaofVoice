package dev.wceng.seaofvoice.data.db

import androidx.room.TypeConverter
import dev.wceng.seaofvoice.data.model.CategoryType

class DataConverter {
    @TypeConverter
    fun fromCategoryType(value: CategoryType): String {
        return value.name
    }

    @TypeConverter
    fun toCategoryType(value: String): CategoryType {
        return CategoryType.valueOf(value)
    }
}
