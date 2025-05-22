package com.example.fitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

enum class DialogMode {
    NONE, ADD, EDIT
}

@Composable
fun NutritionDetailScreen(
    navController: NavHostController,
    title: String,
    content: String,
    additionalContent: @Composable () -> Unit,
    onAdd: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    var isAddEditDialogOpen by remember { mutableStateOf(false) }
    var isCancelConfirmDialogOpen by remember { mutableStateOf(false) }
    var dialogMode by remember { mutableStateOf(DialogMode.NONE) }
    var inputTitle by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        additionalContent()
                    }
                }
            }

            Column {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Quay về",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            dialogMode = DialogMode.ADD
                            inputTitle = ""
                            isAddEditDialogOpen = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Thêm")
                    }

                    Button(
                        onClick = {
                            dialogMode = DialogMode.EDIT
                            inputTitle = title
                            isAddEditDialogOpen = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Sửa")
                    }

                    Button(
                        onClick = {
                            isCancelConfirmDialogOpen = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa")
                    }
                }
            }
        }
    }

    if (isAddEditDialogOpen) {
        AddEditInputDialog(
            title = if (dialogMode == DialogMode.ADD) "Thêm chế độ" else "Sửa chế độ",
            inputText = inputTitle,
            onInputChange = { inputTitle = it },
            onConfirm = {
                if (dialogMode == DialogMode.ADD) {
                    onAdd(inputTitle.ifBlank { "Chế độ mới" })
                } else if (dialogMode == DialogMode.EDIT) {
                    onEdit(inputTitle)
                }
                isAddEditDialogOpen = false
            },
            onCancelRequest = {
                isCancelConfirmDialogOpen = true
            }
        )
    }

    if (isCancelConfirmDialogOpen) {
        CancelConfirmDialog(
            onConfirmCancel = {
                isCancelConfirmDialogOpen = false
                isAddEditDialogOpen = false
            },
            onDismissCancel = {
                isCancelConfirmDialogOpen = false
            }
        )
    }
}

@Composable
fun AddEditInputDialog(
    title: String,
    inputText: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancelRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelRequest,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                label = { Text("Tên chế độ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelRequest) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun CancelConfirmDialog(
    onConfirmCancel: () -> Unit,
    onDismissCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissCancel,
        title = { Text("Xác nhận") },
        text = { Text("Bạn có chắc muốn hủy? Mọi thay đổi chưa lưu sẽ bị mất.") },
        confirmButton = {
            TextButton(onClick = onConfirmCancel) {
                Text("Đồng ý")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissCancel) {
                Text("Tiếp tục chỉnh sửa")
            }
        }
    )
}

// Ví dụ các màn hình chi tiết giữ nguyên nội dung bạn có

@Composable
fun AnLongChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Lợi ích uống đủ nước",
                description = "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa.",
                imageUri = null // hoặc Uri nếu có
            ),
            NutritionCardItem(
                title = "Ví dụ món ăn",
                description = "Súp, nước hầm xương, nước trái cây loãng.",
                imageUri = null
            ),
            NutritionCardItem( // Nội dung mới được thêm vào
                title = "Lời khuyên dinh dưỡng",
                description = "Nên uống ít nhất 2 lít nước mỗi ngày và ăn nhiều rau củ quả để duy trì sức khỏe.",
                imageUri = null // hoặc Uri nếu có
            )
        )
    )
}


@Composable
fun AnKiengChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Thực phẩm nên tránh",
                description = "Hạn chế bánh kẹo, nước ngọt có ga, đồ ăn nhanh, bánh mì trắng, cơm trắng nhiều, các món nhiều dầu mỡ hoặc gia vị mạnh.",
                imageUri = null // Hoặc Uri nếu có
            ),
            NutritionCardItem(
                title = "Thực phẩm ưu tiên",
                description = "Ăn nhiều rau xanh, đậu, ngũ cốc nguyên hạt và trái cây vừa phải. Protein nạc như cá, thịt gà không da, đậu phụ.",
                imageUri = null
            ),
            NutritionCardItem(
                title = "Lời khuyên chuyên gia",
                description = "Kiểm tra đường huyết định kỳ, tập luyện đều đặn để cải thiện insulin và sức khỏe tim mạch. Tuân thủ hướng dẫn bác sĩ.",
                imageUri = null
            )
        )
    )
}

