package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rasoulhajiazizi.niroresani.core.database.entity.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM unit ORDER BY title")
    fun observeAll(): Flow<List<UnitEntity>>

    @Insert
    suspend fun insert(unit: UnitEntity): Long

    @Insert
    suspend fun insertAll(units: List<UnitEntity>): List<Long>
}
