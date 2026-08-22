package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotation",
    foreignKeys = [
        ForeignKey(entity = CustomerEntity::class, parentColumns = ["id"], childColumns = ["customerId"])
    ],
    indices = [Index("customerId"), Index("number"), Index("issueDateEpoch")]
)
data class QuotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val number: String,
    val issueDateShamsi: String,
    val issueDateEpoch: Long,
    val customerId: Long,
    val companySnapshotJson: String,
    val customerSnapshotJson: String,
    val description: String? = null,
    val totalAmount: Long = 0L,
    val status: String = "DRAFT",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
