package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Query
import com.rasoulhajiazizi.niroresani.core.database.entity.SecurityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security WHERE id = 1")
    fun observe(): Flow<SecurityEntity?>

    @Query("SELECT * FROM security WHERE id = 1")
    suspend fun get(): SecurityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SecurityEntity)
}
