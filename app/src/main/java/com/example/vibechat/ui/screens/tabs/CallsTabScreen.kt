package com.example.vibechat.ui.screens.tabs

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.vibechat.data.model.CallDirection
import com.example.vibechat.data.model.CallLogEntry
import com.example.vibechat.data.model.CallType
import com.example.vibechat.ui.components.AvatarView
import com.example.vibechat.ui.theme.OnlineGreen
import com.example.vibechat.ui.theme.VibeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallsTabScreen(
    callLogs: List<CallLogEntry>,
    onStartCall: (String, Long, CallType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calls",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search calls",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Create Call Link Item
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(VibeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Create call link",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Create call link",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Share a link for your WhatsApp call",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Recent",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // Recent Calls List
            items(callLogs, key = { it.id }) { log ->
                CallLogRow(
                    log = log,
                    onAudioCall = { onStartCall(log.contactName, log.contactAvatarColor, CallType.AUDIO) },
                    onVideoCall = { onStartCall(log.contactName, log.contactAvatarColor, CallType.VIDEO) }
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

        // Start Call Floating Action Button
        FloatingActionButton(
            onClick = {
                if (callLogs.isNotEmpty()) {
                    val first = callLogs.first()
                    onStartCall(first.contactName, first.contactAvatarColor, CallType.AUDIO)
                }
            },
            containerColor = VibeGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_new_call")
        ) {
            Icon(
                imageVector = Icons.Default.AddIcCall,
                contentDescription = "New Call"
            )
        }
    }
}

@Composable
private fun CallLogRow(
    log: CallLogEntry,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    val timeStr = remember(log.timestamp) {
        SimpleDateFormat("MMMM d, HH:mm", Locale.getDefault()).format(Date(log.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAudioCall)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("call_item_${log.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(
            username = log.contactName,
            size = 48.dp,
            avatarColorHex = log.contactAvatarColor
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.contactName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (log.direction == CallDirection.MISSED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (log.direction) {
                        CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                        CallDirection.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                        CallDirection.MISSED -> Icons.AutoMirrored.Filled.CallReceived
                    },
                    contentDescription = null,
                    tint = if (log.direction == CallDirection.MISSED) MaterialTheme.colorScheme.error else OnlineGreen,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = if (log.callType == CallType.VIDEO) onVideoCall else onAudioCall) {
            Icon(
                imageVector = if (log.callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "Call again",
                tint = VibeGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
