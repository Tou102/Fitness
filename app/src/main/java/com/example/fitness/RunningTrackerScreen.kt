import androidx.compose.ui.graphics.vector.ImageVector



import android.Manifest
import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun RunningTrackerScreen(
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

    // Store recent speeds for moving average
    val speedHistory = remember { mutableStateListOf<Float>() }
    val maxSpeedHistory = 5 // Number of samples for moving average
    val minSpeedThreshold = 0.5f // km/h, below this we consider stationary
    val minDistanceThreshold = 5f // meters, minimum distance to add a new point
    val maxAccuracyThreshold = 20f // meters, maximum acceptable location accuracy

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.0285, 105.8542), 15f)
    }

    // Location callback
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

                    val timeDiff = (currentTime - lastUpdateTime).coerceAtLeast(1L) // ms, tránh chia 0
                    val speedCalculated = (dist / timeDiff.toFloat()) * 1000f * 3.6f // m/ms -> m/s -> km/h

                    // Cập nhật speed nếu đủ điều kiện (khoảng cách đủ lớn, GPS chính xác)
                    if (dist > minDistanceThreshold && loc.accuracy <= maxAccuracyThreshold) {
                        distance += dist
                        pathPoints = pathPoints + newPoint
                        speed = if (speedCalculated >= minSpeedThreshold) speedCalculated else 0f

                        // Cập nhật camera khi di chuyển
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(newPoint))
                        }

                        lastPoint = newPoint
                        lastUpdateTime = currentTime
                    }
                } else {
                    // Khởi tạo lần đầu
                    pathPoints = listOf(newPoint)
                    lastPoint = newPoint
                    lastUpdateTime = currentTime
                    speed = 0f
                }
            }
        }
    }


    // Quản lý cập nhật vị trí
    fun startLocationUpdates() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(2000) // Batch updates to reduce noise
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
        speedHistory.clear() // Clear speed history when stopping
    }

    // Timer đếm thời gian chạy
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            runningTime++
        }
    }

    // Tính calo đốt cháy (ước lượng đơn giản)
    val caloriesBurned = distance * 0.06f

    // Lưu dữ liệu chạy bộ vào database qua ViewModel
    fun saveRun() {
        if (runningTime == 0L) return
        val record = com.example.fitness.entity.CaloriesRecordEntity(
            caloriesBurned = caloriesBurned,
            timestamp = System.currentTimeMillis(),
            distance = distance,
            runningTime = runningTime
        )
        caloriesViewModel.addRecord(record)
        onNavigateToSave()
        // Reset
        distance = 0f
        speed = 0f
        runningTime = 0L
        pathPoints = emptyList()
        speedHistory.clear()
        isRunning = false
    }

    // Color scheme for fitness theme (blue palette)
    val primaryBlue = Color(0xFF0288D1) // Fitness blue
    val lightBlue = Color(0xFF4FC3F7)
    val darkBlue = Color(0xFF01579B)
    val accentColor = Color(0xFFFFCA28) // Yellow accent for contrast

    // UI with enhanced design
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Map Card
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
                    Polyline(
                        points = pathPoints,
                        color = primaryBlue,
                        width = 12f
                    )
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

        // Metrics Card
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
                    text = "Thông số chạy bộ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = darkBlue,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "Tốc độ",
                        value = "%.2f km/h".format(speed),
                        color = darkBlue
                    )
                    MetricItem(
                        label = "Quãng đường",
                        value = "%.2f m".format(distance),
                        color = darkBlue
                    )
                    MetricItem(
                        label = "Thời gian",
                        value = "%02d:%02d:%02d".format(
                            runningTime / 3600,
                            (runningTime % 3600) / 60,
                            runningTime % 60
                        ),
                        color = darkBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                text = "Bắt đầu",
                icon = Icons.Default.DirectionsRun,
                enabled = !isRunning,
                onClick = { if (!isRunning) startLocationUpdates() },
                containerColor = primaryBlue,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionButton(
                text = "Tạm dừng",
                icon = Icons.Default.Pause,
                enabled = isRunning,
                onClick = { if (isRunning) stopLocationUpdates() },
                containerColor = primaryBlue,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionButton(
                text = "Reset",
                icon = Icons.Default.Refresh,
                enabled = true,
                onClick = {
                    stopLocationUpdates()
                    distance = 0f
                    speed = 0f
                    runningTime = 0L
                    pathPoints = emptyList()
                    speedHistory.clear()
                },
                containerColor = accentColor,
                contentColor = darkBlue,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionButton(
                text = "Lưu",
                icon = Icons.Default.Save,
                enabled = !isRunning && runningTime > 0L,
                onClick = { saveRun() },
                containerColor = primaryBlue,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Reusable MetricItem Composable
@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = color,
                fontSize = 14.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 18.sp
            )
        )
    }
}

// Reusable ActionButton Composable with Animation
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
            scale.animateTo(1.1f, animationSpec = tween(200))
            scale.animateTo(1f, animationSpec = tween(200))
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
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

// Updated AnimateExample Composable
@Composable
fun AnimateExample() {
    val animatable = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                scope.launch {
                    animatable.animateTo(1f, animationSpec = tween(300))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Start Animation",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    scaleX = animatable.value
                    scaleY = animatable.value
                }
                .background(Color(0xFF4FC3F7), shape = RoundedCornerShape(8.dp))
        )
    }
}