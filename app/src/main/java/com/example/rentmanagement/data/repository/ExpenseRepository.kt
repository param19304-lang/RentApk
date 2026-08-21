package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.ExpenseDao
import com.example.rentmanagement.data.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    fun getExpensesForProperty(propertyId: Long): Flow<List<ExpenseEntity>>
    fun getTotalExpensesBetween(from: Long, to: Long): Flow<Double>
    suspend fun addExpense(expense: ExpenseEntity): Long
    suspend fun updateExpense(expense: ExpenseEntity)
    suspend fun deleteExpense(id: Long)
}

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {
    override fun getAllExpenses() = dao.getAllExpenses()
    override fun getExpensesForProperty(propertyId: Long) = dao.getExpensesForProperty(propertyId)
    override fun getTotalExpensesBetween(from: Long, to: Long) = dao.getTotalExpensesBetween(from, to)
    override suspend fun addExpense(expense: ExpenseEntity) = dao.insert(expense)
    override suspend fun updateExpense(expense: ExpenseEntity) = dao.update(expense)
    override suspend fun deleteExpense(id: Long) = dao.softDelete(id)
}
