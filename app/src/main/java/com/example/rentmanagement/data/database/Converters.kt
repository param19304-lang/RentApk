package com.example.rentmanagement.data.database

import androidx.room.TypeConverter
import com.example.rentmanagement.domain.model.*

class Converters {
    @TypeConverter fun fromPropertyType(v: PropertyType): String = v.name
    @TypeConverter fun toPropertyType(v: String): PropertyType = PropertyType.valueOf(v)

    @TypeConverter fun fromUnitStatus(v: UnitStatus): String = v.name
    @TypeConverter fun toUnitStatus(v: String): UnitStatus = UnitStatus.valueOf(v)

    @TypeConverter fun fromLeaseStatus(v: LeaseStatus): String = v.name
    @TypeConverter fun toLeaseStatus(v: String): LeaseStatus = LeaseStatus.valueOf(v)

    @TypeConverter fun fromPaymentStatus(v: PaymentStatus): String = v.name
    @TypeConverter fun toPaymentStatus(v: String): PaymentStatus = PaymentStatus.valueOf(v)

    @TypeConverter fun fromPaymentMethod(v: PaymentMethod): String = v.name
    @TypeConverter fun toPaymentMethod(v: String): PaymentMethod = PaymentMethod.valueOf(v)

    @TypeConverter fun fromExpenseCategory(v: ExpenseCategory): String = v.name
    @TypeConverter fun toExpenseCategory(v: String): ExpenseCategory = ExpenseCategory.valueOf(v)

    @TypeConverter fun fromReminderType(v: ReminderType): String = v.name
    @TypeConverter fun toReminderType(v: String): ReminderType = ReminderType.valueOf(v)

    @TypeConverter fun fromDocumentCategory(v: DocumentCategory): String = v.name
    @TypeConverter fun toDocumentCategory(v: String): DocumentCategory = DocumentCategory.valueOf(v)
}
