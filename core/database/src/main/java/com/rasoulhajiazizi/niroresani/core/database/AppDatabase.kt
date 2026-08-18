package com.rasoulhajiazizi.niroresani.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rasoulhajiazizi.niroresani.core.database.dao.*
import com.rasoulhajiazizi.niroresani.core.database.entity.*

/**
 * دیتابیس اصلی برنامه. نسخه ۱ (MVP).
 *
 * توجه مهم برای توسعه‌دهنده آینده: هرگز از fallbackToDestructiveMigration
 * استفاده نشود؛ طبق الزام سند (بخش‌های ۱۰۷۱، ۱۸۴۷، ۲۰۲۳) هر تغییر ساختار
 * دیتابیس باید با یک Migration صریح همراه شود تا اطلاعات کاربر از بین نرود.
 */
@Database(
    entities = [
        CompanyEntity::class,
        CustomerEntity::class,
        CategoryEntity::class,
        UnitEntity::class,
        CatalogItemEntity::class,
        PriceHistoryEntity::class,
        QuotationEntity::class,
        QuotationItemEntity::class,
        SettingsEntity::class,
        SecurityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun customerDao(): CustomerDao
    abstract fun categoryDao(): CategoryDao
    abstract fun unitDao(): UnitDao
    abstract fun catalogItemDao(): CatalogItemDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun quotationDao(): QuotationDao
    abstract fun quotationItemDao(): QuotationItemDao
    abstract fun settingsDao(): SettingsDao
    abstract fun securityDao(): SecurityDao

    companion object {
        const val DATABASE_NAME = "niroresani.db"
    }
}
