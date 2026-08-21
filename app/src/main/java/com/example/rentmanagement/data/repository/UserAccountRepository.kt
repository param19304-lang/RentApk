package com.example.rentmanagement.data.repository

import com.example.rentmanagement.data.dao.UserAccountDao
import com.example.rentmanagement.data.entities.UserAccountEntity
import com.example.rentmanagement.domain.model.UserRole
import com.example.rentmanagement.utils.PasswordHasher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

sealed class AuthResult {
    data class Success(val user: UserAccountEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface UserAccountRepository {
    fun getAllUsers(): Flow<List<UserAccountEntity>>
    suspend fun getById(id: Long): UserAccountEntity?
    fun observeById(id: Long): Flow<UserAccountEntity?>
    suspend fun hasAnyAdmin(): Boolean
    suspend fun login(username: String, password: String): AuthResult
    suspend fun createUser(username: String, password: String, fullName: String, role: UserRole): AuthResult
    suspend fun setActive(id: Long, isActive: Boolean)
}

class UserAccountRepositoryImpl @Inject constructor(
    private val dao: UserAccountDao
) : UserAccountRepository {

    override fun getAllUsers(): Flow<List<UserAccountEntity>> = dao.getAllUsers()
    override suspend fun getById(id: Long): UserAccountEntity? = dao.getById(id)
    override fun observeById(id: Long): Flow<UserAccountEntity?> = dao.observeById(id)
    override suspend fun hasAnyAdmin(): Boolean = dao.countActiveAdmins() > 0

    override suspend fun login(username: String, password: String): AuthResult {
        val user = dao.getByUsername(username.trim())
            ?: return AuthResult.Error("Invalid username or password")
        if (!user.isActive) return AuthResult.Error("This account has been deactivated. Contact your admin.")
        val valid = PasswordHasher.verify(password, user.salt, user.passwordHash)
        return if (valid) AuthResult.Success(user) else AuthResult.Error("Invalid username or password")
    }

    override suspend fun createUser(username: String, password: String, fullName: String, role: UserRole): AuthResult {
        val cleanUsername = username.trim()
        if (cleanUsername.length < 3) {
            return AuthResult.Error("Username must be at least 3 characters")
        }
        if (password.length < 4) {
            return AuthResult.Error("Password must be at least 4 characters")
        }
        if (dao.getByUsername(cleanUsername) != null) {
            return AuthResult.Error("That username is already taken")
        }
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash(password, salt)
        val entity = UserAccountEntity(
            username = cleanUsername,
            passwordHash = hash,
            salt = salt,
            fullName = fullName.trim().ifBlank { cleanUsername },
            role = role
        )
        val id = dao.insert(entity)
        return AuthResult.Success(entity.copy(id = id))
    }

    override suspend fun setActive(id: Long, isActive: Boolean) = dao.setActive(id, isActive)
}
