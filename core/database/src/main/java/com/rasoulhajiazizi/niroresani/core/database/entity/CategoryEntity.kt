package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val parentId: Long? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)
