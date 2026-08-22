package com.rasoulhajiazizi.niroresani.di

import android.content.Context
import androidx.room.Room
import com.rasoulhajiazizi.niroresani.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCompanyDao(db: AppDatabase) = db.companyDao()

    @Provides
    @Singleton
    fun provideCustomerDao(db: AppDatabase) = db.customerDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: AppDatabase) = db.categoryDao()

    @Provides
    @Singleton
    fun provideUnitDao(db: AppDatabase) = db.unitDao()

    @Provides
    @Singleton
    fun provideCatalogItemDao(db: AppDatabase) = db.catalogItemDao()

    @Provides
    @Singleton
    fun providePriceHistoryDao(db: AppDatabase) = db.priceHistoryDao()

    @Provides
    @Singleton
    fun provideQuotationDao(db: AppDatabase) = db.quotationDao()

    @Provides
    @Singleton
    fun provideQuotationItemDao(db: AppDatabase) = db.quotationItemDao()

    @Provides
    @Singleton
    fun provideSettingsDao(db: AppDatabase) = db.settingsDao()

    @Provides
    @Singleton
    fun provideSecurityDao(db: AppDatabase) = db.securityDao()
}
