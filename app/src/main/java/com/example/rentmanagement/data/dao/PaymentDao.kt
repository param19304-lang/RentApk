package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: PaymentEntity): Long

    @Query("SELECT * FROM payments WHERE isDeleted = 0 ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE rentId = :rentId AND isDeleted = 0 ORDER BY paymentDate ASC")
    fun getPaymentsForRent(rentId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE tenantId = :tenantId AND isDeleted = 0 ORDER BY paymentDate DESC")
    fun getPaymentsForTenant(tenantId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getPaymentById(id: Long): PaymentEntity?

    @Query("SELECT COALESCE(SUM(amount),0) FROM payments WHERE isDeleted = 0 AND paymentDate BETWEEN :from AND :to")
    fun getTotalCollectedBetween(from: Long, to: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM payments WHERE isDeleted = 0")
    suspend fun countAll(): Int
}
