package com.example.fitness

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop

@SuppressLint("MissingPermission")
@Composable
fun RunningTrackerScreen(onNavigateToSave: (Float, Long, Float) -> Unit) {
    val context = LocalContext.current
    var speed by remember { mutableStateOf(0f) }
    var distance by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }
    var runningTime by remember { mutableStateOf(0L) }
    var showStopDialog by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val pathPoints = remember { mutableStateListOf<LatLng>() }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            // Có thể thông báo người dùng
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            runningTime++
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isRunning) return
                val location = locationResult.lastLocation ?: return

                val newLatLng = LatLng(location.latitude, location.longitude)
                currentLatLng = newLatLng

                previousLocation?.let {
                    distance += it.distanceTo(location)
                    speed = location.speed * 3.6f
                }
                previousLocation = location
                pathPoints.add(newLatLng)
            }
        }
    }

    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        if (runningTime == 0L) {
            distance = 0f
            previousLocation = null
            pathPoints.clear()
        }
        isRunning = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(1000)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stopLocationUpdates() {
        isRunning = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun pauseRunning() {
        isRunning = false
    }

    fun resumeRunning() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        isRunning = true
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(1000)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    val caloriesBurned = distance * 0.06f
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(currentLatLng) {
        currentLatLng?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.5f)
                .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                currentLatLng?.let {
                    Marker(rememberMarkerState(position = it), title = "Current Location")
                }
                if (pathPoints.size >= 2) {
                    Polyline(points = pathPoints, color = MaterialTheme.colorScheme.primary, width = 8f)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Trình theo dõi chạy bộ", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0288D1), modifier = Modifier.padding(bottom = 16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoBox("Speed", "%.2f km/h".format(speed))
            InfoBox("Distance", "%.2f m".format(distance))
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoBox("Time", formatTime(runningTime))
            InfoBox("Calo", "%.2f kcal".format(caloriesBurned))
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = if (isRunning) "Trạng thái: Đang chạy" else "Trạng thái: Tạm dừng",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = if (isRunning) Color(0xFF4CAF50) else Color(0xFF757575),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { if (!isRunning) startLocationUpdates() },
                enabled = !isRunning,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1), disabledContainerColor = Color(0xFFB0BEC5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Bắt đầu", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = { if (isRunning) pauseRunning() },
                enabled = isRunning,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000), disabledContainerColor = Color(0xFFB0BEC5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Pause, contentDescription = "Tạm dừng", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    // Reset tất cả dữ liệu về trạng thái ban đầu
                    isRunning = false
                    distance = 0f
                    speed = 0f
                    runningTime = 0L
                    previousLocation = null
                    pathPoints.clear()
                    currentLatLng = null
                },
                enabled = runningTime > 0L,  // Chỉ bật nút khi có dữ liệu để reset
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336), // Màu đỏ báo reset/xóa
                    disabledContainerColor = Color(0xFFB0BEC5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }


            Spacer(Modifier.width(12.dp))

            Button(
                onClick = { if (runningTime > 0L) showStopDialog = true },
                enabled = runningTime > 0L,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), disabledContainerColor = Color(0xFFB0BEC5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Stop, contentDescription = "Dừng", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }

        if (showStopDialog) {
            AlertDialog(
                onDismissRequest = { showStopDialog = false },
                title = { Text("Dừng chạy?") },
                text = { Text("Bạn có muốn dừng và lưu lại quãng đường đã chạy?") },
                confirmButton = {
                    Button(onClick = {
                        stopLocationUpdates()
                        onNavigateToSave(distance, runningTime, caloriesBurned)
                        showStopDialog = false
                    }) {
                        Text("Có")
                    }
                },
                dismissButton = {
                    Button(onClick = { showStopDialog = false }) {
                        Text("Không")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoBox(label: String, value: String) {
    Column(
        modifier = Modifier
            .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .width(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF0288D1))
        Spacer(Modifier.height(8.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black)
    }
}

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
