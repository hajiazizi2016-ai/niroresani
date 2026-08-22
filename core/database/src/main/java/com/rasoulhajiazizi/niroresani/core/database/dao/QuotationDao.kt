package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationDao {
    @Query("SELECT * FROM quotation ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<QuotationEntity>>

    @Query("SELECT * FROM quotation WHERE id = :id")
    suspend fun getById(id: Long): QuotationEntity?

    @Query("SELECT * FROM quotation WHERE id = :id")
    fun observeById(id: Long): Flow<QuotationEntity?>

    @Query("""SELECT * FROM quotation WHERE number LIKE '%' || :query || '%'
              OR customerSnapshotJson LIKE '%' || :query || '%'
              ORDER BY createdAt DESC""")
    fun search(query: String): Flow<List<QuotationEntity>>

    @Insert
    suspend fun insert(quotation: QuotationEntity): Long

    @Update
    suspend fun update(quotation: QuotationEntity)

    @Delete
    suspend fun delete(quotation: QuotationEntity)

    @Query("SELECT COUNT(*) FROM quotation WHERE number LIKE :yearSuffix")
    suspend fun countForYear(yearSuffix: String): Int
}
