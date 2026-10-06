package com.example.vibechat.data.model

data class StatusStory(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatarColor: Long,
    val textCaption: String,
    val bgGradientColor: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val isMine: Boolean = false
)
