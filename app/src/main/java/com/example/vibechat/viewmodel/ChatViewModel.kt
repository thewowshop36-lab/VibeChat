package com.example.vibechat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vibechat.data.model.CallDirection
import com.example.vibechat.data.model.CallLogEntry
import com.example.vibechat.data.model.CallType
import com.example.vibechat.data.model.ChatMessage
import com.example.vibechat.data.model.Community
import com.example.vibechat.data.model.CommunityGroup
import com.example.vibechat.data.model.StatusStory
import com.example.vibechat.data.model.UserProfile
import com.example.vibechat.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class BottomNavTab {
    CHATS, UPDATES, COMMUNITIES, CALLS
}

data class ActiveCallInfo(
    val contactName: String,
    val avatarColor: Long,
    val callType: CallType
)

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    private val _currentTab = MutableStateFlow(BottomNavTab.CHATS)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedContact = MutableStateFlow<UserProfile?>(null)
    val selectedContact: StateFlow<UserProfile?> = _selectedContact.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    val typingStatus: StateFlow<Map<String, Boolean>> = repository.isUserTyping

    // Status Stories State
    private val _stories = MutableStateFlow<List<StatusStory>>(
        listOf(
            StatusStory(
                id = "story_alex",
                userId = "user_alex",
                username = "Alex Rivera",
                userAvatarColor = 0xFF128C7E,
                textCaption = "Excited to test the new VibeChat bottom navigation! 🚀🔥",
                bgGradientColor = 0xFF005C4B,
                timestamp = System.currentTimeMillis() - 40 * 60 * 1000L,
                isViewed = false
            ),
            StatusStory(
                id = "story_sarah",
                userId = "user_sarah",
                username = "Sarah Jenkins",
                userAvatarColor = 0xFF075E54,
                textCaption = "Designing new theme gradients 🎨✨ Have a wonderful Tuesday!",
                bgGradientColor = 0xFF6A1B9A,
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000L,
                isViewed = false
            ),
            StatusStory(
                id = "story_lucas",
                userId = "user_lucas",
                username = "Lucas Miller",
                userAvatarColor = 0xFFE91E63,
                textCaption = "New playlist vibes out now 🎧🎶",
                bgGradientColor = 0xFFC2185B,
                timestamp = System.currentTimeMillis() - 4 * 3600 * 1000L,
                isViewed = true
            )
        )
    )
    val stories: StateFlow<List<StatusStory>> = _stories.asStateFlow()

    private val _activeViewingStory = MutableStateFlow<StatusStory?>(null)
    val activeViewingStory: StateFlow<StatusStory?> = _activeViewingStory.asStateFlow()

    // Communities State
    private val _communities = MutableStateFlow<List<Community>>(
        listOf(
            Community(
                id = "comm_devs",
                name = "Android & Jetpack Developers",
                description = "Modern Android dev discussions, Kotlin & Compose tips.",
                iconColor = 0xFF00A884,
                groups = listOf(
                    CommunityGroup(
                        id = "grp_ann_devs",
                        name = "Announcements",
                        description = "Official announcements for devs",
                        memberCount = 1420,
                        isAnnouncement = true
                    ),
                    CommunityGroup(
                        id = "grp_compose",
                        name = "Compose UI Masters",
                        description = "Everything animations, layouts & M3 design",
                        memberCount = 890
                    ),
                    CommunityGroup(
                        id = "grp_architecture",
                        name = "Clean Architecture & Room",
                        description = "Data layer discussions and performance",
                        memberCount = 650
                    )
                )
            ),
            Community(
                id = "comm_design",
                name = "Vibe Creators Hub",
                description = "Creative space for designers, creators, and artists.",
                iconColor = 0xFF9C27B0,
                groups = listOf(
                    CommunityGroup(
                        id = "grp_ann_design",
                        name = "Hub Announcements",
                        description = "Weekly showcases and design challenges",
                        memberCount = 780,
                        isAnnouncement = true
                    ),
                    CommunityGroup(
                        id = "grp_ui_ux",
                        name = "UI/UX Feedback",
                        description = "Get constructive critique on your designs",
                        memberCount = 430
                    )
                )
            )
        )
    )
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Calls State
    private val _callLogs = MutableStateFlow<List<CallLogEntry>>(
        listOf(
            CallLogEntry(
                id = "call_1",
                contactId = "user_alex",
                contactName = "Alex Rivera",
                contactAvatarColor = 0xFF128C7E,
                callType = CallType.VIDEO,
                direction = CallDirection.INCOMING,
                timestamp = System.currentTimeMillis() - 25 * 60 * 1000L,
                durationSeconds = 145
            ),
            CallLogEntry(
                id = "call_2",
                contactId = "user_sarah",
                contactName = "Sarah Jenkins",
                contactAvatarColor = 0xFF075E54,
                callType = CallType.AUDIO,
                direction = CallDirection.MISSED,
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000L
            ),
            CallLogEntry(
                id = "call_3",
                contactId = "user_jordan",
                contactName = "Jordan Chen",
                contactAvatarColor = 0xFF25D366,
                callType = CallType.AUDIO,
                direction = CallDirection.OUTGOING,
                timestamp = System.currentTimeMillis() - 6 * 3600 * 1000L,
                durationSeconds = 310
            )
        )
    )
    val callLogs: StateFlow<List<CallLogEntry>> = _callLogs.asStateFlow()

    private val _activeCall = MutableStateFlow<ActiveCallInfo?>(null)
    val activeCall: StateFlow<ActiveCallInfo?> = _activeCall.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts: StateFlow<List<UserProfile>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getAllContacts(user.id)
        } else {
            emptyFlow()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredContacts: StateFlow<List<UserProfile>> = combine(contacts, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.username.contains(query, ignoreCase = true) || it.email.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<ChatMessage>> = combine(currentUser, _selectedContact) { user, contact ->
        Pair(user, contact)
    }.flatMapLatest { (user, contact) ->
        if (user != null && contact != null) {
            repository.getConversation(user.id, contact.id)
        } else {
            emptyFlow()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Auth State
    private val _isAuthBusy = MutableStateFlow(false)
    val isAuthBusy: StateFlow<Boolean> = _isAuthBusy.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authInfo = MutableStateFlow<String?>(null)
    val authInfo: StateFlow<String?> = _authInfo.asStateFlow()

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectContact(contact: UserProfile?) {
        _selectedContact.value = contact
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleMyPresence() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.togglePresence(user.id, !user.isOnline)
        }
    }

    fun sendMessage(content: String) {
        val user = currentUser.value ?: return
        val contact = selectedContact.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(user.id, contact.id, content)
        }
    }

    fun getLastMessageFlow(contactId: String): Flow<ChatMessage?> {
        val user = currentUser.value
        return if (user != null) {
            repository.getLastMessage(user.id, contactId)
        } else {
            emptyFlow()
        }
    }

    // Status Actions
    fun viewStory(story: StatusStory) {
        _activeViewingStory.value = story
        // Mark as viewed
        _stories.value = _stories.value.map {
            if (it.id == story.id) it.copy(isViewed = true) else it
        }
    }

    fun dismissStory() {
        _activeViewingStory.value = null
    }

    fun replyToStory(replyText: String) {
        val currentStory = _activeViewingStory.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage(user.id, currentStory.userId, "Replied to status: $replyText")
        }
    }

    fun addMyStory(caption: String = "VibeChat is live! ✨") {
        val user = currentUser.value ?: return
        val myNewStory = StatusStory(
            id = "story_me_" + UUID.randomUUID().toString().take(6),
            userId = user.id,
            username = "My Status",
            userAvatarColor = user.avatarColorHex,
            textCaption = caption,
            bgGradientColor = 0xFF008069,
            timestamp = System.currentTimeMillis(),
            isViewed = false,
            isMine = true
        )
        _stories.value = listOf(myNewStory) + _stories.value.filter { !it.isMine }
        _activeViewingStory.value = myNewStory
    }

    // Call Actions
    fun startCall(contactName: String, avatarColor: Long, type: CallType) {
        _activeCall.value = ActiveCallInfo(
            contactName = contactName,
            avatarColor = avatarColor,
            callType = type
        )
        // Add to call log
        val newLog = CallLogEntry(
            id = "call_" + UUID.randomUUID().toString().take(6),
            contactId = "user_active",
            contactName = contactName,
            contactAvatarColor = avatarColor,
            callType = type,
            direction = CallDirection.OUTGOING,
            timestamp = System.currentTimeMillis()
        )
        _callLogs.value = listOf(newLog) + _callLogs.value
    }

    fun endCall() {
        _activeCall.value = null
    }

    fun signIn(email: String, pass: String) {
        if (email.isBlank()) {
            _authError.value = "Please enter an email address"
            return
        }
        _isAuthBusy.value = true
        _authError.value = null
        _authInfo.value = null
        viewModelScope.launch {
            val result = repository.signIn(email, pass)
            _isAuthBusy.value = false
            result.onSuccess {
                _authError.value = null
            }.onFailure {
                _authError.value = it.message ?: "Failed to sign in"
            }
        }
    }

    fun signUp(username: String, email: String, pass: String) {
        if (username.isBlank() || email.isBlank()) {
            _authError.value = "Please fill in all fields"
            return
        }
        _isAuthBusy.value = true
        _authError.value = null
        _authInfo.value = null
        viewModelScope.launch {
            val result = repository.signUp(username, email, pass)
            _isAuthBusy.value = false
            result.onSuccess {
                _authError.value = null
            }.onFailure {
                _authError.value = it.message ?: "Failed to create account"
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _selectedContact.value = null
            repository.signOut()
        }
    }

    fun switchAccount(email: String) {
        viewModelScope.launch {
            _selectedContact.value = null
            repository.signIn(email, "password123")
        }
    }

    class Factory(private val repository: ChatRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(repository) as T
        }
    }
}
