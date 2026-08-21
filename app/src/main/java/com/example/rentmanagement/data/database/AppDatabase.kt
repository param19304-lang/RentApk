package com.example.rentmanagement.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rentmanagement.data.dao.*
import com.example.rentmanagement.data.entities.*

@Database(
    entities = [
        PropertyEntity::class,
        UnitEntity::class,
        TenantEntity::class,
        LeaseEntity::class,
        RentEntity::class,
        PaymentEntity::class,
        ExpenseEntity::class,
        DocumentEntity::class,
        ReminderEntity::class,
        UserAccountEntity::class
    ],
    version = 2,
    exportSchema = false // TODO Phase 3: set true + wire room.schemaLocation once real Migrations replace fallbackToDestructiveMigration()
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun unitDao(): UnitDao
    abstract fun tenantDao(): TenantDao
    abstract fun leaseDao(): LeaseDao
    abstract fun rentDao(): RentDao
    abstract fun paymentDao(): PaymentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun documentDao(): DocumentDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        const val DATABASE_NAME = "rent_management.db"
    }
}
