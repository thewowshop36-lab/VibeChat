package com.example.vibechat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibechat.data.model.UserProfile
import com.example.vibechat.ui.components.AvatarView
import com.example.vibechat.ui.components.SearchBox
import com.example.vibechat.ui.screens.tabs.CallsTabScreen
import com.example.vibechat.ui.screens.tabs.CommunitiesTabScreen
import com.example.vibechat.ui.screens.tabs.UpdatesTabScreen
import com.example.vibechat.ui.theme.OfflineGray
import com.example.vibechat.ui.theme.OnlineGreen
import com.example.vibechat.ui.theme.VibeGreen
import com.example.vibechat.ui.theme.VibeTeal
import com.example.vibechat.viewmodel.BottomNavTab
import com.example.vibechat.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SidebarScreen(
    viewModel: ChatViewModel,
    currentUser: UserProfile,
    contacts: List<UserProfile>,
    selectedContactId: String?,
    onSelectContact: (UserProfile) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val communities by viewModel.communities.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Tab Viewport
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    BottomNavTab.CHATS -> {
                        ChatsListView(
                            viewModel = viewModel,
                            currentUser = currentUser,
                            contacts = contacts,
                            selectedContactId = selectedContactId,
                            searchQuery = searchQuery,
                            onSelectContact = onSelectContact,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = onToggleDarkMode,
                            onSignOut = onSignOut
                        )
                    }
                    BottomNavTab.UPDATES -> {
                        UpdatesTabScreen(
                            currentUser = currentUser,
                            stories = stories,
                            onViewStory = { viewModel.viewStory(it) },
                            onAddStory = { viewModel.addMyStory() }
                        )
                    }
                    BottomNavTab.COMMUNITIES -> {
                        CommunitiesTabScreen(
                            communities = communities,
                            onGroupClick = { _, _ ->
                                if (contacts.isNotEmpty()) onSelectContact(contacts.first())
                            },
                            onNewCommunity = {}
                        )
                    }
                    BottomNavTab.CALLS -> {
                        CallsTabScreen(
                            callLogs = callLogs,
                            onStartCall = { name, color, type ->
                                viewModel.startCall(name, color, type)
                            }
                        )
                    }
                }
            }

            // WhatsApp Style Bottom Navigation Bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 6.dp
            ) {
                // Chats Tab
                NavigationBarItem(
                    selected = currentTab == BottomNavTab.CHATS,
                    onClick = { viewModel.selectTab(BottomNavTab.CHATS) },
                    icon = {
                        BadgedBox(badge = {
                            Badge(containerColor = VibeGreen) { Text("${contacts.size}") }
                        }) {
                            Icon(Icons.Default.Chat, contentDescription = "Chats")
                        }
                    },
                    label = { Text("Chats", fontWeight = if (currentTab == BottomNavTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibeGreen,
                        selectedTextColor = VibeGreen,
                        indicatorColor = VibeGreen.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_chats")
                )

                // Updates / Status Tab
                NavigationBarItem(
                    selected = currentTab == BottomNavTab.UPDATES,
                    onClick = { viewModel.selectTab(BottomNavTab.UPDATES) },
                    icon = {
                        BadgedBox(badge = {
                            val unviewedCount = stories.count { !it.isViewed && !it.isMine }
                            if (unviewedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(OnlineGreen)
                                )
                            }
                        }) {
                            Icon(Icons.Default.Update, contentDescription = "Updates")
                        }
                    },
                    label = { Text("Updates", fontWeight = if (currentTab == BottomNavTab.UPDATES) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibeGreen,
                        selectedTextColor = VibeGreen,
                        indicatorColor = VibeGreen.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_updates")
                )

                // Communities Tab
                NavigationBarItem(
                    selected = currentTab == BottomNavTab.COMMUNITIES,
                    onClick = { viewModel.selectTab(BottomNavTab.COMMUNITIES) },
                    icon = {
                        Icon(Icons.Default.Groups, contentDescription = "Communities")
                    },
                    label = { Text("Communities", fontWeight = if (currentTab == BottomNavTab.COMMUNITIES) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibeGreen,
                        selectedTextColor = VibeGreen,
                        indicatorColor = VibeGreen.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_communities")
                )

                // Calls Tab
                NavigationBarItem(
                    selected = currentTab == BottomNavTab.CALLS,
                    onClick = { viewModel.selectTab(BottomNavTab.CALLS) },
                    icon = {
                        Icon(Icons.Default.Call, contentDescription = "Calls")
                    },
                    label = { Text("Calls", fontWeight = if (currentTab == BottomNavTab.CALLS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibeGreen,
                        selectedTextColor = VibeGreen,
                        indicatorColor = VibeGreen.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_calls")
                )
            }
        }
    }
}

@Composable
private fun ChatsListView(
    viewModel: ChatViewModel,
    currentUser: UserProfile,
    contacts: List<UserProfile>,
    selectedContactId: String?,
    searchQuery: String,
    onSelectContact: (UserProfile) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSignOut: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarView(
                    username = currentUser.username,
                    size = 40.dp,
                    avatarColorHex = currentUser.avatarColorHex,
                    showOnlineBadge = true,
                    isOnline = currentUser.isOnline
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VibeChat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VibeGreen
                    )
                    Text(
                        text = "${currentUser.username} • ${if (currentUser.isOnline) "Online" else "Offline"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (currentUser.isOnline) OnlineGreen else OfflineGray
                    )
                }

                // Presence Toggle
                IconButton(
                    onClick = { viewModel.toggleMyPresence() },
                    modifier = Modifier.testTag("presence_toggle_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (currentUser.isOnline) OnlineGreen else OfflineGray)
                    )
                }

                // Theme Toggle
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Logout Button
                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SearchBox(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Contact List
            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No users found" else "No contacts available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(contacts, key = { it.id }) { user ->
                        val isSelected = user.id == selectedContactId
                        val lastMessage by viewModel.getLastMessageFlow(user.id).collectAsState(initial = null)

                        ContactItem(
                            user = user,
                            isSelected = isSelected,
                            lastMessageSnippet = lastMessage?.content ?: user.statusMessage,
                            lastMessageTime = lastMessage?.createdAt,
                            onClick = { onSelectContact(user) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }

        // New Chat FAB
        FloatingActionButton(
            onClick = {
                if (contacts.isNotEmpty()) onSelectContact(contacts.first())
            },
            containerColor = VibeGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_new_chat")
        ) {
            Icon(
                imageVector = Icons.Default.AddComment,
                contentDescription = "New Chat"
            )
        }
    }
}

@Composable
private fun ContactItem(
    user: UserProfile,
    isSelected: Boolean,
    lastMessageSnippet: String,
    lastMessageTime: Long?,
    onClick: () -> Unit
) {
    val timeStr = remember(lastMessageTime) {
        if (lastMessageTime != null && lastMessageTime > 0) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastMessageTime))
        } else ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("user_item_${user.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(
            username = user.username,
            size = 48.dp,
            avatarColorHex = user.avatarColorHex,
            showOnlineBadge = true,
            isOnline = user.isOnline
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (timeStr.isNotEmpty()) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lastMessageSnippet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (user.isOnline) {
                    Text(
                        text = "online",
                        fontSize = 11.sp,
                        color = OnlineGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
