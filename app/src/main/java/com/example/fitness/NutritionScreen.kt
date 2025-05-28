package com.example.fitness

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun NutritionScreen(navController: NavHostController) {
    // Trạng thái để kích hoạt hoạt ảnh khi màn hình xuất hiện
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100) // Trì hoãn nhẹ để tạo hiệu ứng mượt mà
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Xanh dương đậm
                        Color(0xFF42A5F5) // Xanh dương nhạt hơn ở dưới
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tiêu đề với hiệu ứng
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut(animationSpec = tween(800))
            ) {
                Text(
                    text = "Chế Độ Dinh Dưỡng",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(nutritionItems) { item ->
                    NutritionCard(item = item, navController = navController, isVisible = isVisible)
                }
            }
        }
    }
}

@Composable
fun NutritionCard(item: NutritionItem, navController: NavHostController, isVisible: Boolean) {
    // Trạng thái để tạo hiệu ứng phóng to khi nhấn
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .scale(scale)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    onClick = {
                        isPressed = true
                        when (item.title) {
                            "1. Chế độ ăn lỏng" -> navController.navigate("anlong_detail")
                            "2. Chế độ ăn kiêng cho người tiểu đường" -> navController.navigate("ankieng_detail")
                            "3. Chế độ dinh dưỡng giàu calo" -> navController.navigate("calo_detail")
                            "4. Chế độ ăn ít cholesterol" -> navController.navigate("choles_detail")
                            "5. Chế độ ăn chay" -> navController.navigate("anchay_detail")
                            "6. Chế độ ăn ít natri" -> navController.navigate("natri_detail")
                            "7. Chế độ dinh dưỡng ít và giàu protein" -> navController.navigate("protein_detail")
                        }
                    },
                    onClickLabel = "Select ${item.title}"
                )
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                // Hình nền bài tập
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Lớp phủ gradient để làm nổi bật tiêu đề
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Tiêu đề bài tập
                Text(
                    text = item.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )

                // Icon nhỏ ở góc phải trên

            }
        }
    }
}

data class NutritionItem(val title: String, val imageResId: Int)

val nutritionItems = listOf(
    NutritionItem("1. Chế độ ăn lỏng", R.drawable.anlong2),
    NutritionItem("2. Chế độ ăn kiêng cho người tiểu đường", R.drawable.ankieng),
    NutritionItem("3. Chế độ dinh dưỡng giàu calo", R.drawable.giaucalo),
    NutritionItem("4. Chế độ ăn ít cholesterol", R.drawable.cholesterol),
    NutritionItem("5. Chế độ ăn chay", R.drawable.anchay),
    NutritionItem("6. Chế độ ăn ít natri", R.drawable.natri),
    NutritionItem("7. Chế độ dinh dưỡng ít và giàu protein", R.drawable.protein)
)