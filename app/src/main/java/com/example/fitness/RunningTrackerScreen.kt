package com.example.fitness.ui.screens

import androidx.compose.ui.graphics.vector.ImageVector
import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.fitness.viewModel.CaloriesViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.fitness.entity.CaloriesRecordEntity
import com.example.fitness.AiCoach // dùng AiCoach từ package gốc

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun RunningTrackerScreen(
    navController: NavHostController,          // 👈 THÊM navController vào tham số
    caloriesViewModel: CaloriesViewModel,
    onNavigateToSave: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    var hasLocationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) {
            Toast.makeText(context, "Cần cấp quyền vị trí để sử dụng", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var isRunning by remember { mutableStateOf(false) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var distance by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }
    var runningTime by remember { mutableStateOf(0L) }

    // AI Coach state
    var rpe by remember { mutableStateOf(5) }
    var goal by remember { mutableStateOf("Giảm cân") }
    var coachSuggest by remember { mutableStateOf<String?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }

    fun avgPaceMinPerKm(distanceM: Float, timeSec: Long): Double? {
        if (distanceM < 200 || timeSec < 60) return null
        val km = distanceM / 1000f
        val min = timeSec / 60f
        return (min / km).toDouble()
    }

    // speed smoothing logic
    val speedHistory = remember { mutableStateListOf<Float>() }
    val maxSpeedHistory = 10
    val minSpeedThreshold = 1.0f
    val minDistanceThreshold = 10f
    val maxAccuracyThreshold = 15f

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.0285, 105.8542), 15f)
    }

    val locationCallback = remember {
        object : LocationCallback() {
            var lastUpdateTime = 0L
            var lastPoint: LatLng? = null
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation ?: return
                if (loc.accuracy > maxAccuracyThreshold) return
                val newPoint = LatLng(loc.latitude, loc.longitude)
                val currentTime = System.currentTimeMillis()
                if (lastPoint != null) {
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        lastPoint!!.latitude, lastPoint!!.longitude,
                        newPoint.latitude, newPoint.longitude,
                        results
                    )
                    val dist = results[0]
                    val timeDiff = (currentTime - lastUpdateTime).coerceAtLeast(1L)
                    val speedCalculated = if (loc.hasSpeed() && loc.speed > 0) {
                        loc.speed * 3.6f
                    } else (dist / timeDiff.toFloat()) * 1000f * 3.6f
                    if (dist > minDistanceThreshold) {
                        distance += dist
                        pathPoints = pathPoints + newPoint
                        speedHistory.add(speedCalculated)
                        if (speedHistory.size > maxSpeedHistory) speedHistory.removeAt(0)
                        val avgSpeed = speedHistory.average().toFloat()
                        speed = if (avgSpeed >= minSpeedThreshold) avgSpeed else 0f
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(newPoint))
                        }
                        lastPoint = newPoint
                        lastUpdateTime = currentTime
                    }
                } else {
                    pathPoints = listOf(newPoint)
                    lastPoint = newPoint
                    lastUpdateTime = currentTime
                }
            }
        }
    }

    fun startLocationUpdates() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(2000)
            .build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        isRunning = true
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isRunning = false
        speedHistory.clear()
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            runningTime++
        }
    }

    val caloriesBurned = distance * 0.06f

    fun saveRun() {
        if (runningTime == 0L) return
        val record = CaloriesRecordEntity(
            caloriesBurned = caloriesBurned,
            timestamp = System.currentTimeMillis(),
            distance = distance,
            runningTime = runningTime
        )
        caloriesViewModel.addRecord(record)
        onNavigateToSave()
        distance = 0f
        speed = 0f
        runningTime = 0L
        pathPoints = emptyList()
        speedHistory.clear()
        isRunning = false
    }

    val primaryBlue = Color(0xFF0288D1)
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF01579B)
    val accentColor = Color(0xFFFFCA28)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Map
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                if (pathPoints.size > 1) {
                    Polyline(points = pathPoints, color = primaryBlue, width = 12f)
                }
                pathPoints.lastOrNull()?.let { lastPoint ->
                    val markerState = rememberMarkerState(position = lastPoint)
                    Marker(
                        state = markerState,
                        title = "Vị trí hiện tại",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = lightBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Thông số chạy bộ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = darkBlue
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem("Tốc độ", "%.2f km/h".format(speed), darkBlue)
                    MetricItem("Quãng đường", "%.2f m".format(distance), darkBlue)
                    MetricItem(
                        "Thời gian",
                        "%02d:%02d:%02d".format(
                            runningTime / 3600, (runningTime % 3600) / 60, runningTime % 60
                        ),
                        darkBlue
                    )
                }
            }
        }

        // AI Coach suggestion
        if (coachSuggest != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Text(
                    "Gợi ý buổi tiếp theo",
                    modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(coachSuggest!!, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                "Bắt đầu",
                Icons.Default.DirectionsRun,
                !isRunning,
                { startLocationUpdates() },
                primaryBlue,
                Color.White,
                Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            ActionButton(
                "Tạm dừng",
                Icons.Default.Pause,
                isRunning,
                { stopLocationUpdates() },
                primaryBlue,
                Color.White,
                Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            ActionButton(
                "Reset",
                Icons.Default.Refresh,
                true,
                {
                    stopLocationUpdates()
                    distance = 0f
                    speed = 0f
                    runningTime = 0L
                    pathPoints = emptyList()
                    speedHistory.clear()
                },
                accentColor,
                darkBlue,
                Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            ActionButton(
                "Lưu",
                Icons.Default.Save,
                !isRunning && runningTime > 0L,
                { showSaveDialog = true },
                primaryBlue,
                Color.White,
                Modifier.weight(1f)
            )
        }
    }

    // RPE + goal dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val pace = avgPaceMinPerKm(distance, runningTime)
                    coachSuggest = AiCoach.recommendNextRun(pace, rpe, goal)
                    saveRun()
                    showSaveDialog = false
                }) { Text("Lưu") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Nút Tư vấn AI (chuyển sang CoachAdviceScreen)
                    ElevatedButton(
                        onClick = {
                            showSaveDialog = false
                            navController.navigate("coach") // 👈 giờ hợp lệ vì có navController
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF0EA5E9),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsRun,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Tư vấn AI")
                    }

                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            },
            title = {
                Text(
                    "Đánh giá buổi chạy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text("RPE (1 = rất dễ, 10 = rất mệt)", fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = rpe.toFloat(),
                        onValueChange = { rpe = it.toInt().coerceIn(1, 10) },
                        steps = 8,
                        valueRange = 1f..10f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0EA5E9),
                            activeTrackColor = Color(0xFF0EA5E9)
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Mục tiêu", fontWeight = FontWeight.SemiBold)
                    DropdownGoal(goal) { goal = it }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider() // Material3
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Bạn có thể nhấn “Tư vấn AI” để nhận gợi ý buổi tập kế tiếp chi tiết hơn.",
                        color = Color(0xFF475569),
                        fontSize = 13.sp
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFFFDFDFE)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownGoal(current: String, onPick: (String) -> Unit) {
    val items = listOf("Giảm cân", "5K", "10K", "21K")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = current,
            onValueChange = {},
            label = { Text("Mục tiêu") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        expanded = false
                        onPick(it)
                    }
                )
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color, fontSize = 14.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(enabled) {
        if (enabled) {
            scale.animateTo(1.1f, tween(200))
            scale.animateTo(1f, tween(200))
        }
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor, contentColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
