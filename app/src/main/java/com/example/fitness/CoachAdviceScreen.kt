// CoachAdviceScreen.kt — Health-theme redesign + bảng chú thích thuật ngữ
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.example.fitness.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitness.AiCoach
import kotlin.math.roundToInt

@Composable
fun CoachAdviceScreen(navController: NavHostController) {
    // ----- THEME PALETTE -----
    val healthGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0EA5E9),
            Color(0xFF38BDF8),
            Color(0xFFE6F4FF)
        )
    )
    val cardBg = Color(0xFFF8FAFF)
    val accent = Color(0xFF10B981)
    val accentSoft = Color(0xFFDCFCE7)
    val info = Color(0xFF0369A1)

    // ----- STATE -----
    var paceText by rememberSaveable { mutableStateOf("") }
    var rpe by rememberSaveable { mutableIntStateOf(6) }
    val goals = listOf("Giảm cân", "5K", "10K", "21K")
    var goal by rememberSaveable { mutableStateOf(goals.first()) }
    var result by rememberSaveable { mutableStateOf<String?>(null) }

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column {
                        Text("AI Coach", fontWeight = FontWeight.Bold)
                        Text(
                            "Tư vấn chạy bộ cá nhân hoá",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .background(healthGradient)
                .padding(inner)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = cardBg),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(18.dp)
                ) {
                    // HEADER
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = accent, shape = CircleShape) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Thiết lập buổi chạy", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text(
                                "Nhập pace dễ, chọn RPE và mục tiêu.",
                                color = Color(0xFF6B7280),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        StatPill(label = "RPE", value = rpe.toString())
                    }

                    Spacer(Modifier.height(16.dp))

                    // PACE INPUT
                    Text("Pace dễ (phút/km)", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = paceText,
                        onValueChange = { txt ->
                            val filtered = txt.filter { it.isDigit() || it == '.' }
                            paceText = if (filtered.count { it == '.' } <= 1) filtered else paceText
                        },
                        placeholder = { Text("vd 5.20 nghĩa là 5'12\"/km") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Timeline, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    HelperRow(paceText, info)

                    Spacer(Modifier.height(14.dp))

                    // RPE SLIDER
                    Text("Độ gắng sức (RPE: 1–10)", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = rpe.toFloat(),
                            onValueChange = { rpe = it.roundToInt().coerceIn(1, 10) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = accent,
                                activeTrackColor = accent,
                                inactiveTrackColor = accentSoft
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(12.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text(rpe.toString(), fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) }
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // GOALS
                    Text("Mục tiêu", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    GoalChips(goals = goals, selected = goal, onSelect = { goal = it })

                    Spacer(Modifier.height(18.dp))
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(Modifier.height(12.dp))

                    // ACTION
                    val pace = paceText.toDoubleOrNull()
                    val canSubmit = rpe in 1..10 && goal.isNotBlank()

                    GradientButton(
                        text = "Nhận tư vấn ngay",
                        enabled = canSubmit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        result = AiCoach.recommendNextRun(
                            paceMinPerKm = pace,
                            rpe = rpe,
                            goal = goal
                        )
                    }

                    AnimatedVisibility(
                        visible = !result.isNullOrBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Spacer(Modifier.height(16.dp))
                        AdviceCard(text = result ?: "", accent = accent)
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            // --- GLOSSARY CORNER ---
            GlossaryCorner(accent = accent)
        }
    }
}

// --- COMPONENTS ---

@Composable
private fun StatPill(label: String, value: String) {
    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(999.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color(0xFF64748B))
            Spacer(Modifier.width(6.dp))
            Surface(color = Color(0xFF0EA5E9), shape = CircleShape) {
                Text(value, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun HelperRow(paceText: String, infoColor: Color) {
    val pace = paceText.toDoubleOrNull()
    val easy = pace?.let { String.format("%.2f", it + 0.20) }
    val tempo = pace?.let { String.format("%.2f", it - 0.15) }

    AnimatedVisibility(visible = pace != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFECF7FF))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gợi ý pace:", fontWeight = FontWeight.SemiBold, color = infoColor)
            Spacer(Modifier.width(10.dp))
            if (easy != null) Text("Easy ~ $easy", color = infoColor)
            Spacer(Modifier.width(16.dp))
            if (tempo != null) Text("Tempo ~ $tempo", color = infoColor)
        }
    }
}

@Composable
private fun AdviceCard(text: String, accent: Color) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = accent, shape = CircleShape) {
                    Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("Gợi ý cho bạn", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(text, color = Color(0xFF374151))
        }
    }
}

@Composable
private fun GoalChips(goals: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        goals.forEach { g ->
            val isSelected = g == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(g) },
                label = { Text(g) },
                leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null) } } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFDCFCE7),
                    selectedLabelColor = Color(0xFF065F46)
                ),
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun GradientButton(text: String, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF14B8A6), Color(0xFF10B981), Color(0xFF22C55E)),
        tileMode = TileMode.Clamp
    )
    val alpha by animateFloatAsState(targetValue = if (enabled) 1f else 0.5f, animationSpec = tween(200, easing = FastOutSlowInEasing), label = "")
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(gradient, RoundedCornerShape(18.dp))
                .graphicsLayer(alpha = alpha)
                .padding(vertical = 14.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun GlossaryCorner(accent: Color) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 80.dp)
                    .widthIn(max = 260.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("📘 Chú thích thuật ngữ", fontWeight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(8.dp))
                    Text("• **RPE (Rate of Perceived Exertion):** Mức gắng sức tự cảm nhận, 1 = rất nhẹ, 10 = cực nặng.")
                    Text("• **HRmax:** Nhịp tim tối đa ≈ 220 - tuổi, dùng để tính vùng tim (zone).")
                    Text("• **Zone 2:** 60–70% HRmax, vùng lý tưởng để đốt mỡ và tăng sức bền nền.")
                    Text("• **Pace:** Thời gian hoàn thành 1 km, đơn vị phút/km.")
                    Text("• **Tempo run:** Chạy ở mức nặng vừa, thường ~80–88% HRmax.")
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = accent,
            shape = CircleShape,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Chú thích", tint = Color.White)
        }
    }
}
