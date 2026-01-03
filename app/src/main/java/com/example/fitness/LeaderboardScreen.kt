package com.example.fitness.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitness.model.LeaderboardEntry
import com.example.fitness.model.sampleLeaderboard
import com.example.fitness.ui.theme.*

@Composable
fun LeaderboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SurfaceStart, SurfaceEnd)
                ),
                shape = RectangleShape
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Bảng Xếp Hạng\nTháng 1/2026",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            lineHeight = 32.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sampleLeaderboard) { entry ->
                LeaderboardItem(
                    entry = entry,
                    onClick = {
                        navController.navigate("user_detail/${entry.userId}")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                "Quay lại",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LeaderboardItem(
    entry: LeaderboardEntry,
    onClick: () -> Unit
) {
    val isTop3 = entry.rank <= 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isTop3) 10.dp else 6.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isTop3) BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f)) else null,
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (isTop3) {
                        when (entry.rank) {
                            1 -> Brush.verticalGradient(listOf(Color(0xFFFFF9E6), Color.White))
                            2 -> Brush.verticalGradient(listOf(Color(0xFFF5F5F5), Color.White))
                            3 -> Brush.verticalGradient(listOf(Color(0xFFFDE8D7), Color.White))
                            else -> Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    } else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank badge + huy chương (ngắn gọn, cân đối)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            when (entry.rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> PrimaryBlue.copy(alpha = 0.18f)
                            },
                            CircleShape
                        )
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = entry.rank.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTop3) Color.White else PrimaryBlue
                        )
                        if (isTop3) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Huy chương",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(14.dp)  // Cúp ngắn gọn, không dài
                                    .padding(top = 0.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar
                AsyncImage(
                    model = entry.avatarUrl ?: "https://i.pravatar.cc/150?img=68",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClick)
                        .shadow(3.dp, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Thông tin user + thành tựu
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.nickname,
                        fontSize = 18.sp,
                        fontWeight = if (isTop3) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isTop3) PrimaryBlue else TextPrimary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildString {
                            append("%.1f km".format(entry.totalDistanceKm))
                            append(" • ${entry.totalCalories} kcal")
                            append(" • ${entry.totalTimeMinutes / 60}h${entry.totalTimeMinutes % 60}'")
                        },
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    // Thành tựu cho top 3 (ngắn gọn, cân đối)
                    if (isTop3) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),  // Giảm khoảng cách
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AchievementBadge("Vua Chạy", Color(0xFFFFD700))
                            AchievementBadge("Kỷ Lục", Color(0xFF4CAF50))
                            if (entry.rank == 1) {
                                AchievementBadge("Streak 30", Color(0xFFE91E63))
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AchievementBadge(
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)  // Bo góc nhỏ hơn
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),  // Padding hẹp → badge ngắn
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)  // Icon cúp nhỏ gọn
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 9.sp,  // Font nhỏ → badge không dài
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}