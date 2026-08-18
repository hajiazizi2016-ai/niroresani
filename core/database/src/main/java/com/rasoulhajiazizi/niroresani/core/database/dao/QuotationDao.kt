package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.*
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

    /** برای تولید شماره خودکار سال‌محور - شمارش پیش‌فاکتورهای همان سال شمسی */
    @Query("SELECT COUNT(*) FROM quotation WHERE number LIKE :yearSuffix")
    suspend fun countForYear(yearSuffix: String): Int
}
