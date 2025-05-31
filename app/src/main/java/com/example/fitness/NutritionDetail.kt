package com.example.fitness

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fitness.entity.NutritionDetail
import com.example.fitness.viewModel.NutritionDetailViewModel

@Composable
fun NutritionGroupScreen(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean,  // phân quyền: true = admin, false = user bình thường
    groupName: String,
    title: String,
    description: String,
    benefits: List<String>,
    examples: List<String>,
    nutritionTips: List<String>
) {
    val nutritionDetails by nutritionDetailViewModel.nutritionDetails.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingNutritionDetail by remember { mutableStateOf<NutritionDetail?>(null) }

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

        Spacer(modifier = Modifier.height(16.dp))

        filteredNutritionDetails.forEach { nutritionDetail ->
            NutritionItem(
                nutritionDetail = nutritionDetail,
                isAdmin = isAdmin,
                onEdit = {
                    editingNutritionDetail = it
                    showDialog = true
                },
                onDelete = {
                    nutritionDetailViewModel.deleteNutritionDetail(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chỉ hiển thị nút Thêm với admin
        if (isAdmin) {
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
    }

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
    isAdmin: Boolean,
    onEdit: (NutritionDetail) -> Unit,
    onDelete: (NutritionDetail) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // tăng chiều cao để đủ chỗ hình
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!nutritionDetail.imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = nutritionDetail.imageUri,
                    contentDescription = nutritionDetail.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                // Nếu không có ảnh thì có thể dùng background màu xám nhạt hoặc màu tùy ý
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray))
            }

            // Lớp phủ gradient để làm nổi bật tiêu đề
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            Text(
                text = nutritionDetail.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )

            if (isAdmin) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
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
            imageUri = uri.toString()
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
                    imageUri = imageUri,
                    groupName = group
                ) ?: NutritionDetail(
                    id = 0,
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
