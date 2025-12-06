package com.example.fitness.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness.viewModel.ChatViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.shadow

// Fitness theme colors
private val FitnessBlue = Color(0xFF0EA5E9)     // Xanh dương khỏe
private val DeepBlue = Color(0xFF0369A1)        // Xanh đậm
private val LightBlue = Color(0xFFE0F2FE)       // Xanh pastel
private val UserBubble = Color(0xFF0284C7)      // Xanh user
private val BotBubble = Color(0xFFDFF3FF)       // Trắng xanh nhạt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()

    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // chọn ảnh
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)!!.use { s -> s.readBytes() }
            viewModel.sendImageForAnalysis(bytes)
        }
    }

    // --- UI ---
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Fitness AI Coach",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FitnessBlue
                ),
                modifier = Modifier.shadow(6.dp)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFE0F7FF),
                            Color(0xFFE8F8FF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Danh sách tin nhắn
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    state = scrollState
                ) {
                    items(messages) { (msg, isUser) ->
                        ChatBubbleFitness(text = msg, isUser = isUser)
                    }
                }

                // --- Input bar ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("Nhập tin nhắn…", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FitnessBlue,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(Modifier.width(6.dp))

                        // Gửi tin nhắn
                        IconButton(
                            onClick = {
                                if (text.isNotBlank()) {
                                    viewModel.sendMessage(text)
                                    text = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(FitnessBlue)
                                .size(46.dp)
                        ) {
                            Icon(Icons.Rounded.Send, "", tint = Color.White)
                        }

                        Spacer(Modifier.width(6.dp))

                        // Chọn ảnh
                        IconButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DeepBlue)
                                .size(46.dp)
                        ) {
                            Icon(Icons.Rounded.Image, "", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Auto scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { scrollState.animateScrollToItem(messages.size - 1) }
        }
    }
}

// Bubble đẹp
@Composable
fun ChatBubbleFitness(text: String, isUser: Boolean) {
    val bubbleColor = if (isUser) UserBubble else BotBubble
    val textColor = if (isUser) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(6.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(if (isUser) 6.dp else 2.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(14.dp),
                color = textColor,
                fontSize = 15.sp
            )
        }
    }
}
