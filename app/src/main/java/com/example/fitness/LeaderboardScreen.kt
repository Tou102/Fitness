package com.example.fitness

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    currentUserName: String = "John Nguyen" // Default current user for demo
) {
    val selectedUser = remember { mutableStateOf<LeaderboardEntry?>(null) }

    if (selectedUser.value != null) {
        UserDetailsScreen(
            user = selectedUser.value!!,
            currentUserName = currentUserName,
            onBack = { selectedUser.value = null }
        )
    } else {
        val leaderboardData = listOf(
            LeaderboardEntry(1, "John Nguyen", 1200, R.drawable.ava1, 15.5f, 6.5f, "1h 30m"),
            LeaderboardEntry(2, "Anh Duc", 1000, R.drawable.ava2, 12.0f, 5.8f, "1h 15m"),
            LeaderboardEntry(3, "user", 800, R.drawable.ava3, 10.2f, 5.0f, "1h 10m"),
            LeaderboardEntry(4, "Phuc Cuong", 600, R.drawable.ava4, 8.5f, 4.5f, "55m"),
            LeaderboardEntry(5, "Ronaldo", 500, R.drawable.ava5, 7.0f, 4.0f, "50m"),
            LeaderboardEntry(6, "Minh Tuan", 450, R.drawable.beophi, 6.5f, 3.8f, "48m"),
            LeaderboardEntry(7, "Thi Anh", 400, R.drawable.natri1, 6.0f, 3.5f, "45m"),
            LeaderboardEntry(8, "Hoang Long", 350, R.drawable.nguoi_lon, 5.5f, 3.3f, "42m"),
            LeaderboardEntry(9, "Ngoc Mai", 300, R.drawable.theduc, 5.0f, 3.0f, "40m"),
            LeaderboardEntry(10, "Van Khanh", 250, R.drawable.vo, 4.5f, 2.8f, "38m")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFE6F0FA), Color.White)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bảng xếp hạng tuần này ",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A56DB)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFF5F7FA), Color(0xFFE6E9F0))
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hạng",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(56.dp)) // Space for avatar
                        Text(
                            text = "Tên",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Điểm",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.width(80.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    leaderboardData.forEachIndexed { index, entry ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(initialAlpha = 0.3f, animationSpec = androidx.compose.animation.core.tween(delayMillis = index * 100)),
                            exit = fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedUser.value = entry }
                                    .padding(vertical = 8.dp)
                                    .background(
                                        if (entry.name == "user") Color(0xFFE6F0FA) else if (index % 2 == 0) Color(0xFFF9FAFB) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        if (entry.name == "user") 1.dp else 0.dp,
                                        if (entry.name == "user") Color(0xFF1A56DB) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rank Badge
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (entry.rank) {
                                                1 -> Color(0xFFFFD700)
                                                2 -> Color(0xFFC0C0C0)
                                                3 -> Color(0xFFCD7F32)
                                                else -> Color(0xFFE5E7EB)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${entry.rank}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (entry.rank <= 3) Color.White else Color.Black
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Avatar
                                Image(
                                    painter = painterResource(id = entry.avatarResId),
                                    contentDescription = "Avatar ${entry.name}",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Name and Points
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = entry.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (entry.name == "user") FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = Color(0xFF1F2937)
                                        )
                                    )
                                    Text(
                                        text = "${entry.points} điểm",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF6B7280)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1A56DB), Color(0xFF4A90E2))
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Quay Lại",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun UserDetailsScreen(
    user: LeaderboardEntry,
    currentUserName: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE6F0FA), Color.White)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() },
                tint = Color(0xFF1A56DB)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Chi Tiết ${user.name}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A56DB)
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFF5F7FA), Color(0xFFE6E9F0))
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Avatar
                Image(
                    painter = painterResource(id = user.avatarResId),
                    contentDescription = "Avatar ${user.name}",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF1A56DB), CircleShape)
                )

                // Bold name if this is the current user
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = if (user.name == currentUserName) FontWeight.ExtraBold else FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                )

                // Stats Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatRow(
                        icon = Icons.Default.DirectionsRun,
                        label = "Quãng đường",
                        value = "${user.distance} km"
                    )
                    StatRow(
                        icon = Icons.Default.Speed,
                        label = "Tốc độ TB",
                        value = "${user.speed} km/h"
                    )
                    StatRow(
                        icon = Icons.Default.Timer,
                        label = "Thời gian",
                        value = user.time
                    )
                    StatRow(
                        icon = null,
                        label = "Hạng",
                        value = "${user.rank}"
                    )
                    StatRow(
                        icon = null,
                        label = "Điểm",
                        value = "${user.points}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A56DB), Color(0xFF4A90E2))
                    ),
                    RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Quay Lại",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun StatRow(icon: androidx.compose.ui.graphics.vector.ImageVector?, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF1A56DB)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )
        )
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int,
    val avatarResId: Int,
    val distance: Float,
    val speed: Float,
    val time: String
)