package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.PropertyDao
import com.example.rentmanagement.data.entities.PropertyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PropertyRepository {
    fun getAllProperties(): Flow<List<PropertyEntity>>
    suspend fun getPropertyById(id: Long): PropertyEntity?
    fun observePropertyById(id: Long): Flow<PropertyEntity?>
    fun getPropertyCount(): Flow<Int>
    fun search(query: String): Flow<List<PropertyEntity>>
    suspend fun addProperty(property: PropertyEntity): Long
    suspend fun updateProperty(property: PropertyEntity)
    suspend fun deleteProperty(id: Long)
}

class PropertyRepositoryImpl @Inject constructor(
    private val dao: PropertyDao
) : PropertyRepository {
    override fun getAllProperties(): Flow<List<PropertyEntity>> = dao.getAllProperties()
    override suspend fun getPropertyById(id: Long): PropertyEntity? = dao.getPropertyById(id)
    override fun observePropertyById(id: Long): Flow<PropertyEntity?> = dao.observePropertyById(id)
    override fun getPropertyCount(): Flow<Int> = dao.getPropertyCount()
    override fun search(query: String): Flow<List<PropertyEntity>> = dao.search(query)
    override suspend fun addProperty(property: PropertyEntity): Long = dao.insert(property)
    override suspend fun updateProperty(property: PropertyEntity) = dao.update(property)
    override suspend fun deleteProperty(id: Long) = dao.softDelete(id)
}
