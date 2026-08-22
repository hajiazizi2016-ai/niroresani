package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotation_item",
    foreignKeys = [
        ForeignKey(
            entity = QuotationEntity::class,
            parentColumns = ["id"],
            childColumns = ["quotationId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [Index("quotationId"), Index("catalogItemId")]
)
data class QuotationItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val quotationId: Long,
    val catalogItemId: Long,
    val titleSnapshot: String,
    val unitSnapshot: String,
    val quantity: Double,
    val unitPriceSnapshot: Long,
    val lineTotal: Long,
    val sortOrder: Int = 0
)
