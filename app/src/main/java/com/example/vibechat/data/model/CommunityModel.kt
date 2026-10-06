package com.example.vibechat.data.model

data class CommunityGroup(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val isAnnouncement: Boolean = false,
    val lastActivityTime: Long = System.currentTimeMillis()
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val iconColor: Long,
    val groups: List<CommunityGroup>
)
