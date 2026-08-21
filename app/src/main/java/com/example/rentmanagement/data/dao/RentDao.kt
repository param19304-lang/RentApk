package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.RentEntity
import com.example.rentmanagement.domain.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RentDao {
    @Insert
    suspend fun insert(rent: RentEntity): Long

    @Update
    suspend fun update(rent: RentEntity)

    @Query("SELECT * FROM rent_records ORDER BY dueDate DESC")
    fun getAllRentRecords(): Flow<List<RentEntity>>

    @Query("SELECT * FROM rent_records WHERE billingMonth = :billingMonth")
    fun getRentForMonth(billingMonth: String): Flow<List<RentEntity>>

    @Query("SELECT * FROM rent_records WHERE leaseId = :leaseId AND billingMonth = :billingMonth LIMIT 1")
    suspend fun getRentForLeaseAndMonth(leaseId: Long, billingMonth: String): RentEntity?

    @Query("SELECT * FROM rent_records WHERE tenantId = :tenantId ORDER BY dueDate DESC")
    fun getRentForTenant(tenantId: Long): Flow<List<RentEntity>>

    @Query("SELECT * FROM rent_records WHERE id = :id")
    suspend fun getRentById(id: Long): RentEntity?

    @Query("SELECT * FROM rent_records WHERE id = :id")
    fun observeRentById(id: Long): Flow<RentEntity?>

    @Query("SELECT * FROM rent_records WHERE status IN ('PENDING','PARTIALLY_PAID','OVERDUE') AND leaseId = :leaseId ORDER BY dueDate ASC")
    suspend fun getOutstandingForLease(leaseId: Long): List<RentEntity>

    @Query("SELECT * FROM rent_records WHERE status = 'OVERDUE'")
    fun getOverdueRent(): Flow<List<RentEntity>>

    @Query("SELECT * FROM rent_records WHERE status IN ('PENDING','PARTIALLY_PAID') AND dueDate BETWEEN :now AND :until")
    fun getUpcomingDue(now: Long, until: Long): Flow<List<RentEntity>>

    @Query("UPDATE rent_records SET status = 'OVERDUE' WHERE status IN ('PENDING','PARTIALLY_PAID') AND dueDate < :now")
    suspend fun markOverdue(now: Long)
}
