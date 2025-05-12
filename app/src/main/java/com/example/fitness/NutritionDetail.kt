import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitness.R

@Composable
fun NutritionDetailScreen(navController: NavHostController, title: String, content: String, additionalContent: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column( // Column chứa tiêu đề, nội dung chính và additionalContent
            modifier = Modifier
                .weight(1f) // Cho phép phần này chiếm phần lớn không gian và cuộn
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = " $title",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Slot cho nội dung bổ sung (các thẻ và hình ảnh)
            additionalContent()
        }

        Column { // Column chứa các nút ở dưới cùng
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
        content = "Bạn nên uống ít nhất 2 lít nước mỗi ngày để duy trì sức khỏe và giúp cơ thể hoạt động hiệu quả.",
        additionalContent = { // Thêm các thẻ và hình ảnh ở đây
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Lợi ích của việc uống đủ nước",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Giúp duy trì chức năng cơ thể, tăng cường năng lượng, hỗ trợ tiêu hóa...")
                }
                Image(
                    painter = painterResource(id = R.drawable.sup), // Thay "water_glass" bằng tên file hình ảnh của bạn trong drawable
                    contentDescription = "Hình ảnh súp",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Các loại đồ uống lỏng khác",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ngoài nước lọc, bạn có thể uống nước ép trái cây, sinh tố, súp...")
                }

                Image(
                    painter = painterResource(id = R.drawable.canh), // Thay "water_glass" bằng tên file hình ảnh của bạn trong drawable
                    contentDescription = "Hình ảnh canh",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thêm một lựa chọn khác",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ví dụ như trà hoặc các loại nước thảo dược...")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.nuocep), // Thay "water_glass" bằng tên file hình ảnh của bạn trong drawable
                    contentDescription = "Hình ảnh nước ép",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Bạn có thể thêm nhiều thẻ và hình ảnh khác ở đây
        }
    )
}

@Composable
fun AnKiengChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Ăn kiêng cho người tiểu đường",
        content = "Chế độ ăn kiêng đặc biệt dành cho người tiểu đường cần kiểm soát lượng đường trong máu.",
        additionalContent = { /* Thêm thẻ và hình ảnh cho ăn kiêng ở đây */ }
    )
}

@Composable
fun CaloChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ dinh dưỡng giàu calo",
        content = "Chế độ ăn này cung cấp nhiều năng lượng, phù hợp cho người muốn tăng cân hoặc vận động nhiều.",
        additionalContent = { /* Thêm thẻ và hình ảnh cho giàu calo ở đây */ }
    )
}

@Composable
fun CholesterolChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn ít cholesterol",
        content = "Chế độ ăn này giúp giảm lượng cholesterol trong máu, tốt cho tim mạch.",
        additionalContent = { /* Thêm thẻ và hình ảnh cho ít cholesterol ở đây */ }
    )
}

@Composable
fun AnChayChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn chay",
        content = "Chế độ ăn không bao gồm thịt và các sản phẩm từ động vật.",
        additionalContent = { // Thêm thẻ và hình ảnh cho ăn chay ở đây
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thực phẩm thường dùng trong chế độ ăn chay",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rau, củ, quả, các loại đậu, ngũ cốc, nấm...")
                }
            }
            // Thêm thêm thẻ và hình ảnh nếu cần
        }
    )
}

@Composable
fun NatriChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ ăn ít natri",
        content = "Chế độ ăn này giúp kiểm soát huyết áp, phù hợp cho người bị cao huyết áp.",
        additionalContent = { /* Thêm thẻ và hình ảnh cho ít natri ở đây */ }
    )
}


@Composable
fun ProteinThapChiTiet(navController: NavHostController) {
    NutritionDetailScreen(
        navController = navController,
        title = "Chế độ dinh dưỡng ít và giàu protein",
        content = "Chế độ ăn này có thể được chỉ định cho một số tình trạng sức khỏe đặc biệt.",
        additionalContent = { /* Thêm thẻ và hình ảnh cho protein ở đây */ }
    )
}