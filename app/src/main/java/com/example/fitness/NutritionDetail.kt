package com.example.fitness

import android.content.Context

import java.io.File

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fitness.entity.NutritionDetail
import com.example.fitness.viewModel.NutritionDetailViewModel



@Composable
fun NutritionDetailScreen(
    navController: NavHostController,
    nutritionType: String?,
    content: String,
    additionalContent: @Composable () -> Unit,
    onAdd: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)


    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dinh dưỡng: $nutritionType",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            additionalContent()
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
                    onClick = onAdd,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Thêm")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa")
                }
            }
        }
    }
}

@Composable
fun NutritionGroupScreen(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    groupName: String,
    title: String,
    description: String,
    benefits: List<String>,
    examples: List<String>,
    nutritionTips: List<String>
) {
    val nutritionDetails by nutritionDetailViewModel.nutritionDetails.collectAsState()

    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var editingNutritionDetail by remember { mutableStateOf<NutritionDetail?>(null) }

    // Lọc các mục dinh dưỡng theo nhóm
    val filteredNutritionDetails = nutritionDetails.filter { it.groupName == groupName }
    LaunchedEffect(nutritionDetails) {
        nutritionDetailViewModel.loadNutritionDetails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị lợi ích của nhóm dinh dưỡng
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Lợi ích:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                benefits.forEach { benefit ->
                    Text(text = "- $benefit", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Hiển thị các ví dụ
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Ví dụ món ăn:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                examples.forEach { example ->
                    Text(text = "- $example", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Hiển thị các lời khuyên dinh dưỡng
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Lời khuyên dinh dưỡng:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                nutritionTips.forEach { tip ->
                    Text(text = "- $tip", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Hiển thị các mục dinh dưỡng thuộc nhóm
        filteredNutritionDetails.forEach { nutritionDetail ->
            NutritionItem(
                nutritionDetail = nutritionDetail,
                onEdit = {
                    editingNutritionDetail = it
                    showDialog = true
                },
                onDelete = { nutritionDetailViewModel.deleteNutritionDetail(nutritionDetail) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                editingNutritionDetail = null
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm mục dinh dưỡng")
        }
    }

    // Hiển thị Dialog khi thêm hoặc chỉnh sửa
    if (showDialog) {
        NutritionDetailDialog(
            nutritionDetail = editingNutritionDetail,
            group = groupName,
            onDismiss = { showDialog = false },
            onSave = { nutritionDetail ->
                val updatedNutritionDetail = nutritionDetail.copy(groupName = groupName)
                if (nutritionDetail.id == 0) {
                    nutritionDetailViewModel.addNutritionDetail(updatedNutritionDetail)
                } else {
                    nutritionDetailViewModel.updateNutritionDetail(updatedNutritionDetail)
                }
                showDialog = false
            }
        )
    }
}




@Composable
fun NutritionItem(
    nutritionDetail: NutritionDetail,
    onEdit: (NutritionDetail) -> Unit,
    onDelete: (NutritionDetail) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()

            .padding(vertical = 8.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = nutritionDetail.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nutritionDetail.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            if (!nutritionDetail.imageUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = nutritionDetail.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onEdit(nutritionDetail) }) {
                    Text("Sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(nutritionDetail) }) {
                    Text("Xóa")
                }
            }
        }
    }
}




fun copyUriToFile(context: Context, uri: Uri, destFile: File): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            destFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun NutritionDetailDialog(
    nutritionDetail: NutritionDetail?,
    group: String,
    onDismiss: () -> Unit,
    onSave: (NutritionDetail) -> Unit
) {
    var title by remember { mutableStateOf(nutritionDetail?.title ?: "") }
    var description by remember { mutableStateOf(nutritionDetail?.description ?: "") }
    var imageUri by remember { mutableStateOf(nutritionDetail?.imageUri ?: "") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Tạo file đích trong bộ nhớ app
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val destFile = File(context.filesDir, fileName)

            val copied = copyUriToFile(context, uri, destFile)
            if (copied) {
                imageUri = destFile.absolutePath
            } else {
                // Nếu copy thất bại thì cấp quyền và lưu URI gốc
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                imageUri = uri.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (nutritionDetail == null) "Thêm mới" else "Chỉnh sửa") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên mục dinh dưỡng") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Chọn hình ảnh")
                }
                if (imageUri.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Hình ảnh đã chọn",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newNutritionDetail = nutritionDetail?.copy(
                    title = title,
                    description = description,
                    imageUri = imageUri
                ) ?: NutritionDetail(
                    title = title,
                    description = description,
                    imageUri = imageUri,
                    groupName = group
                )
                onSave(newNutritionDetail)
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}







@Composable
fun AnLongChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn lỏng",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Chế độ ăn lỏng",
        description = "Chế độ ăn lỏng giúp cung cấp đủ nước cho cơ thể và dễ tiêu hóa.",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",
            "Cung cấp đủ nước cho cơ thể",
            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(
            "Súp",
            "Nước hầm xương",
            "Nước trái cây loãng"
        ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe."

        )

    )

}




@Composable
fun AnKiengChiTiet(navController: NavHostController,nutritionDetailViewModel: NutritionDetailViewModel) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn kiêng",
        title = "Chế độ ăn kiêng",
        description = "Hạn chế bánh kẹo, nước ngọt có ga, đồ ăn nhanh, bánh mì trắng, cơm trắng nhiều, các món nhiều dầu mỡ hoặc gia vị mạnh.",
        benefits = listOf(

        ),
        examples = listOf(
            "Ăn nhiều rau xanh, đậu, ngũ cốc nguyên hạt và trái cây vừa phải. Protein nạc như cá, thịt gà không da, đậu phụ.",

            ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe.",
            "Kiểm tra đường huyết định kỳ, tập luyện đều đặn để cải thiện insulin và sức khỏe tim mạch. Tuân thủ hướng dẫn bác sĩ."


        )

    )

}

@Composable
fun CaloChiTiet(navController: NavHostController,nutritionDetailViewModel: NutritionDetailViewModel) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Thực phẩm giàu năng lượng",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Thực phẩm giàu năng lượng",
        description = "Hạt như hạnh nhân, óc chó, bơ, dầu ô liu ...",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",

            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(

            "Sinh tố bơ, cơm gạo lứt ...",
        ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe.",
            "Chia nhỏ bữa ăn ..."

        )

    )

}


