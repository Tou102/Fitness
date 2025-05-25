//package com.example.fitness
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import coil.compose.rememberAsyncImagePainter
//
//import com.example.fitness.entity.NutritionDetail
//import com.example.fitness.viewModel.NutritionDetailViewModel
//
//data class NutritionCardItem(
//    var title: String,
//    var description: String,
//    var imageUri: Uri? = null
//)
//
//@Composable
//fun NutritionCardEditor(
//    modifier: Modifier = Modifier,
//    nutritionDetailViewModel: NutritionDetailViewModel,
//    groupName: String,
//    defaultItems: List<NutritionCardItem> = emptyList()
//) {
//    val nutritionDetails by nutritionDetailViewModel.nutritionDetails.collectAsState()
//    val groupItems = nutritionDetails.filter { it.groupName == groupName }
//
//    var editingItem by remember { mutableStateOf<NutritionDetail?>(null) }
//    var isDialogOpen by remember { mutableStateOf(false) }
//
//    // Biến edit
//    var editTitle by remember { mutableStateOf("") }
//    var editDescription by remember { mutableStateOf("") }
//    var editImageUri by remember { mutableStateOf<String?>(null) } // Lưu URI dưới dạng String
//
//    // Launcher chọn ảnh từ máy
//    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        uri?.let {
//            editImageUri = it.toString()
//        }
//    }
//
//    fun openEditDialog(item: NutritionDetail) {
//        editingItem = item
//        editTitle = item.title
//        editDescription = item.description
//        editImageUri = item.imageUri
//        isDialogOpen = true
//    }
//
//    fun openAddDialog() {
//        editingItem = null
//        editTitle = ""
//        editDescription = ""
//        editImageUri = null
//        isDialogOpen = true
//    }
//
//    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
//        // Hiển thị defaultItems
//        defaultItems.forEach { item ->
//            // ... tương tự như trước ...
//        }
//
//        // Hiển thị groupItems có nút chỉnh sửa, xóa
//        groupItems.forEach { item ->
//            // ... tương tự như trước ...
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = { openAddDialog() }, modifier = Modifier.fillMaxWidth()) {
//            Text("Thêm thẻ mới")
//        }
//    }
//
//    // Dialog thêm/sửa
//    if (isDialogOpen) {
//        AlertDialog(
//            onDismissRequest = { isDialogOpen = false },
//            title = { Text(if (editingItem == null) "Thêm thẻ" else "Sửa thẻ") },
//            text = {
//                Column {
//                    OutlinedTextField(
//                        value = editTitle,
//                        onValueChange = { editTitle = it },
//                        label = { Text("Tiêu đề") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedTextField(
//                        value = editDescription,
//                        onValueChange = { editDescription = it },
//                        label = { Text("Mô tả") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Nút chọn ảnh
//                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
//                        Text("Chọn ảnh từ máy")
//                    }
//
//                    // Hiển thị ảnh nếu có chọn
//                    editImageUri?.let { uriString ->
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Image(
//                            painter = rememberAsyncImagePainter(uriString),
//                            contentDescription = "Ảnh chọn",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(150.dp),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    if (editingItem == null) {
//                        nutritionDetailViewModel.insert(
//                            NutritionDetail(
//                                id = 0L,
//                                title = editTitle,
//                                description = editDescription,
//                                imageUri = editImageUri,
//                                groupName = groupName
//                            )
//                        )
//                    } else {
//                        nutritionDetailViewModel.update(
//                            editingItem!!.copy(
//                                title = editTitle,
//                                description = editDescription,
//                                imageUri = editImageUri
//                            )
//                        )
//                    }
//                    isDialogOpen = false
//                }) {
//                    Text("Lưu")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { isDialogOpen = false }) {
//                    Text("Hủy")
//                }
//            }
//        )
//    }
//}
//
