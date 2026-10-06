package com.example.vibechat.data.model

enum class CallType {
    AUDIO, VIDEO
}

enum class CallDirection {
    INCOMING, OUTGOING, MISSED
}

data class CallLogEntry(
    val id: String,
    val contactId: String,
    val contactName: String,
    val contactAvatarColor: Long,
    val callType: CallType,
    val direction: CallDirection,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
)
