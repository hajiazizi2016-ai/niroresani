package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * پیش‌فاکتور. companySnapshotJson و customerSnapshotJson اسنپ‌شات لحظه صدور هستند
 * تا تغییر بعدی اطلاعات شرکت/مشتری روی اسناد قدیمی اثر نگذارد
 * (الزام حیاتی و تکرارشونده سند: بخش‌های ۷۸۶، ۱۰۷، ۲۷۶، ۳۷۰).
 *
 * status فعلاً فقط DRAFT/FINAL است؛ مقادیر آینده (SENT/APPROVED/REJECTED)
 * در همین ستون رشته‌ای بدون تغییر ساختار جدول قابل افزودن است (بخش ۵۹، ۱۳۱، ۷۷۲).
 */
@Entity(
    tableName = "quotation",
    foreignKeys = [
        ForeignKey(entity = CustomerEntity::class, parentColumns = ["id"], childColumns = ["customerId"])
    ],
    indices = [Index("customerId"), Index("number"), Index("issueDateEpoch")]
)
data class QuotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val number: String,                 // فرمت: "۱۲۵-۱۴۰۵"
    val issueDateShamsi: String,        // مثال: «سه‌شنبه ۶ مرداد ۱۴۰۵»
    val issueDateEpoch: Long,
    val customerId: Long,
    val companySnapshotJson: String,
    val customerSnapshotJson: String,
    val description: String? = null,
    val totalAmount: Long = 0L,
    val status: String = "DRAFT",       // DRAFT | FINAL
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
