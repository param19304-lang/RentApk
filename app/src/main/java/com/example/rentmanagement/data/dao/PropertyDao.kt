package com.example.rentmanagement.data.dao

import androidx.room.*
import com.example.rentmanagement.data.entities.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Insert
    suspend fun insert(property: PropertyEntity): Long

    @Update
    suspend fun update(property: PropertyEntity)

    @Query("UPDATE properties SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM properties WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: Long): PropertyEntity?

    @Query("SELECT * FROM properties WHERE id = :id")
    fun observePropertyById(id: Long): Flow<PropertyEntity?>

    @Query("SELECT COUNT(*) FROM properties WHERE isDeleted = 0")
    fun getPropertyCount(): Flow<Int>

    @Query("SELECT * FROM properties WHERE isDeleted = 0 AND (name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%')")
    fun search(query: String): Flow<List<PropertyEntity>>
}
