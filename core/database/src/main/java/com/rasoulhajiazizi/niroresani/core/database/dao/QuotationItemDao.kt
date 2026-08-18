package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.*
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationItemDao {
    @Query("SELECT * FROM quotation_item WHERE quotationId = :quotationId ORDER BY sortOrder")
    fun observeForQuotation(quotationId: Long): Flow<List<QuotationItemEntity>>

    @Query("SELECT * FROM quotation_item WHERE quotationId = :quotationId ORDER BY sortOrder")
    suspend fun getForQuotation(quotationId: Long): List<QuotationItemEntity>

    @Insert
    suspend fun insert(item: QuotationItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<QuotationItemEntity>): List<Long>

    @Update
    suspend fun update(item: QuotationItemEntity)

    @Delete
    suspend fun delete(item: QuotationItemEntity)

    @Query("DELETE FROM quotation_item WHERE quotationId = :quotationId")
    suspend fun deleteAllForQuotation(quotationId: Long)
}
