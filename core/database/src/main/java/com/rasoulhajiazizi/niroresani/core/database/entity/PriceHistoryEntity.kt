package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_history",
    foreignKeys = [
        ForeignKey(
            entity = CatalogItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["catalogItemId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [Index("catalogItemId")]
)
data class PriceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val catalogItemId: Long,
    val oldPrice: Long,
    val newPrice: Long,
    val changedAt: Long = System.currentTimeMillis(),
    val note: String? = null
)