@Composable
fun CaloChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Thực phẩm giàu năng lượng",
                description = "Hạt như hạnh nhân, óc chó, bơ, dầu ô liu ...",
                imageUri = null // hoặc Uri nếu có
            ),
            NutritionCardItem(
                title = "Món ăn gợi ý",
                description = "Sinh tố bơ, cơm gạo lứt ...",
                imageUri = null
            ),
            NutritionCardItem(
                title = "Lời khuyên",
                description = "Chia nhỏ bữa ăn ...",
                imageUri = null
            )
        )
    )
}


@Composable
fun NatriChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Thực phẩm nên tránh",
                description = "Hạn chế muối, đồ hộp, mì ăn liền và thức ăn nhanh chứa nhiều natri gây hại tim mạch.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Thực phẩm ưu tiên",
                description = "Ăn nhiều rau xanh, trái cây và đậu giúp cân bằng natri. Dùng thảo mộc thay muối để tăng vị.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Lời khuyên chuyên gia",
                description = "Ăn ít natri, uống đủ nước và theo dõi huyết áp thường xuyên giúp bảo vệ tim mạch và sức khỏe.",
                imageUri = null // Không có ảnh
            )
        )
    )
}

@Composable
fun ProteinThapChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Chế độ ăn ít protein",
                description = "Phù hợp với người suy thận hoặc gan, giúp giảm gánh nặng lọc thải, ngăn ngừa tổn thương thêm.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Chế độ ăn giàu protein",
                description = "Cần cho vận động viên, người tăng cơ hoặc phục hồi. Protein giúp xây dựng cơ và nâng cao sức khỏe.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Lời khuyên",
                description = "Tham khảo bác sĩ hoặc chuyên gia dinh dưỡng để cân bằng protein phù hợp với sức khỏe và nhu cầu.",
                imageUri = null // Không có ảnh
            )
        )
    )
}


@Composable
fun CholesterolChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Các loại cholesterol",
                description = "• LDL (xấu): Gây tích tụ mảng bám, làm tắc nghẽn mạch máu.\n" +
                        "• HDL (tốt): Giúp loại bỏ cholesterol xấu khỏi cơ thể.\n" +
                        "• Triglycerides: Chất béo máu, tăng cao gây hại tim.",
                imageUri = null // Không cần ảnh, hoặc sử dụng Uri nếu có
            ),
            NutritionCardItem(
                title = "Nguyên nhân tăng cholesterol",
                description = "• Ăn nhiều chất béo bão hòa, trans fat.\n" +
                        "• Ít vận động, thừa cân.\n" +
                        "• Yếu tố di truyền, bệnh lý.",
                imageUri = null // Không cần ảnh
            ),
            NutritionCardItem(
                title = "Cách kiểm soát",
                description = "• Ăn nhiều rau, trái cây, chất xơ.\n" +
                        "• Hạn chế chất béo bão hòa, chọn dầu thực vật.\n" +
                        "• Tập thể dục đều đặn.\n" +
                        "• Kiểm soát cân nặng, khám sức khỏe định kỳ.",
                imageUri = null // Không cần ảnh
            )
        )
    )
}


@Composable
fun AnChayChiTiet(navController: NavHostController) {
    NutritionCardEditor(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        initialItems = listOf(
            NutritionCardItem(
                title = "Các loại ăn chay",
                description = "• Vegan: Không dùng sản phẩm động vật.\n" +
                        "• Lacto-ovo: Có trứng và sữa.\n" +
                        "• Bán phần: Thỉnh thoảng ăn cá, thịt.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Lợi ích sức khỏe",
                description = "• Giảm nguy cơ bệnh tim.\n" +
                        "• Hỗ trợ kiểm soát cân nặng.\n" +
                        "• Nhiều chất chống oxy hóa, vitamin.\n" +
                        "• Cải thiện tiêu hóa nhờ chất xơ.",
                imageUri = null // Không có ảnh
            ),
            NutritionCardItem(
                title = "Nguồn protein chay",
                description = "• Đậu, đỗ, hạt, ngũ cốc nguyên hạt.\n" +
                        "• Đậu nành, đậu hũ, sữa đậu nành.\n" +
                        "• Bổ sung vitamin B12, sắt, omega-3.",
                imageUri = null // Không có ảnh
            )
        )
    )
}

