package com.example.fitness.ui.screens

import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.fitness.viewModel.CaloriesViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Màu sắc xanh dương đồng bộ toàn app
private val PrimaryBlue = Color(0xFF0EA5E9)     // Xanh dương neon chính
private val AccentBlue  = Color(0xFF0284C7)     // Xanh đậm highlight
private val SurfaceStart = Color(0xFFF0F9FF)    // Nền gradient đầu
private val SurfaceEnd  = Color(0xFFE0F2FE)     // Nền gradient cuối (xanh nhạt)
private val CardBg      = Color.White.copy(alpha = 0.96f)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF6B7280)

@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RunningTrackerScreen(
    navController: NavHostController,
    caloriesViewModel: CaloriesViewModel,
    onNavigateToSave: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    var isRunning by remember { mutableStateOf(false) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var distance by remember { mutableStateOf(0f) }
    var runningTime by remember { mutableStateOf(0L) }
    var caloriesBurned by remember { mutableStateOf(0f) }
    var avgSpeed by remember { mutableStateOf(0f) }
    var avgPace by remember { mutableStateOf("--:--'/km") }
    var maxSpeed by remember { mutableStateOf(0f) }

    var showSaveDialog by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.0285, 105.8542), 15f)
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
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return

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

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startTracking()
    }

    fun requestLocationPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

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

        caloriesViewModel.addRecord(
            com.example.fitness.entity.CaloriesRecordEntity(
                caloriesBurned = caloriesBurned,
                distance = distance,
                runningTime = runningTime,
                timestamp = System.currentTimeMillis()
            )
        )

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

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SurfaceStart, SurfaceEnd)))
            .padding(16.dp)
    ) {
        // Map Card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                cameraPositionState = cameraPositionState
            ) {
                if (pathPoints.size > 1) {
                    Polyline(
                        points = pathPoints,
                        color = PrimaryBlue,
                        width = 10f
                    )
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

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Thông số buổi chạy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

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

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlButton(
                text = "Bắt đầu",
                icon = Icons.Default.DirectionsRun,
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
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) color else Color.LightGray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .height(56.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        Icon(icon, null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}