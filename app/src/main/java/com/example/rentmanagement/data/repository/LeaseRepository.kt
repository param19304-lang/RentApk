package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.LeaseDao
import com.example.rentmanagement.data.entities.LeaseEntity
import com.example.rentmanagement.domain.model.LeaseStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LeaseRepository {
    fun getAllLeases(): Flow<List<LeaseEntity>>
    fun getLeasesByStatus(status: LeaseStatus): Flow<List<LeaseEntity>>
    suspend fun getActiveLeasesOnce(): List<LeaseEntity>
    suspend fun getActiveLeaseForUnit(unitId: Long): LeaseEntity?
    fun getLeasesForTenant(tenantId: Long): Flow<List<LeaseEntity>>
    suspend fun getLeaseById(id: Long): LeaseEntity?
    fun getLeasesExpiringBetween(now: Long, until: Long): Flow<List<LeaseEntity>>
    suspend fun addLease(lease: LeaseEntity): Long
    suspend fun updateLease(lease: LeaseEntity)
    suspend fun updateStatus(id: Long, status: LeaseStatus)
}

class LeaseRepositoryImpl @Inject constructor(
    private val dao: LeaseDao
) : LeaseRepository {
    override fun getAllLeases() = dao.getAllLeases()
    override fun getLeasesByStatus(status: LeaseStatus) = dao.getLeasesByStatus(status)
    override suspend fun getActiveLeasesOnce() = dao.getActiveLeasesOnce()
    override suspend fun getActiveLeaseForUnit(unitId: Long) = dao.getActiveLeaseForUnit(unitId)
    override fun getLeasesForTenant(tenantId: Long) = dao.getLeasesForTenant(tenantId)
    override suspend fun getLeaseById(id: Long) = dao.getLeaseById(id)
    override fun getLeasesExpiringBetween(now: Long, until: Long) = dao.getLeasesExpiringBetween(now, until)
    override suspend fun addLease(lease: LeaseEntity) = dao.insert(lease)
    override suspend fun updateLease(lease: LeaseEntity) = dao.update(lease)
    override suspend fun updateStatus(id: Long, status: LeaseStatus) = dao.updateStatus(id, status)
}
