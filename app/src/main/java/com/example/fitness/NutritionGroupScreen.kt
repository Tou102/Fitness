package com.example.fitness

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.fitness.viewModel.NutritionDetailViewModel

@Composable
fun AnLongChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn lỏng",
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
fun AnKiengChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn kiêng",
        title = "Chế độ ăn kiêng",
        description = "Hạn chế bánh kẹo, nước ngọt có ga, đồ ăn nhanh, bánh mì trắng, cơm trắng nhiều, các món nhiều dầu mỡ hoặc gia vị mạnh.",
        benefits = listOf(),
        examples = listOf(
            "Ăn nhiều rau xanh, đậu, ngũ cốc nguyên hạt và trái cây vừa phải. Protein nạc như cá, thịt gà không da, đậu phụ."
        ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe.",
            "Kiểm tra đường huyết định kỳ, tập luyện đều đặn để cải thiện insulin và sức khỏe tim mạch. Tuân thủ hướng dẫn bác sĩ."
        )
    )
}

@Composable
fun CaloChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Thực phẩm giàu năng lượng",
        title = "Thực phẩm giàu năng lượng",
        description = "Hạt như hạnh nhân, óc chó, bơ, dầu ô liu ...",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",
            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(
            "Sinh tố bơ, cơm gạo lứt ..."
        ),
        nutritionTips = listOf(
            "Nên uống ít nhất 2 lít nước mỗi ngày.",
            "Ăn nhiều rau củ quả để duy trì sức khỏe.",
            "Chia nhỏ bữa ăn ..."
        )
    )
}

@Composable
fun NatriChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn Natri",
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
            "Ăn nhiều rau củ quả để duy trì sức khỏe."
        )
    )
}

@Composable
fun ProteinThapChiTiet(
    navController: NavHostController,
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn ít protein",
        title = "Chế độ ăn ít protein",
        description = "Phù hợp với người suy thận hoặc gan, giúp giảm gánh nặng lọc thải, ngăn ngừa tổn thương thêm.",
        benefits = listOf(
            "Giúp cơ thể hoạt động tốt, tăng sức khỏe và hỗ trợ tiêu hóa",
            "Dễ tiêu hóa và giúp cơ thể duy trì năng lượng"
        ),
        examples = listOf(
            "Thức ăn ít protein",
            "Nước trái cây"
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
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn cholesterol",
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
    nutritionDetailViewModel: NutritionDetailViewModel,
    isAdmin: Boolean
) {
    NutritionGroupScreen(
        navController = navController,
        nutritionDetailViewModel = nutritionDetailViewModel,
        isAdmin = isAdmin,
        groupName = "Chế độ ăn chay",
        title = "Chế độ ăn chay",
        description = "• Vegan: Không dùng sản phẩm động vật.\n" +
                "• Lacto-ovo: Có trứng và sữa.\n" +
                "• Bán phần: Thỉnh thoảng ăn cá, thịt.",
        benefits = listOf(
            "• Giảm nguy cơ bệnh tim.\n" +
                    "• Hỗ trợ kiểm soát cân nặng.\n" +
                    "• Nhiều chất chống oxy hóa, vitamin.\n" +
                    "• Cải thiện tiêu hóa nhờ chất xơ."
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
