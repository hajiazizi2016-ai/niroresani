package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogItemDao {
    @Query("SELECT * FROM catalog_item WHERE categoryId = :categoryId AND isActive = 1 ORDER BY title")
    fun observeByCategory(categoryId: Long): Flow<List<CatalogItemEntity>>

    @Query("""SELECT * FROM catalog_item WHERE isActive = 1 AND title LIKE '%' || :query || '%'
              ORDER BY title LIMIT 100""")
    fun search(query: String): Flow<List<CatalogItemEntity>>

    @Query("SELECT * FROM catalog_item WHERE id = :id")
    suspend fun getById(id: Long): CatalogItemEntity?

    @Insert
    suspend fun insert(item: CatalogItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<CatalogItemEntity>): List<Long>

    @Update
    suspend fun update(item: CatalogItemEntity)

    @Query("UPDATE catalog_item SET currentPrice = :newPrice, priceUpdatedAt = :changedAt WHERE id = :itemId")
    suspend fun updatePrice(itemId: Long, newPrice: Long, changedAt: Long)

    @Query("SELECT COUNT(*) FROM catalog_item")
    suspend fun count(): Int
}
