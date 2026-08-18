package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.*
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE isActive = 1 ORDER BY sortOrder")
    fun observeAllActive(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE parentId IS NULL AND isActive = 1 ORDER BY sortOrder")
    fun observeRootCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE parentId = :parentId AND isActive = 1 ORDER BY sortOrder")
    fun observeChildren(parentId: Long): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun update(category: CategoryEntity)
}
