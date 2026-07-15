package dev.wceng.seaofvoice.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.wceng.seaofvoice.data.db.entity.CategoryEntity
import dev.wceng.seaofvoice.data.model.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY stationCount DESC")
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE type = :type")
    suspend fun deleteCategoriesByType(type: CategoryType)
}
