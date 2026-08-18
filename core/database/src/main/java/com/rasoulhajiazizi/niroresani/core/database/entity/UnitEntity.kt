package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** واحد اندازه‌گیری: عدد، متر، کیلوگرم، تن، سرویس، ساعت، روز و... (بخش ۲۹۴) */
@Entity(tableName = "unit")
data class UnitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String
)
