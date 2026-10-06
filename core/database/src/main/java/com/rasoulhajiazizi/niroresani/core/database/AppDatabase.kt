package com.rasoulhajiazizi.niroresani.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

/**
 * Migration نسخه ۱ به ۲ - افزودن فیلدهای جدید شرکت (شناسه ملی، شماره اقتصادی، کدپستی،
 * تلفن همراه، نمابر، آدرس، امضا) و مشتری (نوع عنوان، موضوع طرح، کد طرح، تماس).
 * طبق اصل ثابت پروژه، هرگز fallbackToDestructiveMigration استفاده نمی‌شود - داده‌های
 * موجود کاربران باید در ارتقا حفظ شوند.
 */
val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE company ADD COLUMN nationalId TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN economicCode TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN postalCode TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN mobile TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN fax TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN address TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE company ADD COLUMN signaturePath TEXT DEFAULT NULL")

        db.execSQL("ALTER TABLE customer ADD COLUMN titleType TEXT NOT NULL DEFAULT 'آقای'")
        db.execSQL("ALTER TABLE customer ADD COLUMN planSubject TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE customer ADD COLUMN planCode TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE customer ADD COLUMN phone TEXT DEFAULT NULL")
    }
}

/**
 * Migration نسخه ۲ به ۳ - افزودن ستون ترتیب نمایش (sortOrder) به تجهیزات بانک اطلاعاتی
 * تا ترتیب دقیق موردنظر کاربر (نه ترتیب الفبایی) در لیست‌ها حفظ شود. محتوای واقعی بانک
 * تجهیزات (دسته‌ها و آیتم‌ها) توسط SeedData.populate() بر اساس یک شماره نسخه‌ی بذر
 * (seed version) ذخیره‌شده در جدول settings بازسازی می‌شود - نه در این Migration -
 * چون جایگزینی کامل درخت تجهیزات به منطق Kotlin نیاز دارد. این بازسازی هرگز
 * پیش‌فاکتورهای ثبت‌شده‌ی قبلی کاربر را دستکاری نمی‌کند، چون تمام اطلاعات آیتم‌های هر
 * پیش‌فاکتور به‌صورت Snapshot (titleSnapshot/unitSnapshot/unitPriceSnapshot) مستقیماً
 * در جدول quotation_item ذخیره شده و هیچ وابستگی کلید خارجی به catalog_item ندارد.
 */
val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE catalog_item ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0")
    }
}
