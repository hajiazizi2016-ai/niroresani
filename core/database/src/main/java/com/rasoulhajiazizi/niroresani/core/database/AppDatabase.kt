package com.rasoulhajiazizi.niroresani.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rasoulhajiazizi.niroresani.core.database.dao.CatalogItemDao
import com.rasoulhajiazizi.niroresani.core.database.dao.CategoryDao
import com.rasoulhajiazizi.niroresani.core.database.dao.CompanyDao
import com.rasoulhajiazizi.niroresani.core.database.dao.CustomerDao
import com.rasoulhajiazizi.niroresani.core.database.dao.PriceHistoryDao
import com.rasoulhajiazizi.niroresani.core.database.dao.QuotationDao
import com.rasoulhajiazizi.niroresani.core.database.dao.QuotationItemDao
import com.rasoulhajiazizi.niroresani.core.database.dao.SecurityDao
import com.rasoulhajiazizi.niroresani.core.database.dao.SettingsDao
import com.rasoulhajiazizi.niroresani.core.database.dao.UnitDao
import com.rasoulhajiazizi.niroresani.core.database.entity.CatalogItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CategoryEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CompanyEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.CustomerEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.PriceHistoryEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.QuotationItemEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.SecurityEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.SettingsEntity
import com.rasoulhajiazizi.niroresani.core.database.entity.UnitEntity

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
