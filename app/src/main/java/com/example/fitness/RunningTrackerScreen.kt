package com.example.fitness.ui.screens

import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.fitness.entity.CaloriesRecordEntity
import com.example.fitness.ui.theme.*
import com.example.fitness.viewModel.CaloriesViewModel
import com.example.fitness.viewModel.ChallengeViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.chromium.base.Flag
import java.util.Calendar

private val PrimaryBlue = Color(0xFF0EA5E9)
private val AccentBlue = Color(0xFF0284C7)
private val SurfaceStart = Color(0xFFF0F9FF)
private val SurfaceEnd = Color(0xFFE0F2FE)
private val SuccessGreen = Color(0xFF10B981)
private val TextSecondary = Color(0xFF6B7280)

@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RunningTrackerScreen(
    navController: NavHostController,
    caloriesViewModel: CaloriesViewModel,
    challengeViewModel: ChallengeViewModel,
    onNavigateToSave: () -> Unit,
    isChallengeLoading: Boolean = false
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    val activeChallenge by challengeViewModel.activeChallenge.collectAsStateWithLifecycle()
    val todayGoal by challengeViewModel.todayGoal.collectAsStateWithLifecycle()
    val completedDays by challengeViewModel.completedDays.collectAsStateWithLifecycle()

    var remainingTime by remember { mutableStateOf("") }

    LaunchedEffect(activeChallenge) {
        while (true) {
            remainingTime = challengeViewModel.getRemainingTime()
            delay(60_000L)
        }
    }

    var isRunning by remember { mutableStateOf(false) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var distance by remember { mutableFloatStateOf(0f) }
    var runningTime by remember { mutableLongStateOf(0L) }
    var caloriesBurned by remember { mutableFloatStateOf(0f) }
    var avgSpeed by remember { mutableFloatStateOf(0f) }
    var avgPace by remember { mutableStateOf("--:--'/km") }
    var maxSpeed by remember { mutableFloatStateOf(0f) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showEndChallengeDialog by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.0285, 105.8542), 15f)
    }

    fun getCurrentDayNumber(startDate: Long, totalDays: Int): Int {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        val diff = today - startDate
        val day = if (diff <= 0) 1 else ((diff / (1000 * 60 * 60 * 24)) + 1).toInt()
        return day.coerceAtMost(totalDays)
    }

    fun recalculateStats() {
        if (runningTime > 0 && distance > 0) {
            avgSpeed = (distance / 1000f) / (runningTime / 3600f)
            val paceMinPerKm = (runningTime / 60f) / (distance / 1000f)
            val min = paceMinPerKm.toInt()
            val sec = ((paceMinPerKm - min) * 60).toInt().coerceAtLeast(0)
            avgPace = "$min:${sec.toString().padStart(2, '0')}'/km"
            caloriesBurned = distance * 0.065f
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            var lastLocation: LatLng? = null
            var lastTime = 0L

            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                if (loc.accuracy > 60f) return

                val current = LatLng(loc.latitude, loc.longitude)
                val now = System.currentTimeMillis()

                if (lastLocation != null && isRunning) {
                    val dist = FloatArray(1)
                    android.location.Location.distanceBetween(
                        lastLocation!!.latitude, lastLocation!!.longitude,
                        current.latitude, current.longitude, dist
                    )

                    if (dist[0] > 2f) {
                        distance += dist[0]
                        pathPoints = pathPoints + current

                        val timeDiff = (now - lastTime) / 1000f
                        if (timeDiff > 0) {
                            val instantSpeed = (dist[0] / timeDiff) * 3.6f
                            if (instantSpeed > maxSpeed) maxSpeed = instantSpeed
                        }
                        recalculateStats()
                    }
                } else {
                    pathPoints = listOf(current)
                }

                lastLocation = current
                lastTime = now

                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(current, 17f),
                        durationMs = 600
                    )
                }
            }
        }
    }

    fun startTracking() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) return

        if (!isRunning && runningTime == 0L) {
            distance = 0f
            caloriesBurned = 0f
            avgSpeed = 0f
            avgPace = "--:--'/km"
            maxSpeed = 0f
            pathPoints = emptyList()
        }

        val request = LocationRequest.Builder(1000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(2f)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        isRunning = true
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isRunning = false
        recalculateStats()
    }

    DisposableEffect(Unit) {
        onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startTracking()
    }

    fun requestLocationPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) startTracking() else permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            runningTime++
            recalculateStats()
        }
    }

    fun saveAndSendToCoach() {
        if (runningTime == 0L) return

        val record = CaloriesRecordEntity(
            caloriesBurned = caloriesBurned,
            distance = distance,
            runningTime = runningTime,
            timestamp = System.currentTimeMillis()
        )
        caloriesViewModel.addRecord(record)

        val summary = """
            Tôi vừa chạy xong:
            • Quãng đường: ${"%.2f".format(distance / 1000)} km
            • Thời gian: ${runningTime / 60} phút ${runningTime % 60} giây
            • Tốc độ TB: ${"%.1f".format(avgSpeed)} km/h
            • Pace TB: $avgPace
            • Tốc độ cao nhất: ${"%.1f".format(maxSpeed)} km/h
            • Calo tiêu hao: ${"%.0f".format(caloriesBurned)} kcal
            
            Gợi ý kế hoạch tập tiếp theo cho tôi nhé!
        """.trimIndent()

        navController.navigate("coach?runSummary=${android.net.Uri.encode(summary)}")
        onNavigateToSave()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
            .padding(16.dp)
    ) {
        // ── Hiển thị thông tin thử thách (nếu có active) ──
        activeChallenge?.let { challenge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Flag,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Thử thách: ${challenge.name}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Thời gian còn lại: $remainingTime",
                        fontSize = 16.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.Medium
                    )

                    val currentDay = getCurrentDayNumber(challenge.startDate, challenge.totalDays)
                    Text(
                        "Ngày $currentDay / ${challenge.totalDays}",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    todayGoal?.let { goal ->
                        Spacer(Modifier.height(8.dp))
                        Text("Mục tiêu hôm nay: ${goal.description}", fontSize = 14.sp, color = AccentBlue)
                    }

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { completedDays.toFloat() / challenge.totalDays },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = SuccessGreen,
                        trackColor = Color.LightGray,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "$completedDays ngày hoàn thành",
                        fontSize = 13.sp,
                        color = SuccessGreen,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { showEndChallengeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kết thúc thử thách (Thừa nhận thất bại)", color = Color.White)
                    }
                }
            }
        }

        // ── Map Card ──
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                cameraPositionState = cameraPositionState
            ) {
                if (pathPoints.size > 1) {
                    Polyline(points = pathPoints, color = PrimaryBlue, width = 10f)
                }
                pathPoints.lastOrNull()?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Stats Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Thông số buổi chạy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    StatItem("Quãng đường", "${"%.2f".format(distance / 1000)} km", PrimaryBlue)
                    StatItem("Thời gian", "%02d:%02d".format(runningTime / 60, runningTime % 60), PrimaryBlue)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    StatItem("Tốc độ TB", "${"%.1f".format(avgSpeed)} km/h", AccentBlue)
                    StatItem("Pace TB", avgPace, AccentBlue)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    StatItem("Max tốc độ", "${"%.1f".format(maxSpeed)} km/h", PrimaryBlue)
                    StatItem("Calo", "${caloriesBurned.toInt()} kcal", PrimaryBlue)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlButton(
                text = "Bắt đầu",
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                enabled = !isRunning,
                color = PrimaryBlue
            ) { requestLocationPermissionAndStart() }

            ControlButton(
                text = "Dừng",
                icon = Icons.Default.Pause,
                enabled = isRunning,
                color = PrimaryBlue
            ) { stopTracking() }

            ControlButton(
                text = "Lưu & Tư vấn",
                icon = Icons.Default.Save,
                enabled = !isRunning && runningTime > 0,
                color = AccentBlue
            ) { showSaveDialog = true }
        }
    }

    // Save Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Hoàn thành buổi chạy!", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
            text = { Text("Lưu kết quả và nhận gợi ý từ AI Coach?") },
            confirmButton = {
                TextButton(onClick = {
                    saveAndSendToCoach()
                    showSaveDialog = false
                }) {
                    Text("Lưu & Tư vấn AI", color = PrimaryBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Hủy") }
            }
        )
    }

    // Dialog kết thúc thử thách (chuyển sang bảng xếp hạng sau khi xác nhận)
    if (showEndChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showEndChallengeDialog = false },
            title = { Text("Kết thúc thử thách sớm?", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn thừa nhận thất bại?\nCác tính năng sẽ được mở lại.") },
            confirmButton = {
                TextButton(onClick = {
                    challengeViewModel.endChallengeEarly(context)
                    showEndChallengeDialog = false
                    // Chuyển sang bảng xếp hạng
                    navController.navigate("leaderboard") {
                        popUpTo("running") { inclusive = false }  // Giữ running trong back stack nếu muốn quay lại
                    }
                }) {
                    Text("Xác nhận thất bại", color = WarningRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndChallengeDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ControlButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            scale.animateTo(1.12f, tween(200))
            scale.animateTo(1f, tween(200))
        }
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = if (enabled) color else Color.LightGray),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(56.dp).graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}