package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.RentDao
import com.example.rentmanagement.data.entities.RentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RentRepository {
    fun getAllRentRecords(): Flow<List<RentEntity>>
    fun getRentForMonth(billingMonth: String): Flow<List<RentEntity>>
    suspend fun getRentForLeaseAndMonth(leaseId: Long, billingMonth: String): RentEntity?
    fun getRentForTenant(tenantId: Long): Flow<List<RentEntity>>
    suspend fun getRentById(id: Long): RentEntity?
    fun observeRentById(id: Long): Flow<RentEntity?>
    suspend fun getOutstandingForLease(leaseId: Long): List<RentEntity>
    fun getOverdueRent(): Flow<List<RentEntity>>
    fun getUpcomingDue(now: Long, until: Long): Flow<List<RentEntity>>
    suspend fun addRent(rent: RentEntity): Long
    suspend fun updateRent(rent: RentEntity)
    suspend fun markOverdue(now: Long)
}

class RentRepositoryImpl @Inject constructor(
    private val dao: RentDao
) : RentRepository {
    override fun getAllRentRecords() = dao.getAllRentRecords()
    override fun getRentForMonth(billingMonth: String) = dao.getRentForMonth(billingMonth)
    override suspend fun getRentForLeaseAndMonth(leaseId: Long, billingMonth: String) =
        dao.getRentForLeaseAndMonth(leaseId, billingMonth)
    override fun getRentForTenant(tenantId: Long) = dao.getRentForTenant(tenantId)
    override suspend fun getRentById(id: Long) = dao.getRentById(id)
    override fun observeRentById(id: Long) = dao.observeRentById(id)
    override suspend fun getOutstandingForLease(leaseId: Long) = dao.getOutstandingForLease(leaseId)
    override fun getOverdueRent() = dao.getOverdueRent()
    override fun getUpcomingDue(now: Long, until: Long) = dao.getUpcomingDue(now, until)
    override suspend fun addRent(rent: RentEntity) = dao.insert(rent)
    override suspend fun updateRent(rent: RentEntity) = dao.update(rent)
    override suspend fun markOverdue(now: Long) = dao.markOverdue(now)
}
