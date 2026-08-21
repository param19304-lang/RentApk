package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.UnitDao
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.domain.model.UnitStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UnitRepository {
    fun getUnitsForProperty(propertyId: Long): Flow<List<UnitEntity>>
    fun getAllUnits(): Flow<List<UnitEntity>>
    suspend fun getUnitById(id: Long): UnitEntity?
    fun observeUnitById(id: Long): Flow<UnitEntity?>
    fun getTotalUnitCount(): Flow<Int>
    fun getUnitCountByStatus(status: UnitStatus): Flow<Int>
    suspend fun addUnit(unit: UnitEntity): Long
    suspend fun updateUnit(unit: UnitEntity)
    suspend fun deleteUnit(id: Long)
    suspend fun updateOccupancy(id: Long, status: UnitStatus, tenantId: Long?)
}

class UnitRepositoryImpl @Inject constructor(
    private val dao: UnitDao
) : UnitRepository {
    override fun getUnitsForProperty(propertyId: Long) = dao.getUnitsForProperty(propertyId)
    override fun getAllUnits() = dao.getAllUnits()
    override suspend fun getUnitById(id: Long) = dao.getUnitById(id)
    override fun observeUnitById(id: Long) = dao.observeUnitById(id)
    override fun getTotalUnitCount() = dao.getTotalUnitCount()
    override fun getUnitCountByStatus(status: UnitStatus) = dao.getUnitCountByStatus(status)
    override suspend fun addUnit(unit: UnitEntity) = dao.insert(unit)
    override suspend fun updateUnit(unit: UnitEntity) = dao.update(unit)
    override suspend fun deleteUnit(id: Long) = dao.softDelete(id)
    override suspend fun updateOccupancy(id: Long, status: UnitStatus, tenantId: Long?) =
        dao.updateOccupancy(id, status, tenantId)
}
