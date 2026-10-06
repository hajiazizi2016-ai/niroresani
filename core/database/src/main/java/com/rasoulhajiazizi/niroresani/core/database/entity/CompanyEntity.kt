package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val registrationNumber: String,
    val nationalId: String? = null,
    val economicCode: String? = null,
    val postalCode: String? = null,
    val mobile: String? = null,
    val fax: String? = null,
    val address: String? = null,
    val logoPath: String? = null,
    val signaturePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
