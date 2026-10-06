package com.example.vibechat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val statusMessage: String = "Hey there! I am using VibeChat.",
    val avatarColorHex: Long = 0xFF00A884,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)
