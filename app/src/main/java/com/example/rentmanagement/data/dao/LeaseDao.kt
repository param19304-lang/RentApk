package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.domain.model.LeaseStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaseDao {
    @Insert
    suspend fun insert(lease: LeaseEntity): Long

    @Update
    suspend fun update(lease: LeaseEntity)

    @Query("SELECT * FROM leases WHERE isDeleted = 0 ORDER BY startDate DESC")
    fun getAllLeases(): Flow<List<LeaseEntity>>

    @Query("SELECT * FROM leases WHERE status = :status AND isDeleted = 0")
    fun getLeasesByStatus(status: LeaseStatus): Flow<List<LeaseEntity>>

    @Query("SELECT * FROM leases WHERE status = 'ACTIVE' AND isDeleted = 0")
    suspend fun getActiveLeasesOnce(): List<LeaseEntity>

    @Query("SELECT * FROM leases WHERE unitId = :unitId AND status = 'ACTIVE' AND isDeleted = 0 LIMIT 1")
    suspend fun getActiveLeaseForUnit(unitId: Long): LeaseEntity?

    @Query("SELECT * FROM leases WHERE tenantId = :tenantId ORDER BY startDate DESC")
    fun getLeasesForTenant(tenantId: Long): Flow<List<LeaseEntity>>

    @Query("SELECT * FROM leases WHERE id = :id")
    suspend fun getLeaseById(id: Long): LeaseEntity?

    @Query("SELECT * FROM leases WHERE endDate BETWEEN :now AND :until AND status IN ('ACTIVE','EXPIRING_SOON') AND isDeleted = 0")
    fun getLeasesExpiringBetween(now: Long, until: Long): Flow<List<LeaseEntity>>

    @Query("UPDATE leases SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: LeaseStatus)
}
