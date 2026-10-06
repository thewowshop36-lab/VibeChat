package com.example.vibechat.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vibechat.ui.screens.auth.AuthScreen
import com.example.vibechat.ui.screens.chat.ChatWindowScreen
import com.example.vibechat.ui.screens.chat.SidebarScreen
import com.example.vibechat.ui.screens.tabs.ActiveCallDialog
import com.example.vibechat.ui.screens.tabs.StatusViewerDialog
import com.example.vibechat.viewmodel.ChatViewModel

@Composable
fun MainChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAuthBusy by viewModel.isAuthBusy.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val authInfo by viewModel.authInfo.collectAsState()

    val contacts by viewModel.filteredContacts.collectAsState()
    val selectedContact by viewModel.selectedContact.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val typingStatus by viewModel.typingStatus.collectAsState()

    val activeStory by viewModel.activeViewingStory.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()

    if (currentUser == null) {
        AuthScreen(
            isBusy = isAuthBusy,
            errorMessage = authError,
            infoMessage = authInfo,
            onSignIn = { email, pass -> viewModel.signIn(email, pass) },
            onSignUp = { user, email, pass -> viewModel.signUp(user, email, pass) },
            onQuickSwitch = { email -> viewModel.switchAccount(email) },
            modifier = modifier
        )
        return
    }

    val user = currentUser!!

    // Active Status Viewer Dialog
    if (activeStory != null) {
        StatusViewerDialog(
            story = activeStory!!,
            onDismiss = { viewModel.dismissStory() },
            onReply = { reply -> viewModel.replyToStory(reply) }
        )
    }

    // Active Simulated Voice/Video Call Dialog
    if (activeCall != null) {
        val call = activeCall!!
        ActiveCallDialog(
            contactName = call.contactName,
            avatarColor = call.avatarColor,
            callType = call.callType,
            onEndCall = { viewModel.endCall() }
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Dual-Pane Layout (Tablet / Landscape)
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarScreen(
                    viewModel = viewModel,
                    currentUser = user,
                    contacts = contacts,
                    selectedContactId = selectedContact?.id,
                    onSelectContact = { viewModel.selectContact(it) },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onSignOut = { viewModel.signOut() },
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (selectedContact != null) {
                        val contact = selectedContact!!
                        val isTyping = typingStatus[contact.id] ?: false
                        ChatWindowScreen(
                            currentUser = user,
                            otherUser = contact,
                            messages = activeMessages,
                            isTyping = isTyping,
                            onSendMessage = { viewModel.sendMessage(it) },
                            onBack = { viewModel.selectContact(null) },
                            onStartCall = { name, color, type -> viewModel.startCall(name, color, type) },
                            showBackButton = false,
                            isDarkMode = isDarkMode
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select a contact to start chatting",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Single-Pane Stack (Mobile)
            if (selectedContact != null) {
                val contact = selectedContact!!
                val isTyping = typingStatus[contact.id] ?: false

                BackHandler {
                    viewModel.selectContact(null)
                }

                ChatWindowScreen(
                    currentUser = user,
                    otherUser = contact,
                    messages = activeMessages,
                    isTyping = isTyping,
                    onSendMessage = { viewModel.sendMessage(it) },
                    onBack = { viewModel.selectContact(null) },
                    onStartCall = { name, color, type -> viewModel.startCall(name, color, type) },
                    showBackButton = true,
                    isDarkMode = isDarkMode
                )
            } else {
                SidebarScreen(
                    viewModel = viewModel,
                    currentUser = user,
                    contacts = contacts,
                    selectedContactId = null,
                    onSelectContact = { viewModel.selectContact(it) },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onSignOut = { viewModel.signOut() }
                )
            }
        }
    }
}
