package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * اطلاعات شرکت - یک‌بار ثبت و برای تمام پیش‌فاکتورها استفاده می‌شود.
 * فقط از طریق تنظیمات قابل ویرایش است (بخش‌های ۸، ۷۱، ۳۳۷، ۳۳۸ سند).
 */
@Entity(tableName = "company")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val registrationNumber: String,
    val logoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
