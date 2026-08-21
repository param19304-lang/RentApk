package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("UPDATE expenses SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE propertyId = :propertyId AND isDeleted = 0 ORDER BY date DESC")
    fun getExpensesForProperty(propertyId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE isDeleted = 0 AND date BETWEEN :from AND :to")
    fun getTotalExpensesBetween(from: Long, to: Long): Flow<Double>
}
