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
import java.io.File

@Composable
fun NutritionGroupScreen(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean,
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

        InfoCard("Lợi ích:", benefits)
        InfoCard("Ví dụ món ăn:", examples)
        InfoCard("Lời khuyên dinh dưỡng:", nutritionTips)

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
                val updated = nutritionDetail.copy(groupName = groupName)
                if (nutritionDetail.id == 0) {
                    nutritionDetailViewModel.addNutritionDetail(updated)
                } else {
                    nutritionDetailViewModel.updateNutritionDetail(updated)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun InfoCard(title: String, items: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Text(text = "- $item", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun NutritionItem(
    nutritionDetail: NutritionDetail,
    isAdmin: Boolean,
    onEdit: (NutritionDetail) -> Unit,
    onDelete: (NutritionDetail) -> Unit
) {
    val imageModel = when {
        nutritionDetail.imageUri    !!.startsWith("http") -> nutritionDetail.imageUri // Ảnh online
        nutritionDetail.imageUri!!.startsWith("content://") -> nutritionDetail.imageUri // URI content từ máy
        else -> File(nutritionDetail.imageUri) // File nội bộ
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (nutritionDetail.imageUri.isNotEmpty()) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = nutritionDetail.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
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
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            imageUri = file.absolutePath
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
                        model = File(imageUri),
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
