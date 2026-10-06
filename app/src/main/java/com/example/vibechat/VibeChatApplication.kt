package com.example.vibechat

import android.app.Application
import com.example.vibechat.data.local.AppDatabase
import com.example.vibechat.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class VibeChatApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy {
        ChatRepository(
            userDao = database.userDao(),
            messageDao = database.messageDao(),
            context = this,
            appScope = applicationScope
        )
    }
}
