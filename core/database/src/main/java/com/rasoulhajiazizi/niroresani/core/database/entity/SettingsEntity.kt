package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity

@Entity(tableName = "settings", primaryKeys = ["key"])
data class SettingsEntity(
    val key: String,
    val value: String
)
