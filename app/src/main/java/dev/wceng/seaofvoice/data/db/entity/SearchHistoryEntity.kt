package dev.wceng.seaofvoice.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val query: String,
    val lastUsedAt: Long = System.currentTimeMillis()
)
