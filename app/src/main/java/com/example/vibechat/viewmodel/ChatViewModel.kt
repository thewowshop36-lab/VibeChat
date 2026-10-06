package com.example.vibechat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vibechat.data.model.ChatMessage
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

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedContact = MutableStateFlow<UserProfile?>(null)
    val selectedContact: StateFlow<UserProfile?> = _selectedContact.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    val typingStatus: StateFlow<Map<String, Boolean>> = repository.isUserTyping

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
