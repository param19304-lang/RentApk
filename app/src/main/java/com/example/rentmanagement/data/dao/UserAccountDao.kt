package com.example.rentmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentmanagement.data.entities.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Insert
    suspend fun insert(user: UserAccountEntity): Long

    @Update
    suspend fun update(user: UserAccountEntity)

    @Query("SELECT * FROM user_accounts WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun getByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :id")
    suspend fun getById(id: Long): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :id")
    fun observeById(id: Long): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts ORDER BY createdAt ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT COUNT(*) FROM user_accounts WHERE role = 'ADMIN' AND isActive = 1")
    suspend fun countActiveAdmins(): Int

    @Query("UPDATE user_accounts SET isActive = :isActive WHERE id = :id")
    suspend fun setActive(id: Long, isActive: Boolean)
}
