package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.TenantDao
import com.example.rentmanagement.data.entities.TenantEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TenantRepository {
    fun getAllTenants(): Flow<List<TenantEntity>>
    suspend fun getTenantById(id: Long): TenantEntity?
    fun observeTenantById(id: Long): Flow<TenantEntity?>
    fun search(query: String): Flow<List<TenantEntity>>
    fun getTenantCount(): Flow<Int>
    suspend fun addTenant(tenant: TenantEntity): Long
    suspend fun updateTenant(tenant: TenantEntity)
    suspend fun deleteTenant(id: Long)
}

class TenantRepositoryImpl @Inject constructor(
    private val dao: TenantDao
) : TenantRepository {
    override fun getAllTenants() = dao.getAllTenants()
    override suspend fun getTenantById(id: Long) = dao.getTenantById(id)
    override fun observeTenantById(id: Long) = dao.observeTenantById(id)
    override fun search(query: String) = dao.search(query)
    override fun getTenantCount() = dao.getTenantCount()
    override suspend fun addTenant(tenant: TenantEntity) = dao.insert(tenant)
    override suspend fun updateTenant(tenant: TenantEntity) = dao.update(tenant)
    override suspend fun deleteTenant(id: Long) = dao.softDelete(id)
}
