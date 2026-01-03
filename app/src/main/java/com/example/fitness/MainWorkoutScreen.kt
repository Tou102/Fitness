package com.example.fitness

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Giữ nguyên enum & data class
enum class BodyPart(val title: String) {
    BUNG("Bụng"), TAY("Cánh tay"), NGUC("Ngực"), CHAN("Chân")
}

data class WorkoutPlan(
    val id: Int,
    val name: String,
    val duration: Int,
    val exerciseCount: Int,
    val difficulty: Int,
    val part: BodyPart,
    val imageRes: Int
)

// Mock data giữ nguyên
val allWorkoutPlans = listOf(
    WorkoutPlan(1, "Bụng Cơ Bản", 10, 13, 1, BodyPart.BUNG, R.drawable.abs1),
    WorkoutPlan(2, "Bụng Trung Cấp", 15,16 , 2, BodyPart.BUNG, R.drawable.abs2),
    WorkoutPlan(3, "Bụng Nâng Cao", 15, 16, 3, BodyPart.BUNG, R.drawable.abs3),

    WorkoutPlan(4, "Cánh Tay Cơ Bản", 10, 8, 1, BodyPart.TAY, R.drawable.arm),
    WorkoutPlan(5, "Cánh Tay Trung Cấp", 25, 8, 2, BodyPart.TAY, R.drawable.tay2),
    WorkoutPlan(6, "Cánh Tay Nâng Cao", 25, 8, 3, BodyPart.TAY, R.drawable.tay),

    WorkoutPlan(7, "Ngực Cơ Bản", 20, 8, 1, BodyPart.NGUC, R.drawable.chest1),
    WorkoutPlan(8, "Ngực Trung Cấp", 20, 8, 2, BodyPart.NGUC, R.drawable.nguc),
    WorkoutPlan(9, "Ngực Nâng Cao", 20, 8, 3, BodyPart.NGUC, R.drawable.nguc3),

    WorkoutPlan(10, "Chân Cơ Bản", 30, 8, 1, BodyPart.CHAN, R.drawable.leg1),
    WorkoutPlan(11, "Chân Trung Cấp", 30, 9, 2, BodyPart.CHAN, R.drawable.chan3),
    WorkoutPlan(12, "Chân Nâng Cao", 30, 10, 3, BodyPart.CHAN, R.drawable.chan10),

    )

// Màu sắc xanh dương đồng bộa
private val PrimaryBlue = Color(0xFF0EA5E9)     // Xanh dương neon chính
private val AccentBlue  = Color(0xFF0284C7)     // Xanh đậm highlight
private val SurfaceStart = Color(0xFFF0F9FF)
private val SurfaceEnd  = Color(0xFFE0F2FE)
private val TextSecondary = Color(0xFF6B7280)
@Composable
fun MainWorkoutScreen(navController: NavHostController,
                      onCameraClick: () -> Unit) {
    var selectedPart by remember { mutableStateOf(BodyPart.BUNG) }
    val filteredList = allWorkoutPlans.filter { it.part == selectedPart }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceStart, SurfaceEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Đẩy 2 bên ra xa
            ) {
                // 1. Tiêu đề
                Text(
                    text = "Cơ thể tập trung",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryBlue,
                    letterSpacing = 0.5.sp
                )

                // 2. Nút Camera
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f)) // Nền xanh nhạt
                        .clickable { onCameraClick() } // Gọi hàm mở camera
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam, // <--- Dùng icon có sẵn ở đây
                        contentDescription = "AI Camera",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Tabs hiện đại
            BodyPartTabs(selectedPart = selectedPart, onSelect = { selectedPart = it })

            Spacer(modifier = Modifier.height(24.dp))

            // Danh sách card
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredList) { plan ->
                    WorkoutPlanCard(
                        plan = plan,
                        onClick = {
                            navController.navigate("plan_detail/${plan.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BodyPartTabs(selectedPart: BodyPart, onSelect: (BodyPart) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(BodyPart.values()) { part ->
            val isSelected = part == selectedPart
            val bgColor by animateColorAsState(
                if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.9f)
            )
            val textColor by animateColorAsState(
                if (isSelected) Color.White else Color.DarkGray
            )
            val scale by animateFloatAsState(if (isSelected) 1.08f else 1f)

            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(28.dp))
                    .background(bgColor)
                    .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(28.dp))
                    .clickable { onSelect(part) }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = part.title,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun WorkoutPlanCard(plan: WorkoutPlan, onClick: () -> Unit) {
    val difficultyText = when (plan.difficulty) {
        1 -> "Dễ"
        2 -> "Trung bình"
        else -> "Nâng cao"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh bìa với gradient overlay xanh
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = plan.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PrimaryBlue.copy(alpha = 0.4f)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$difficultyText • ${plan.duration} phút • ${plan.exerciseCount} bài",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tia sét xanh dương
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { index ->
                        val isActive = index < plan.difficulty
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isActive) PrimaryBlue else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}