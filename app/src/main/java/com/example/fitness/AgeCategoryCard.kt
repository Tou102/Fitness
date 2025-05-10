package com.example.fitness.ui.components // Đảm bảo package name phù hợp với vị trí file của bạn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgeCategoryCard(
    title: String,
    imageResourceId: Int, // ID của hình ảnh trong thư mục res/drawable
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp) // Điều chỉnh chiều rộng theo ý muốn
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF303030)) // Màu nền tương tự
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hình ảnh
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Điều chỉnh chiều cao hình ảnh
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop // Để hình ảnh vừa vặn
            )

            // Phần chữ và icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Xem thêm",
                    tint = Color.White
                )
            }
        }
    }
}