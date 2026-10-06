package com.rasoulhajiazizi.niroresani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نوع عنوان مشتری - بخش ۴ درخواست اصلاحات (شرکت / آقای / خانم).
 */
object CustomerTitleType {
    const val COMPANY = "شرکت"
    const val MR = "آقای"
    const val MRS = "خانم"
}

@Entity(tableName = "customer")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val titleType: String = CustomerTitleType.MR,
    val firstName: String,
    val lastName: String,
    val planSubject: String? = null,
    val planCode: String? = null,
    val phone: String? = null,
    val address: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
