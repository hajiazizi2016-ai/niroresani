package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val firstName: String,
    val lastName: String,
    val address: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
