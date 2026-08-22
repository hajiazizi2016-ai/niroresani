package com.rasoulhajiazizi.niroresani.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customer WHERE id = :id")
    suspend fun getById(id: Long): CustomerEntity?

    @Query("""SELECT * FROM customer WHERE firstName LIKE '%' || :query || '%'
              OR lastName LIKE '%' || :query || '%' ORDER BY createdAt DESC""")
    fun search(query: String): Flow<List<CustomerEntity>>

    @Insert
    suspend fun insert(customer: CustomerEntity): Long

    @Update
    suspend fun update(customer: CustomerEntity)

    @Delete
    suspend fun delete(customer: CustomerEntity)
}
