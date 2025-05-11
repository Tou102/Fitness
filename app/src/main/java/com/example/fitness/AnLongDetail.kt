package com.example.fitness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun NutritionDetailScreen(navController: NavHostController, title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = "Chi tiết: $title",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
        }


        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Quay về")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* TODO: Xử lý thêm */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Thêm")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* TODO: Xử lý sửa */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sửa")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* TODO: Xử lý xóa */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa")
                }
            }
        }
    }
}

@Composable
fun AnLongChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Ăn lỏng",
        content = "Bạn nên uống ít nhất 2 lít nước mỗi ngày để duy trì sức khỏe và giúp cơ thể hoạt động hiệu quả."
    )
}

@Composable
fun AnKiengChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Ăn kiêng cho người tiểu đường",
        content = "Chế độ ăn kiêng đặc biệt dành cho người tiểu đường cần kiểm soát lượng đường trong máu."
    )
}

@Composable
fun CaloChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ dinh dưỡng giàu calo",
        content = "Chế độ ăn này cung cấp nhiều năng lượng, phù hợp cho người muốn tăng cân hoặc vận động nhiều."
    )
}

@Composable
fun CholesterolChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn ít cholesterol",
        content = "Chế độ ăn này giúp giảm lượng cholesterol trong máu, tốt cho tim mạch."
    )
}

@Composable
fun AnChayChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn chay",
        content = "Chế độ ăn không bao gồm thịt và các sản phẩm từ động vật."
    )
}

@Composable
fun NatriChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn ít natri",
        content = "Chế độ ăn này giúp kiểm soát huyết áp, phù hợp cho người bị cao huyết áp."
    )
}


@Composable
fun ProteinThapChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ dinh dưỡng ít và giàu protein",
        content = "Chế độ ăn này có thể được chỉ định cho một số tình trạng sức khỏe đặc biệt."
    )
}

