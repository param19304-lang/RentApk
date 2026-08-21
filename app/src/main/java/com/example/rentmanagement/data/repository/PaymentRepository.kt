package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.PaymentDao
import com.example.rentmanagement.data.entities.PaymentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PaymentRepository {
    fun getAllPayments(): Flow<List<PaymentEntity>>
    fun getPaymentsForRent(rentId: Long): Flow<List<PaymentEntity>>
    fun getPaymentsForTenant(tenantId: Long): Flow<List<PaymentEntity>>
    suspend fun getPaymentById(id: Long): PaymentEntity?
    fun getTotalCollectedBetween(from: Long, to: Long): Flow<Double>
    suspend fun addPayment(payment: PaymentEntity): Long
    suspend fun countAll(): Int
}

class PaymentRepositoryImpl @Inject constructor(
    private val dao: PaymentDao
) : PaymentRepository {
    override fun getAllPayments() = dao.getAllPayments()
    override fun getPaymentsForRent(rentId: Long) = dao.getPaymentsForRent(rentId)
    override fun getPaymentsForTenant(tenantId: Long) = dao.getPaymentsForTenant(tenantId)
    override suspend fun getPaymentById(id: Long) = dao.getPaymentById(id)
    override fun getTotalCollectedBetween(from: Long, to: Long) = dao.getTotalCollectedBetween(from, to)
    override suspend fun addPayment(payment: PaymentEntity) = dao.insert(payment)
    override suspend fun countAll() = dao.countAll()
}
