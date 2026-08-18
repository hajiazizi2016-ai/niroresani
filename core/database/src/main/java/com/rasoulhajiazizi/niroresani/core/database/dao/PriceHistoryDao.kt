package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rasoulhajiazizi.niroresani.core.database.entity.PriceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Query("SELECT * FROM price_history WHERE catalogItemId = :catalogItemId ORDER BY changedAt DESC")
    fun observeForItem(catalogItemId: Long): Flow<List<PriceHistoryEntity>>

    @Insert
    suspend fun insert(history: PriceHistoryEntity): Long
}
