package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity

/** جدول ساده Key-Value برای تنظیمات عمومی و شمارنده آخرین شماره پیش‌فاکتور. */
@Entity(tableName = "settings", primaryKeys = ["key"])
data class SettingsEntity(
    val key: String,
    val value: String
)
