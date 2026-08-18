package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * دسته‌بندی درختی تجهیزات/هزینه‌ها (خودارجاع - self referencing tree).
 * parentId == null یعنی یک گروه ریشه (خط هوایی، پست، مصالح، حمل و نقل، کارگری،
 * سایر هزینه‌ها، اجرایی-اداری). به‌جای حذف، isActive=false می‌شود (بخش ۱۴۶، ۳۶۲).
 */
@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val parentId: Long? = null,
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)
