package com.example.fitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// --- 1. DATA MODELS (Cấu trúc dữ liệu) ---

// Định nghĩa các phần cơ thể (Dùng Enum để dễ quản lý)
enum class BodyPart(val title: String) {
    BUNG("Bụng"),
    TAY("Cánh tay"),
    NGUC("Ngực"),
    CHAN("Chân")
}

// Model cho một Gói bài tập (Workout Plan)
data class WorkoutPlan(
    val id: Int,
    val name: String,         // VD: Bụng người bắt đầu
    val duration: Int,        // VD: 20 (phút)
    val exerciseCount: Int,   // VD: 16 (bài)
    val difficulty: Int,      // 1: Dễ, 2: Vừa, 3: Khó (để vẽ tia sét)
    val part: BodyPart,       // Thuộc nhóm cơ nào?
    val imageRes: Int         // Ảnh bìa
)

// --- 2. MOCK DATA (Dữ liệu giả lập) ---
val allWorkoutPlans = listOf(
    // BỤNG
    WorkoutPlan(1, "Bụng Người bắt đầu", 20, 16, 1, BodyPart.BUNG, R.drawable.abs1),
    WorkoutPlan(2, "Bụng Trung bình", 29, 21, 2, BodyPart.BUNG, R.drawable.abs2),
    WorkoutPlan(3, "Bụng Nâng cao", 36, 21, 3, BodyPart.BUNG, R.drawable.abs3),

    // TAY
    WorkoutPlan(4, "Cánh tay Người bắt đầu", 15, 12, 1, BodyPart.TAY, R.drawable.arm),
    WorkoutPlan(5, "Cơ bắp tay Trung bình", 25, 18, 2, BodyPart.TAY, R.drawable.hitdat1),

    // NGỰC
    WorkoutPlan(6, "Ngực nở Người bắt đầu", 20, 15, 2, BodyPart.NGUC, R.drawable.chest1),

    // CHÂN
    WorkoutPlan(7, "Chân Người bắt đầu", 30, 20, 2, BodyPart.CHAN, R.drawable.leg1)
)

// --- 3. UI COMPONENTS ---

@Composable
fun MainWorkoutScreen(navController: NavHostController) {
    // State lưu giữ tab đang được chọn, mặc định là BỤNG
    var selectedPart by remember { mutableStateOf(BodyPart.BUNG) }

    // Lọc danh sách: Chỉ lấy những bài tập thuộc phần cơ thể đang chọn
    val filteredList = allWorkoutPlans.filter { it.part == selectedPart }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Cơ thể tập trung",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // A. Hàng Tabs (Bụng - Cánh Tay - Ngực...)
        BodyPartTabs(selectedPart = selectedPart, onSelect = { selectedPart = it })

        Spacer(modifier = Modifier.height(20.dp))

        // B. Danh sách bài tập (Đã lọc)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa các item
        ) {
            items(filteredList) { plan ->
                WorkoutPlanItem(
                    plan = plan,
                    onClick = {
                        // Chuyển sang màn hình chi tiết, kèm theo ID gói tập
                        navController.navigate("plan_detail/${plan.id}")
                    }
                )
            }
        }
    }
}

// Component hiển thị hàng Tabs
@Composable
fun BodyPartTabs(selectedPart: BodyPart, onSelect: (BodyPart) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(BodyPart.values()) { part ->
            val isSelected = part == selectedPart
            val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF5F5F5) // Xanh nhạt hoặc Xám
            val textColor = if (isSelected) Color(0xFF1976D2) else Color.Gray
            val borderColor = if (isSelected) Color(0xFF1976D2) else Color.Transparent

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { onSelect(part) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = part.title,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Component hiển thị từng Item bài tập
@Composable
fun WorkoutPlanItem(plan: WorkoutPlan, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick()  /* Thêm sự kiện click nếu cần */ }
            .padding(vertical = 8.dp), // Thêm padding dọc để các hàng không dính sát nhau
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Hình ảnh (Đã tăng kích thước)
        Image(
            painter = painterResource(id = plan.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp) // TĂNG TỪ 80.dp LÊN 100.dp
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(20.dp)) // Tăng khoảng cách với text một chút cho thoáng

        // 2. Nội dung text + Tia sét
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plan.name,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp // Tăng cỡ chữ tiêu đề lên một chút
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${plan.duration} phút • ${plan.exerciseCount} Bài tập",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Vẽ các tia sét biểu thị độ khó
            Row {
                repeat(3) { index ->
                    val iconColor = if (index < plan.difficulty) Color(0xFF2979FF) else Color(0xFFE0E0E0)
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp) // Tăng nhẹ cỡ icon tia sét
                    )
                }
            }
        }
    }
}