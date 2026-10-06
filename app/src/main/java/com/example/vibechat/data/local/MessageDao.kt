package com.example.vibechat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vibechat.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :userA AND receiverId = :userB) 
           OR (senderId = :userB AND receiverId = :userA)
        ORDER BY createdAt ASC
    """)
    fun getConversation(userA: String, userB: String): Flow<List<ChatMessage>>

    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :userA AND receiverId = :userB) 
           OR (senderId = :userB AND receiverId = :userA)
        ORDER BY createdAt DESC LIMIT 1
    """)
    fun getLastMessage(userA: String, userB: String): Flow<ChatMessage?>

    @Query("""
        SELECT * FROM messages 
        WHERE senderId = :userId OR receiverId = :userId 
        ORDER BY createdAt DESC
    """)
    fun getAllMessagesForUser(userId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("DELETE FROM messages WHERE (senderId = :userA AND receiverId = :userB) OR (senderId = :userB AND receiverId = :userA)")
    suspend fun clearConversation(userA: String, userB: String)
}
