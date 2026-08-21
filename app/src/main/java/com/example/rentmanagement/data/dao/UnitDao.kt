package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.UnitEntity
import com.example.rentmanagement.domain.model.UnitStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Insert
    suspend fun insert(unit: UnitEntity): Long

    @Update
    suspend fun update(unit: UnitEntity)

    @Query("UPDATE units SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM units WHERE propertyId = :propertyId AND isDeleted = 0 ORDER BY unitName ASC")
    fun getUnitsForProperty(propertyId: Long): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE isDeleted = 0 ORDER BY unitName ASC")
    fun getAllUnits(): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE id = :id")
    suspend fun getUnitById(id: Long): UnitEntity?

    @Query("SELECT * FROM units WHERE id = :id")
    fun observeUnitById(id: Long): Flow<UnitEntity?>

    @Query("SELECT COUNT(*) FROM units WHERE isDeleted = 0")
    fun getTotalUnitCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM units WHERE isDeleted = 0 AND status = :status")
    fun getUnitCountByStatus(status: UnitStatus): Flow<Int>

    @Query("UPDATE units SET status = :status, currentTenantId = :tenantId WHERE id = :id")
    suspend fun updateOccupancy(id: Long, status: UnitStatus, tenantId: Long?)
}
