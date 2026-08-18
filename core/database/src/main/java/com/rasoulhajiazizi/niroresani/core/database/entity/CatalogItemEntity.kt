package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * بانک تجهیزات/مصالح/حمل‌ونقل/کارگری/سایر هزینه‌ها - ساختار یکسان برای همه
 * (الزام بخش ۳۵، ۵۵۴: «هر تجهیز باید ساختار یکسان داشته باشد»).
 * تفکیک نوع فقط از طریق categoryId در درخت Category انجام می‌شود.
 */
@Entity(
    tableName = "catalog_item",
    foreignKeys = [
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"]),
        ForeignKey(entity = UnitEntity::class, parentColumns = ["id"], childColumns = ["unitId"])
    ],
    indices = [Index("categoryId"), Index("unitId"), Index("title")]
)
data class CatalogItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val categoryId: Long,
    val title: String,
    val description: String? = null,
    val unitId: Long,
    val currentPrice: Long = 0L, // واحد: ریال
    val priceUpdatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