@Composable
fun NatriChiTiet(navController: NavHostController,nutritionDetailViewModel: NutritionDetailViewModel) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn Natri",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Chế độ ăn Natri",
        description = "Hạn chế muối, đồ hộp, mì ăn liền và thức ăn nhanh chứa nhiều natri gây hại tim mạch.",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",
            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(
            "Ăn nhiều rau xanh, trái cây và đậu giúp cân bằng natri. Dùng thảo mộc thay muối để tăng vị.",
            "Ăn ít natri, uống đủ nước và theo dõi huyết áp thường xuyên giúp bảo vệ tim mạch và sức khỏe."
        ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe.",

            )

    )

}

@Composable
fun ProteinThapChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn ít protein",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Chế độ ăn ít protein",
        description = "Phù hợp với người suy thận hoặc gan, giúp giảm gánh nặng lọc thải, ngăn ngừa tổn thương thêm.",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",

            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(

            "Thức ăn ít protein",
            "Nước trái cây "
        ),
        nutritionTips = listOf(
            "Tham khảo bác sĩ hoặc chuyên gia dinh dưỡng để cân bằng protein phù hợp với sức khỏe và nhu cầu.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe."

        )

    )

}


@Composable
fun CholesterolChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn cholesterol",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Chế độ ăn cholesterol",
        description = "• LDL (xấu): Gây tích tụ mảng bám, làm tắc nghẽn mạch máu.\n" +
                "• HDL (tốt): Giúp loại bỏ cholesterol xấu khỏi cơ thể.\n" +
                "• Triglycerides: Chất béo máu, tăng cao gây hại tim.",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",

            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(
            "Ăn nhiều rau, trái cây, chất xơ."
        ),
        nutritionTips = listOf(
            "• Ăn nhiều rau, trái cây, chất xơ.\n" +
                    "• Hạn chế chất béo bão hòa, chọn dầu thực vật.\n" +
                    "• Tập thể dục đều đặn.\n" +
                    "• Kiểm soát cân nặng, khám sức khỏe định kỳ."

        )

    )

}

@Composable
fun AnChayChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        groupName = "Chế độ ăn chay",  // Chỉ định nhóm dinh dưỡng là "Chế độ ăn lỏng"
        title = "Chế độ ăn chay",
        description = "• Vegan: Không dùng sản phẩm động vật.\n" +
                "• Lacto-ovo: Có trứng và sữa.\n" +
                "• Bán phần: Thỉnh thoảng ăn cá, thịt.",
        benefits = listOf(
            "• Giảm nguy cơ bệnh tim.\n" +
                    "• Hỗ trợ kiểm soát cân nặng.\n" +
                    "• Nhiều chất chống oxy hóa, vitamin.\n" +
                    "• Cải thiện tiêu hóa nhờ chất xơ.",
        ),
        examples = listOf(
            "Ăn rau rủ quả"
        ),
        nutritionTips = listOf(
            "• Đậu, đỗ, hạt, ngũ cốc nguyên hạt.\n" +
                    "• Đậu nành, đậu hũ, sữa đậu nành.\n" +
                    "• Bổ sung vitamin B12, sắt, omega-3."

        )

    )

}






