package com.example.rentmanagement.di

import android.content.Context
import androidx.room.Room
import com.example.rentmanagement.data.dao.*
import com.example.rentmanagement.data.database.AppDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // TODO Phase 2: replace with real Migration objects
            .build()

    @Provides fun providePropertyDao(db: AppDatabase): PropertyDao = db.propertyDao()
    @Provides fun provideUnitDao(db: AppDatabase): UnitDao = db.unitDao()
    @Provides fun provideTenantDao(db: AppDatabase): TenantDao = db.tenantDao()
    @Provides fun provideLeaseDao(db: AppDatabase): LeaseDao = db.leaseDao()
    @Provides fun provideRentDao(db: AppDatabase): RentDao = db.rentDao()
    @Provides fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()
    @Provides fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()
    @Provides fun provideDocumentDao(db: AppDatabase): DocumentDao = db.documentDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
}
