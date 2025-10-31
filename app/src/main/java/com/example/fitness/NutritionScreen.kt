package com.example.fitness.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fitness.repository.NutritionRepoLocalFirst
import kotlinx.coroutines.launch

// ---- Palette & styles ----
private val BluePrimary = Color(0xFF0EA5E9)      // sky-500
private val BluePrimaryDark = Color(0xFF0284C7)  // sky-600
private val BlueLight = Color(0xFFE0F2FE)        // sky-100
private val SurfaceSoft = Color(0xFFF8FAFC)      // slate-50

// ---- Data model ----
private data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val repoLocalFirst = remember { NutritionRepoLocalFirst(context) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(1, "Chào bạn! Gửi tên món ăn (vd: 'phở gà 500g') để mình ước lượng calo nhé 🍜", false)
        )
    }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Tự cuộn xuống cuối khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // Nền gradient xanh dương "healthy"
    val healthGradient = Brush.verticalGradient(
        colors = listOf(
            BluePrimary,
            Color(0xFF38BDF8), // sky-400
            BlueLight
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(healthGradient),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Chat dinh dưỡng",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("workout") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = BluePrimaryDark
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            // Thanh nhập dạng capsule nổi trên nền
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = SurfaceSoft.copy(alpha = 0.98f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        placeholder = { Text("Nhập món ăn…", color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            cursorColor = BluePrimary
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            val content = input.trim()
                            if (content.isNotEmpty()) {
                                messages += ChatMessage(System.currentTimeMillis(), content, true)
                                input = ""
                                scope.launch {
                                    val reply = repoLocalFirst.getNutritionInfo(content)
                                    messages += ChatMessage(System.currentTimeMillis() + 1, reply, false)
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(44.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = BluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Gửi")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(healthGradient)
        ) {
            // Lớp thẻ nội dung mềm để dễ đọc
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp),
                color = Color.White.copy(alpha = 0.6f),
                tonalElevation = 0.dp
            ) {
                if (messages.isEmpty()) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            "Bắt đầu trò chuyện về dinh dưỡng của bạn!",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(messages, key = { _, m -> m.id }) { _, msg ->
                            ChatBubble(message = msg.text, isUser = msg.isUser)
                        }
                        item { Spacer(Modifier.height(84.dp)) } // chừa chỗ cho bottom bar
                    }
                }
            }
        }
    }
}

/** Bong bóng chat: xanh dương cho user, trắng + viền xanh cho bot */
@Composable
private fun ChatBubble(message: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Avatar bot
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("AI", style = MaterialTheme.typography.labelSmall, color = BluePrimary)
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp, topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 6.dp,
                bottomEnd  = if (isUser) 6.dp else 18.dp
            ),
            color = if (isUser) BluePrimary else Color.White,
            tonalElevation = if (isUser) 2.dp else 1.dp,
            shadowElevation = if (isUser) 2.dp else 0.dp,
            border = if (isUser) null else BorderStroke(1.dp, BlueLight.copy(alpha = 0.9f))
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else Color(0xFF0F172A)
            )
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            // Avatar user
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("You", style = MaterialTheme.typography.labelSmall, color = BluePrimaryDark)
            }
        }
    }
}