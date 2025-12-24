package com.example.fitness.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitness.IntroPage
import com.google.accompanist.pager.*
import com.example.fitness.R

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FitnessIntroPager(navController: NavController) {
    val pages = listOf(
        IntroPage(
            imageRes = R.drawable.lich,
            title = "Chào mừng bạn đến với Lịch tập luyện cá nhân hoá",
            description = "Khởi đầu hành trình sức khỏe của bạn bằng kế hoạch tập luyện được thiết kế riêng biệt, dựa trên thể trạng và mục tiêu cá nhân, giúp bạn đạt hiệu quả tối ưu từng ngày."
        ),
        IntroPage(
            imageRes = R.drawable.theo,
            title = "Theo dõi dinh dưỡng và tiến trình",
            description = "Dễ dàng quản lý chế độ ăn uống khoa học cùng biểu đồ theo dõi sự cải thiện sức khỏe và vóc dáng của bạn một cách trực quan và sinh động."
        ),
        IntroPage(
            imageRes = R.drawable.hotro,
            title = "Hỗ trợ mục tiêu sức khỏe",
            description = "Đồng hành cùng bạn trong quá trình giảm cân, tăng cơ hoặc giữ dáng với những lời khuyên chuyên nghiệp và động lực thiết thực."
        )
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Hàng chứa nút Skip ở góc trên phải
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            TextButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("introPager") { inclusive = true }
                    }
                }
            ) {
                Text(text = "Bỏ qua", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nội dung intro pager chính
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val p = pages[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = p.imageRes),
                    contentDescription = p.title,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = p.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = p.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        Button(
            onClick = {
                if (pagerState.currentPage == pages.size - 1) {
                    navController.navigate("home") {
                        popUpTo("introPager") { inclusive = true }
                    }
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == pages.size - 1) "Bắt đầu tập luyện" else "Tiếp theo"
            )
        }
    }
}
