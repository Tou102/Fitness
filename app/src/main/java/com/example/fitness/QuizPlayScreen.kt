package com.example.fitness

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

// 4 màu đáp án
private val answerColors = listOf(
    Color(0xFFE53935), // Đỏ
    Color(0xFF1E88E5), // Xanh dương
    Color(0xFFFBC02D), // Vàng
    Color(0xFF43A047)  // Xanh lá
)

@Composable
fun QuizPlayScreen(
    navController: NavHostController,
    workout: WorkoutType,
    level: Int
) {
    // lấy câu hỏi theo bài tập + level
    val questions = remember(workout, level) {
        QuizQuestionRepository.getQuestions(workout, level)
    }

    // ImageLoader riêng để GIF chạy được
    val context = LocalContext.current
    val gifImageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    // nếu không có câu nào thì out về
    if (questions.isEmpty()) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(20) }

    val question = questions[currentIndex]

    // ========== TIMER ==========
    LaunchedEffect(currentIndex) {
        isAnswered = false
        selectedAnswer = null
        timeLeft = 20

        while (timeLeft > 0 && !isAnswered && !showSummary) {
            delay(1000)
            timeLeft--
        }

        // hết giờ mà chưa trả lời -> coi như sai, không cộng điểm
        if (!isAnswered && !showSummary) {
            isAnswered = true
            selectedAnswer = null
        }
    }

    // ========== SUMMARY ==========
    if (showSummary) {
        SummaryScreen(
            score = score,
            totalQuestions = questions.size,
            onBackToQuizHome = { navController.popBackStack() },
            onRetry = {
                showSummary = false
                currentIndex = 0
                selectedAnswer = null
                isAnswered = false
                score = 0
                timeLeft = 20
            }
        )
        return
    }

    // ========== MAIN UI ==========
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF000066), Color(0xFF0099FF))
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Câu ${currentIndex + 1}/${questions.size}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${timeLeft}s",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        // ===== MEDIA + CÂU HỎI Ở GIỮA =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // GIF hoặc ảnh (ưu tiên GIF nếu có)
            if (question.gifRes != null || question.imageRes != null) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)   // khung cố định – ảnh / gif nào cũng ổn
                ) {
                    when {
                        question.gifRes != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(question.gifRes!!)
                                    .build(),
                                imageLoader = gifImageLoader,
                                contentDescription = "GIF minh hoạ",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        question.imageRes != null -> {
                            Image(
                                painter = painterResource(id = question.imageRes!!),
                                contentDescription = "Ảnh minh hoạ",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            Text(
                text = question.question,
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))

        // ===== 4 ĐÁP ÁN 2x2 Ở GẦN DƯỚI =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (row in 0..1) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col

                        AnswerBox(
                            text = question.options[index],
                            color = answerColors[index],
                            isSelected = selectedAnswer == index,
                            isCorrect = question.correctIndex == index,
                            isAnswered = isAnswered
                        ) {
                            if (!isAnswered) {
                                selectedAnswer = index
                                isAnswered = true

                                if (index == question.correctIndex) {
                                    score += 10
                                }
                            }
                        }
                    }
                }
            }

            // ===== NÚT CÂU TIẾP THEO + HINT =====
            if (isAnswered) {
                Spacer(Modifier.height(12.dp))

                val isCorrect = selectedAnswer == question.correctIndex
                Text(
                    text = when {
                        selectedAnswer == null ->
                            "Hết giờ rồi, câu này coi như trượt nha 😅"
                        isCorrect ->
                            "Chuẩn bài luôn 💪"
                        else ->
                            "Sai mất rồi 😭"
                    },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = question.hint,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (currentIndex < questions.lastIndex) {
                            currentIndex++
                            selectedAnswer = null
                            isAnswered = false
                            timeLeft = 20
                        } else {
                            showSummary = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (currentIndex < questions.lastIndex)
                            "Câu tiếp theo"
                        else
                            "Xem kết quả",
                        fontSize = 19.sp
                    )
                }
            }
        }
    }
}

// =============== COMPONENTS ===============

@Composable
fun RowScope.AnswerBox(
    text: String,
    color: Color,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        !isAnswered -> color                  // chưa trả lời
        isCorrect -> color.copy(alpha = 1f)   // đúng -> giữ màu, đậm nhất
        isSelected -> Color.LightGray         // chọn sai -> xám
        else -> color.copy(alpha = 0.3f)      // còn lại -> mờ
    }

    Card(
        modifier = Modifier
            .weight(1f)
            .height(110.dp)
            .clickable(enabled = !isAnswered) { onClick() },
        shape = RoundedCornerShape(14.dp),
        backgroundColor = bg,
        elevation = 6.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SummaryScreen(
    score: Int,
    totalQuestions: Int,
    onBackToQuizHome: () -> Unit,
    onRetry: () -> Unit
) {
    val maxScore = totalQuestions * 10
    val correctCount = score / 10

    val message = when {
        correctCount == totalQuestions -> "Quá ghê, không sai câu nào luôn! 💪"
        correctCount >= totalQuestions * 2 / 3 ->
            "Ổn áp, form học bài tập ngon đó. Cố thêm xíu nữa là perfect!"
        correctCount >= totalQuestions / 3 ->
            "Tạm ổn nhưng còn nhiều chỗ hổng, chịu khó xem lại giáo án nha 😏"
        else ->
            "Hơi toang… mai lên gym hỏi lại coach chứ kiểu này dễ tập sai lắm 😅"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF000066), Color(0xFF1976D2))
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KẾT QUẢ",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            backgroundColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            elevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Điểm của bạn",
                    color = Color(0xFF0D47A1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$score / $maxScore",
                    color = Color(0xFFE53935),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Đúng $correctCount / $totalQuestions câu",
                    color = Color(0xFF424242),
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBackToQuizHome,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0288D1))
            ) {
                Text("Về mini game", color = Color.White, fontSize = 17.sp)
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0288D1))
            ) {
                Text("Làm lại", color = Color.White, fontSize = 17.sp)
            }
        }
    }
}
