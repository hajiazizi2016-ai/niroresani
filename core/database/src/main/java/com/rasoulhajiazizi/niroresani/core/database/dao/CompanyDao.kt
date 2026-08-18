package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.*
import com.rasoulhajiazizi.niroresani.core.database.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM company LIMIT 1")
    fun observeCompany(): Flow<CompanyEntity?>

    @Query("SELECT * FROM company LIMIT 1")
    suspend fun getCompany(): CompanyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(company: CompanyEntity): Long

    @Update
    suspend fun update(company: CompanyEntity)
}
