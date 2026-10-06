package com.example.vibechat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibechat.ui.theme.OnlineGreen

@Composable
fun AvatarView(
    username: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    avatarColorHex: Long = 0xFF00A884,
    showOnlineBadge: Boolean = false,
    isOnline: Boolean = false
) {
    val initial = if (username.isNotBlank()) username.trim().first().uppercaseChar() else '?'
    val initialColor = Color(avatarColorHex)

    Box(
        modifier = modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(initialColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial.toString(),
                color = Color.White,
                fontSize = (size.value * 0.42).sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showOnlineBadge && isOnline) {
            val badgeSize = (size.value * 0.3).dp.coerceIn(10.dp, 14.dp)
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .background(OnlineGreen)
            )
        }
    }
}
