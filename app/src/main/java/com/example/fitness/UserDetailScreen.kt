package com.example.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitness.model.LeaderboardEntry
import com.example.fitness.model.sampleLeaderboard
import com.example.fitness.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: Int
) {
    // Tìm user từ dữ liệu mẫu (sau này thay bằng ViewModel + DB)
    val user = sampleLeaderboard.find { it.userId == userId }
        ?: LeaderboardEntry(0, 0, "Không tìm thấy", null, 0f, 0, 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
    ) {
        // AppBar đơn giản
        TopAppBar(
            title = { Text("Chi tiết thành tích") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PrimaryBlue,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar lớn
            AsyncImage(
                model = user.avatarUrl ?: "https://i.pravatar.cc/150?img=68",
                contentDescription = "Avatar của ${user.nickname}",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tên & hạng
            Text(
                text = user.nickname,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hạng ${user.rank} • Tháng 1/2026",
                fontSize = 16.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Thống kê chính (card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StatItem("Tổng khoảng cách", "%.1f km".format(user.totalDistanceKm))
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    StatItem("Tổng calo đốt", "${user.totalCalories} kcal")
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    StatItem("Thời gian tập", "${user.totalTimeMinutes / 60}h ${user.totalTimeMinutes % 60}'")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nút xem lịch sử (tùy chọn mở rộng sau)
            Button(
                onClick = { /* TODO: mở lịch sử chạy bộ của user */ },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xem lịch sử hoạt động", color = Color.White)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}