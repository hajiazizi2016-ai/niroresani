package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** یک رکورد تکی برای مدیریت رمز ورود اختیاری برنامه (بخش ۴۰۱، ۱۰۴۹، ۱۰۵۰). */
@Entity(tableName = "security")
data class SecurityEntity(
    @PrimaryKey val id: Int = 1, // همیشه فقط یک رکورد فعال
    val passwordHash: String? = null,
    val isPasswordEnabled: Boolean = false
)
