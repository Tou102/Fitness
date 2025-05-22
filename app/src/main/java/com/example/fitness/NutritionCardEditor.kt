package com.example.fitness

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

data class NutritionCardItem(
    var title: String,
    var description: String,
    var imageUri: Uri? = null
)

@Composable
fun NutritionCardEditor(
    modifier: Modifier = Modifier,
    initialItems: List<NutritionCardItem> = emptyList()
) {
    val cardItems = remember { mutableStateListOf<NutritionCardItem>().apply { addAll(initialItems) } }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editImageUri by remember { mutableStateOf<Uri?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            editImageUri = uri
        }
    }

    fun openEditDialog(index: Int) {
        editingIndex = index
        val item = cardItems[index]
        editTitle = item.title
        editDescription = item.description
        editImageUri = item.imageUri
        isDialogOpen = true
    }

    fun openAddDialog() {
        editingIndex = null
        editTitle = ""
        editDescription = ""
        editImageUri = null
        isDialogOpen = true
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        cardItems.forEachIndexed { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Row {
                            IconButton(onClick = { openEditDialog(index) }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa")
                            }
                            IconButton(onClick = { cardItems.removeAt(index) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.description)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (item.imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(item.imageUri),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { openAddDialog() }, modifier = Modifier.fillMaxWidth()) {
            Text("Thêm thẻ mới")
        }
    }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text(if (editingIndex == null) "Thêm thẻ" else "Sửa thẻ") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Tiêu đề") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Mô tả") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
                        Text("Chọn ảnh")
                    }
                    editImageUri?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Ảnh chọn",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editingIndex == null) {
                        cardItems.add(NutritionCardItem(editTitle, editDescription, editImageUri))
                    } else {
                        editingIndex?.let {
                            cardItems[it] = NutritionCardItem(editTitle, editDescription, editImageUri)
                        }
                    }
                    isDialogOpen = false
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
