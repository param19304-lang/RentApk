package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.TenantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Insert
    suspend fun insert(tenant: TenantEntity): Long

    @Update
    suspend fun update(tenant: TenantEntity)

    @Query("UPDATE tenants SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM tenants WHERE isDeleted = 0 ORDER BY fullName ASC")
    fun getAllTenants(): Flow<List<TenantEntity>>

    @Query("SELECT * FROM tenants WHERE id = :id")
    suspend fun getTenantById(id: Long): TenantEntity?

    @Query("SELECT * FROM tenants WHERE id = :id")
    fun observeTenantById(id: Long): Flow<TenantEntity?>

    @Query("SELECT * FROM tenants WHERE isDeleted = 0 AND (fullName LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%')")
    fun search(query: String): Flow<List<TenantEntity>>

    @Query("SELECT COUNT(*) FROM tenants WHERE isDeleted = 0")
    fun getTenantCount(): Flow<Int>
}
