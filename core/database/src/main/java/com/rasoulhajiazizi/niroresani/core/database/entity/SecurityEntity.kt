package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security")
data class SecurityEntity(
    @PrimaryKey val id: Int = 1,
    val passwordHash: String? = null,
    val isPasswordEnabled: Boolean = false
)
