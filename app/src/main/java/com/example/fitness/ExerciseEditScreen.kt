package com.example.fitness

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.fitness.entity.AppRepository
import com.example.fitness.entity.MeasureUnit
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

// Định nghĩa 3 Tab hiển thị
enum class TabType(val title: String) {
    ANIMATION("Hoạt hình"),
    MUSCLE("Cơ bắp"),
    INSTRUCTION("Hướng dẫn")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditScreen(exerciseId: Int, onBack: () -> Unit) {
    val context = LocalContext.current

    // 1. Lấy dữ liệu bài tập
    var currentExercise by remember { mutableStateOf(AppRepository.getExerciseById(exerciseId)) }
    if (currentExercise == null) return

    // State lưu giá trị chỉnh sửa
    var editValue by remember(currentExercise) { mutableStateOf(currentExercise!!.value) }

    // State lưu tab đang chọn
    var selectedTab by remember { mutableStateOf(TabType.INSTRUCTION) }

    // State quản lý Bottom Sheet
    var showSwapSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // --- CẤU HÌNH BỘ ĐỌC GIF (QUAN TRỌNG ĐỂ KHÔNG VĂNG) ---
    val imageLoader = remember {
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

    Scaffold(
        bottomBar = {
            // --- THANH BUTTON DƯỚI CÙNG ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // A. NÚT ĐẶT LẠI
                OutlinedButton(
                    onClick = {
                        // SỬA LỖI: Dùng exerciseId (tham số hàm) thay vì currentExercise!!.id
                        // Để đảm bảo dù đang chọn bài nào, nó vẫn tìm về đúng bài gốc ban đầu.
                        val restored = AppRepository.resetToDefault(exerciseId)

                        if (restored != null) {
                            currentExercise = restored
                            editValue = restored.value
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF2979FF))
                ) {
                    Text("ĐẶT LẠI", color = Color(0xFF2979FF), fontWeight = FontWeight.Bold)
                }

                // B. NÚT LƯU
                Button(
                    onClick = {
                        AppRepository.saveUserChanges(
                            exerciseIdInput = exerciseId,
                            newValue = editValue,
                            newActiveId = currentExercise!!.id
                        )
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                ) {
                    Text("LƯU", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. HEADER
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                Text(
                    text = currentExercise!!.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                TextButton(onClick = { showSwapSheet = true }) {
                    Icon(Icons.Default.SwapVert, null, tint = Color(0xFF2979FF))
                    Text("Thay thế", color = Color(0xFF2979FF), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. KHUNG HIỂN THỊ MEDIA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Tăng chiều cao lên chút cho thoáng
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = selectedTab, label = "TabAnimation") { tab ->
                    when (tab) {
                        TabType.INSTRUCTION -> {
                            // --- VIDEO YOUTUBE ---
                            if (currentExercise!!.videoId.isNotEmpty()) {
                                YouTubePlayerComposable(videoId = currentExercise!!.videoId)
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Không có video hướng dẫn")
                                }
                            }
                        }

                        // --- ĐÃ SỬA: DÙNG COIL ĐỂ CHẠY GIF ---
                        TabType.ANIMATION -> {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(currentExercise!!.imageRes) // Lấy GIF từ imageRes
                                        .size(Size.ORIGINAL)              // Giữ nét căng
                                        .build(),
                                    imageLoader = imageLoader             // Truyền bộ đọc vào
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit, // Fit để thấy hết động tác
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            )
                        }

                        // --- TAB CƠ BẮP: DÙNG ẢNH TĨNH ---
                        TabType.MUSCLE -> {
                            Image(
                                painter = painterResource(id = currentExercise!!.muscleImageRes),
                                contentDescription = "Cơ bắp",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. HÀNG NÚT CHUYỂN TAB
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(25.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabType.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    val bgColor = if (isSelected) Color(0xFF2979FF) else Color.Transparent
                    val textColor = if (isSelected) Color.White else Color(0xFF757575)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(25.dp))
                            .background(bgColor)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. BỘ ĐẾM +/-
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val step = if (currentExercise!!.unit == MeasureUnit.TIME) 5 else 1
                    if (editValue >= step) editValue -= step
                }) {
                    Icon(Icons.Default.RemoveCircleOutline, null, Modifier.size(44.dp), tint = Color(0xFF2979FF))
                }

                Text(
                    text = if (currentExercise!!.unit == MeasureUnit.TIME) "${editValue}s" else "x$editValue",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                IconButton(onClick = {
                    val step = if (currentExercise!!.unit == MeasureUnit.TIME) 5 else 1
                    editValue += step
                }) {
                    Icon(Icons.Default.AddCircleOutline, null, Modifier.size(44.dp), tint = Color(0xFF2979FF))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. MÔ TẢ
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("HƯỚNG DẪN", fontWeight = FontWeight.Bold, color = Color(0xFF2979FF), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentExercise!!.description,
                    color = Color.Black,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // --- BOTTOM SHEET THAY THẾ ---
        if (showSwapSheet) {
            ModalBottomSheet(onDismissRequest = { showSwapSheet = false }, sheetState = sheetState) {
                val candidates = AppRepository.getSwapCandidates(currentExercise!!)

                Column(modifier = Modifier.padding(16.dp).padding(bottom = 30.dp)) {
                    Text("Chọn bài thay thế", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

                    if (candidates.isEmpty()) {
                        Text("Không có bài tập thay thế phù hợp.", color = Color.Gray)
                    } else {
                        LazyColumn {
                            items(candidates) { candidate ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentExercise = candidate
                                            editValue = candidate.value
                                            showSwapSheet = false
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Chỗ này cũng load GIF nhưng nhỏ (thumbnail)
                                    // Bạn có thể dùng Coil hoặc painterResource (nếu ảnh nhỏ)
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(context)
                                                .data(candidate.imageRes)
                                                .size(200) // Load nhỏ cho nhẹ
                                                .build(),
                                            imageLoader = imageLoader
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(candidate.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(if (candidate.unit == MeasureUnit.TIME) "${candidate.value} giây" else "${candidate.value} lần", color = Color.Gray)
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PLAYER YOUTUBE ---
@Composable
fun YouTubePlayerComposable(videoId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isVideoError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            player.cueVideo(videoId, 0f)
                        }
                        override fun onError(player: YouTubePlayer, error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError) {
                            isVideoError = true
                        }
                    })
                }
            }
        )
        if (isVideoError) {
            Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    Text("Lỗi phát video", color = Color.White)
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
                        context.startActivity(intent)
                    }) { Text("Mở YouTube") }
                }
            }
        }
    }
}