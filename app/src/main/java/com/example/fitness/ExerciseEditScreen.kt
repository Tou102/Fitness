package com.example.fitness

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.fitness.entity.AppRepository
import com.example.fitness.entity.MeasureUnit
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

enum class TabType(val title: String) {
    ANIMATION("Hoạt hình"),
    MUSCLE("Cơ bắp"),
    INSTRUCTION("Hướng dẫn")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditScreen(exerciseId: Int, onBack: () -> Unit) {
    val context = LocalContext.current

    var currentExercise by remember { mutableStateOf(AppRepository.getExerciseById(exerciseId)) }
    if (currentExercise == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Không tìm thấy bài tập", color = Color.Gray) }
        return
    }

    var editValue by remember(currentExercise) { mutableStateOf(currentExercise!!.value) }
    var selectedTab by remember { mutableStateOf(TabType.INSTRUCTION) }
    var showSwapSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }

    val primary = Color(0xFF0EA5E9)    // Xanh dương neon
    val accent = Color(0xFF0284C7)     // Xanh đậm

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Button(
                onClick = {
                    AppRepository.saveUserChanges(
                        exerciseIdInput = exerciseId,
                        newValue = editValue,
                        newActiveId = currentExercise!!.id
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Icon(Icons.Default.Save, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("LƯU", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Header đơn giản
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        currentExercise!!.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { showSwapSheet = true }) {
                        Icon(Icons.Default.SwapHoriz, null, tint = accent)
                        Spacer(Modifier.width(4.dp))
                        Text("Thay thế", color = accent)
                    }
                }

                // Media Card đơn giản
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Crossfade(targetState = selectedTab, animationSpec = tween(300)) { tab ->
                        when (tab) {
                            TabType.INSTRUCTION -> {
                                if (currentExercise!!.videoId.isNotEmpty()) {
                                    YouTubePlayerComposable(videoId = currentExercise!!.videoId)
                                } else {
                                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Không có video", color = Color.Gray) }
                                }
                            }
                            TabType.ANIMATION -> {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(context)
                                            .data(currentExercise!!.imageRes)
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = imageLoader
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            TabType.MUSCLE -> {
                                Image(
                                    painter = painterResource(currentExercise!!.muscleImageRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Tabs đơn giản
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabType.values().forEach { tab ->
                        val isSelected = tab == selectedTab
                        val bg = if (isSelected) primary else Color.Transparent
                        val textColor = if (isSelected) Color.White else Color.DarkGray

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(bg)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab.title,
                                color = textColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Counter đơn giản
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val step = if (currentExercise!!.unit == MeasureUnit.TIME) 5 else 1
                            if (editValue >= step) editValue -= step
                        }
                    ) {
                        Icon(Icons.Default.RemoveCircle, null, tint = primary, modifier = Modifier.size(48.dp))
                    }

                    Spacer(Modifier.width(32.dp))

                    Text(
                        if (currentExercise!!.unit == MeasureUnit.TIME) "${editValue}s" else "x$editValue",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )

                    Spacer(Modifier.width(32.dp))

                    IconButton(
                        onClick = {
                            val step = if (currentExercise!!.unit == MeasureUnit.TIME) 5 else 1
                            editValue += step
                        }
                    ) {
                        Icon(Icons.Default.AddCircle, null, tint = primary, modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Description
                Column {
                    Text(
                        "HƯỚNG DẪN CHI TIẾT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        currentExercise!!.description,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }
            }

            // Bottom Sheet đơn giản
            if (showSwapSheet) {
                ModalBottomSheet(onDismissRequest = { showSwapSheet = false }, sheetState = sheetState) {
                    val candidates = AppRepository.getSwapCandidates(currentExercise!!)

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Chọn bài tập thay thế",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Spacer(Modifier.height(12.dp))

                        LazyColumn {
                            items(candidates) { candidate ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable {
                                            currentExercise = candidate
                                            editValue = candidate.value
                                            showSwapSheet = false
                                        },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = ImageRequest.Builder(context)
                                                    .data(candidate.imageRes)
                                                    .crossfade(true)
                                                    .build(),
                                                imageLoader = imageLoader
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(candidate.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(
                                                if (candidate.unit == MeasureUnit.TIME) "${candidate.value} giây" else "x${candidate.value} lần",
                                                color = Color.Gray,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubePlayerComposable(videoId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            player.cueVideo(videoId, 0f)
                            player.play()
                        }
                        override fun onError(player: YouTubePlayer, error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError) {
                            isError = true
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isError) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Không phát được video", color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))) },
                        colors = ButtonDefaults.buttonColors(Color(0xFF0EA5E9))
                    ) { Text("Mở YouTube") }
                }
            }
        }
    }
}