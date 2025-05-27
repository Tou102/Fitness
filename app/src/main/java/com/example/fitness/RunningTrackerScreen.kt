package com.example.fitness

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import android.provider.Settings
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.fitness.entity.CaloriesRecordEntity
import com.example.fitness.viewModel.CaloriesViewModel
import kotlinx.coroutines.launch

// Move getInitialLocation outside the composable
fun getInitialLocation(client: FusedLocationProviderClient, callback: (Location?) -> Unit) {
    try {
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener { e ->
                Log.e("RunningTracker", "Failed to get initial location: ${e.message}")
                callback(null)
            }
    } catch (e: SecurityException) {
        Log.e("RunningTracker", "Security exception: ${e.message}")
        callback(null)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun RunningTrackerScreen(
    onNavigateToSave: () -> Unit,
    caloriesViewModel: CaloriesViewModel
) {
    val context = LocalContext.current
    var speed by remember { mutableStateOf(0f) }
    var distance by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }
    var runningTime by remember { mutableStateOf(0L) }
    var showStopDialog by remember { mutableStateOf(false) }
    var gpsEnabled by remember { mutableStateOf(false) }
    var showGpsDialog by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val pathPoints = remember { mutableStateListOf<LatLng>() }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Define locationManager
    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Quyền truy cập vị trí bị từ chối. Vui lòng cấp quyền để sử dụng tính năng này.", Toast.LENGTH_LONG).show()
        } else {
            // Check GPS and get initial location after permission is granted
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!gpsEnabled) {
                showGpsDialog = true
            } else {
                coroutineScope.launch {
                    getInitialLocation(fusedLocationClient) { location ->
                        if (location != null) {
                            currentLatLng = LatLng(location.latitude, location.longitude)
                            Log.d("RunningTracker", "Initial location: ${location.latitude}, ${location.longitude}, accuracy: ${location.accuracy}")
                        }
                    }
                }
            }
        }
    }

    // Check GPS and permissions on start
    LaunchedEffect(Unit) {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            showGpsDialog = true
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (gpsEnabled) {
            // Get initial location on start
            getInitialLocation(fusedLocationClient) { location ->
                if (location != null) {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("RunningTracker", "Initial location on start: ${location.latitude}, ${location.longitude}, accuracy: ${location.accuracy}")
                }
            }
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            runningTime++
        }
    }

    val caloriesBurned = distance * 0.06f

    fun saveRun() {
        val record = CaloriesRecordEntity(
            caloriesBurned = caloriesBurned,
            timestamp = System.currentTimeMillis(),
            distance = distance,
            runningTime = runningTime
        )
        caloriesViewModel.addRecord(record)
        onNavigateToSave()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isRunning) return
                val location = locationResult.lastLocation ?: return

                // Log for debugging
                Log.d("RunningTracker", "Location received: ${location.latitude}, ${location.longitude}, accuracy: ${location.accuracy}")

                // Accept locations with accuracy under 20m
                if (location.accuracy > 20f) {
                    Log.d("RunningTracker", "Location accuracy too low: ${location.accuracy}")
                    return
                }

                // Update location
                val newLatLng = LatLng(location.latitude, location.longitude)
                currentLatLng = newLatLng
                pathPoints.add(newLatLng)

                // Use GPS speed if available
                val gpsSpeed = if (location.hasSpeed()) {
                    location.speed * 3.6f // Convert m/s to km/h
                } else {
                    0f
                }

                // Minimum speed threshold to consider as stationary
                val minSpeedThreshold = 0.5f // km/h
                if (gpsSpeed < minSpeedThreshold && previousLocation != null) {
                    speed = 0f // Set speed to 0 if below threshold
                    return // Skip distance update when stationary
                }

                // Update distance if moved
                previousLocation?.let { prevLocation ->
                    val distanceChange = prevLocation.distanceTo(location)
                    if (distanceChange > 5f) { // 5m threshold for sensitivity
                        distance += distanceChange
                        speed = (speed * 0.4f + gpsSpeed * 0.6f) // Smooth speed
                    }
                }
                previousLocation = location
            }
        }
    }

    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDialog = true
            return
        }
        if (runningTime == 0L) {
            distance = 0f
            previousLocation = null
            pathPoints.clear()
        }
        isRunning = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates() {
        isRunning = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun pauseRunning() {
        isRunning = false
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(currentLatLng) {
        currentLatLng?.let {
            Log.d("RunningTracker", "Updating camera to: $it")
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
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

        Text(
            "Trình theo dõi chạy bộ",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0288D1),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoBox("Speed", "%.2f km/h".format(speed))
            InfoBox("Distance", "%.2f m".format(distance))
        }

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            InfoBox("Time", formatTime(runningTime))
            InfoBox ("Calo", "%.2f kcal".format(caloriesBurned))
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
                    isRunning = false
                    distance = 0f
                    speed = 0f
                    runningTime = 0L
                    previousLocation = null
                    pathPoints.clear()
                    currentLatLng = null
                },
                enabled = runningTime > 0L,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336), disabledContainerColor = Color(0xFFB0BEC5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(28.dp))
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
                        saveRun()
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

        if (showGpsDialog) {
            AlertDialog(
                onDismissRequest = { showGpsDialog = false },
                title = { Text("Bật GPS") },
                text = { Text("GPS chưa được bật. Vui lòng bật GPS để theo dõi vị trí.") },
                confirmButton = {
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        showGpsDialog = false
                    }) {
                        Text("Mở cài đặt")
                    }
                },
                dismissButton = {
                    Button(onClick = { showGpsDialog = false }) {
                        Text("Hủy")
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