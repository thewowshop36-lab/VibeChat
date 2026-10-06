package com.example.vibechat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.vibechat.data.local.MessageDao
import com.example.vibechat.data.local.UserDao
import com.example.vibechat.data.model.ChatMessage
import com.example.vibechat.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(
    private val userDao: UserDao,
    private val messageDao: MessageDao,
    context: Context,
    private val appScope: CoroutineScope
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vibechat_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isUserTyping = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isUserTyping: StateFlow<Map<String, Boolean>> = _isUserTyping.asStateFlow()

    init {
        // Load persisted user session or default to demo user
        val savedUserId = prefs.getString("current_user_id", "user_me") ?: "user_me"
        appScope.launch(Dispatchers.IO) {
            // Check if DB has users, if not populate
            if (userDao.getUserCount() == 0) {
                com.example.vibechat.data.local.AppDatabase.populateInitialData(userDao, messageDao)
            }
            userDao.getUserById(savedUserId).collect { user ->
                if (user != null) {
                    _currentUser.value = user
                } else {
                    // Fallback to demo user
                    val fallback = userDao.getUserByEmail("user@vibechat.io")
                    if (fallback != null) {
                        _currentUser.value = fallback
                        prefs.edit().putString("current_user_id", fallback.id).apply()
                    }
                }
            }
        }
    }

    fun getAllContacts(currentUserId: String): Flow<List<UserProfile>> {
        return userDao.getAllUsersExcept(currentUserId)
    }

    fun getConversation(userA: String, userB: String): Flow<List<ChatMessage>> {
        return messageDao.getConversation(userA, userB)
    }

    fun getLastMessage(userA: String, userB: String): Flow<ChatMessage?> {
        return messageDao.getLastMessage(userA, userB)
    }

    suspend fun signIn(email: String, password: String):Result<UserProfile> {
        val cleanEmail = email.trim().lowercase()
        var user = userDao.getUserByEmail(cleanEmail)
        if (user == null) {
            // Check if matches known demo or create
            val usernameFromEmail = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            user = UserProfile(
                id = "user_" + UUID.randomUUID().toString().take(8),
                username = usernameFromEmail,
                email = cleanEmail,
                isOnline = true,
                avatarColorHex = 0xFF00A884
            )
            userDao.insertUser(user)
        }
        userDao.updateOnlineStatus(user.id, true)
        _currentUser.value = user
        prefs.edit().putString("current_user_id", user.id).apply()
        return Result.success(user)
    }

    suspend fun signUp(username: String, email: String, password: String): Result<UserProfile> {
        val cleanUsername = username.trim()
        val cleanEmail = email.trim().lowercase()

        val existingUser = userDao.getUserByEmail(cleanEmail)
        if (existingUser != null) {
            _currentUser.value = existingUser
            prefs.edit().putString("current_user_id", existingUser.id).apply()
            return Result.success(existingUser)
        }

        val newUser = UserProfile(
            id = "user_" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            email = cleanEmail,
            isOnline = true,
            avatarColorHex = 0xFF00A884
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        prefs.edit().putString("current_user_id", newUser.id).apply()
        return Result.success(newUser)
    }

    suspend fun signOut() {
        _currentUser.value?.let { user ->
            userDao.updateOnlineStatus(user.id, false)
        }
        _currentUser.value = null
        prefs.edit().remove("current_user_id").apply()
    }

    suspend fun togglePresence(userId: String, isOnline: Boolean) {
        userDao.updateOnlineStatus(userId, isOnline)
        _currentUser.value?.let { current ->
            if (current.id == userId) {
                _currentUser.value = current.copy(isOnline = isOnline)
            }
        }
    }

    suspend fun sendMessage(senderId: String, receiverId: String, content: String) {
        val msg = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            content = content.trim(),
            createdAt = System.currentTimeMillis()
        )
        messageDao.insertMessage(msg)

        // Trigger intelligent simulated reply from contact
        triggerSimulatedReply(senderId, receiverId, content)
    }

    private fun triggerSimulatedReply(myId: String, contactId: String, userMessage: String) {
        appScope.launch(Dispatchers.IO) {
            // Set contact online & typing status
            userDao.updateOnlineStatus(contactId, true)
            delay(800)
            _isUserTyping.value = _isUserTyping.value + (contactId to true)
            delay(1500)
            _isUserTyping.value = _isUserTyping.value - contactId

            val replyText = generateContextualReply(userMessage)
            val replyMsg = ChatMessage(
                senderId = contactId,
                receiverId = myId,
                content = replyText,
                createdAt = System.currentTimeMillis()
            )
            messageDao.insertMessage(replyMsg)
        }
    }

    private fun generateContextualReply(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hey! Great to hear from you! How is your day going? 😊"
            lower.contains("how are you") || lower.contains("what's up") ->
                "Doing great, thanks for asking! Just testing out the new VibeChat updates 🚀"
            lower.contains("theme") || lower.contains("dark") || lower.contains("light") ->
                "The dark mode is super easy on the eyes, especially the deep WhatsApp vibe tones!"
            lower.contains("cool") || lower.contains("awesome") || lower.contains("nice") ->
                "Glad you like it! Jetpack Compose animations and Room caching make it buttery smooth ✨"
            lower.contains("bye") || lower.contains("see you") ->
                "Talk soon! Have an amazing rest of your day 👋"
            lower.contains("?") ->
                "That's a good question! Everything is working in real-time with instant persistence."
            else -> {
                val cannedReplies = listOf(
                    "Got it! Thanks for keeping me updated 👍",
                    "Sounds awesome! Let me know if you need anything else.",
                    "Totally agree with you on that!",
                    "That's super interesting! Tell me more ✨",
                    "Nice! Loving this seamless chat experience."
                )
                cannedReplies.random()
            }
        }
    }
}
