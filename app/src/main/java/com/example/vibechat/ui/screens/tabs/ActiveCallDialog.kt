package com.example.vibechat.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vibechat.data.model.CallType
import com.example.vibechat.ui.components.AvatarView
import com.example.vibechat.ui.theme.VibeDarkBg
import com.example.vibechat.ui.theme.VibeTealDark
import kotlinx.coroutines.delay

@Composable
fun ActiveCallDialog(
    contactName: String,
    avatarColor: Long,
    callType: CallType,
    onEndCall: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(callType == CallType.AUDIO) }
    var isSpeakerOn by remember { mutableStateOf(false) }
    var callDurationSeconds by remember { mutableIntStateOf(0) }
    var callStatus by remember { mutableStateOf("Ringing…") }

    LaunchedEffect(Unit) {
        delay(2000)
        callStatus = "Connected"
        while (true) {
            delay(1000)
            callDurationSeconds++
        }
    }

    val minutes = callDurationSeconds / 60
    val seconds = callDurationSeconds % 60
    val durationStr = String.format("%02d:%02d", minutes, seconds)

    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Contact Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarView(
                        username = contactName,
                        size = 100.dp,
                        avatarColorHex = avatarColor
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = contactName,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (callStatus == "Connected") durationStr else callStatus,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = if (callType == CallType.VIDEO) "WhatsApp Video Call" else "WhatsApp Voice Call",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }

                // Call Controls Bar
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isSpeakerOn = !isSpeakerOn },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isSpeakerOn) Color.White else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaker",
                                tint = if (isSpeakerOn) Color.Black else Color.White
                            )
                        }

                        if (callType == CallType.VIDEO) {
                            IconButton(
                                onClick = { isVideoOff = !isVideoOff },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isVideoOff) Color.White.copy(alpha = 0.2f) else Color.White)
                            ) {
                                Icon(
                                    imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = "Camera",
                                    tint = if (isVideoOff) Color.White else Color.Black
                                )
                            }
                        }

                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (isMuted) Color.Black else Color.White
                            )
                        }

                        // End Call Button
                        FloatingActionButton(
                            onClick = onEndCall,
                            containerColor = Color(0xFFE53935),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("end_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Call",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
