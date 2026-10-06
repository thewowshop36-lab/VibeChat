package com.example.vibechat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibechat.data.model.ChatMessage
import com.example.vibechat.data.model.UserProfile
import com.example.vibechat.ui.components.AvatarView
import com.example.vibechat.ui.theme.OnlineGreen
import com.example.vibechat.ui.theme.VibeBlueTick
import com.example.vibechat.ui.theme.VibeDarkBubbleIn
import com.example.vibechat.ui.theme.VibeDarkBubbleOut
import com.example.vibechat.ui.theme.VibeDarkChatBg
import com.example.vibechat.ui.theme.VibeGreen
import com.example.vibechat.ui.theme.VibeLightBubbleIn
import com.example.vibechat.ui.theme.VibeLightBubbleOut
import com.example.vibechat.ui.theme.VibeLightChatBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatWindowScreen(
    currentUser: UserProfile,
    otherUser: UserProfile,
    messages: List<ChatMessage>,
    isTyping: Boolean,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onStartCall: (String, Long, com.example.vibechat.data.model.CallType) -> Unit = { _, _, _ -> },
    showBackButton: Boolean = true,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to latest message on receive/send
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val chatBgColor = if (isDarkMode) VibeDarkChatBg else VibeLightChatBg
    val bubbleOutColor = if (isDarkMode) VibeDarkBubbleOut else VibeLightBubbleOut
    val bubbleInColor = if (isDarkMode) VibeDarkBubbleIn else VibeLightBubbleIn

    Surface(
        modifier = modifier.fillMaxSize(),
        color = chatBgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .testTag("chat_window_header"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                AvatarView(
                    username = otherUser.username,
                    size = 40.dp,
                    avatarColorHex = otherUser.avatarColorHex,
                    showOnlineBadge = true,
                    isOnline = otherUser.isOnline
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = otherUser.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when {
                            isTyping -> "typing..."
                            otherUser.isOnline -> "online"
                            else -> "offline"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isTyping) VibeGreen else if (otherUser.isOnline) OnlineGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    onStartCall(otherUser.username, otherUser.avatarColorHex, com.example.vibechat.data.model.CallType.VIDEO)
                }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    onStartCall(otherUser.username, otherUser.avatarColorHex, com.example.vibechat.data.model.CallType.AUDIO)
                }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Messages Stream
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && !isTyping) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "🔒 Messages are encrypted & stored on device.\nSay hello to ${otherUser.username}!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            val isMine = msg.senderId == currentUser.id
                            MessageBubble(
                                message = msg,
                                isMine = isMine,
                                bubbleColor = if (isMine) bubbleOutColor else bubbleInColor
                            )
                        }

                        if (isTyping) {
                            item {
                                TypingIndicatorBubble(bubbleColor = bubbleInColor)
                            }
                        }
                    }
                }
            }

            // Message Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rounded Input Box
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { inputText += "😊" },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = "Emojis",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("message_input"),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(VibeGreen),
                        maxLines = 4,
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Type a message",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach file",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                FloatingActionButton(
                    onClick = {
                        val textToSend = inputText.trim()
                        if (textToSend.isNotEmpty()) {
                            onSendMessage(textToSend)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("send_button"),
                    shape = CircleShape,
                    containerColor = VibeGreen,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMine: Boolean,
    bubbleColor: Color
) {
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isMine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    val timeStr = remember(message.createdAt) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.createdAt))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            shape = shape,
            color = bubbleColor,
            modifier = Modifier
                .widthIn(max = 290.dp)
                .shadow(1.dp, shape)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = VibeBlueTick,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicatorBubble(bubbleColor: Color) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = bubbleColor,
            modifier = Modifier
                .padding(4.dp)
                .shadow(1.dp, RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "typing…",
                    style = MaterialTheme.typography.labelSmall,
                    color = VibeGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
