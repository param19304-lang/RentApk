package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.DocumentDao
import com.example.rentmanagement.data.entities.DocumentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<DocumentEntity>>
    fun getDocumentsForTenant(tenantId: Long): Flow<List<DocumentEntity>>
    suspend fun addDocument(document: DocumentEntity): Long
    suspend fun deleteDocument(id: Long)
}

class DocumentRepositoryImpl @Inject constructor(
    private val dao: DocumentDao
) : DocumentRepository {
    override fun getAllDocuments() = dao.getAllDocuments()
    override fun getDocumentsForTenant(tenantId: Long) = dao.getDocumentsForTenant(tenantId)
    override suspend fun addDocument(document: DocumentEntity) = dao.insert(document)
    override suspend fun deleteDocument(id: Long) = dao.softDelete(id)
}
