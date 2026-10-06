package com.example.vibechat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vibechat.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM profiles WHERE id != :currentUserId ORDER BY username ASC")
    fun getAllUsersExcept(currentUserId: String): Flow<List<UserProfile>>

    @Query("SELECT * FROM profiles ORDER BY username ASC")
    fun getAllUsers(): Flow<List<UserProfile>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserProfile?>

    @Query("SELECT * FROM profiles WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserProfile?

    @Query("SELECT * FROM profiles WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserProfile>)

    @Update
    suspend fun updateUser(user: UserProfile)

    @Query("UPDATE profiles SET isOnline = :isOnline, lastSeen = :lastSeen WHERE id = :userId")
    suspend fun updateOnlineStatus(userId: String, isOnline: Boolean, lastSeen: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getUserCount(): Int
}
