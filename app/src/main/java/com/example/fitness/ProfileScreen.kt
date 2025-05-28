import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitness.R
import com.example.fitness.utils.copyUriToInternalStorage
import com.example.fitness.viewModel.UserViewModel
import com.example.fitness.viewModel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    workoutViewModel: WorkoutViewModel,
    userId: Int
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val user by userViewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var username by rememberSaveable(user) { mutableStateOf("Người dùng") }
    var avatarUri by rememberSaveable(user) { mutableStateOf<Uri?>(null) }
    var expandedMenu by remember { mutableStateOf(false) }
    var infoSavedVisible by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        val currentUser = user
        if (currentUser != null) {
            username = currentUser.nickname ?: currentUser.username ?: "Người dùng"
            avatarUri = currentUser.avatarUriString?.let { Uri.parse(it) }
        } else {
            username = "Người dùng"
            avatarUri = null
        }
    }

    LaunchedEffect(userId) {
        workoutViewModel.loadWeeklySessions(userId)
    }

    val weeklySessions by workoutViewModel.weeklySessions.collectAsState(initial = emptyList())

    val weeklySchedule = remember(weeklySessions) {
        val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        days.mapIndexed { index, day ->
            day to weeklySessions.any { it.day == (index + 1) && it.completed }
        }.toMap()
    }

    if (infoSavedVisible) {
        InfoSavedScreen(
            onBack = { infoSavedVisible = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2),
                            Color(0xFF82B1FF)
                        )
                    )
                )
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF2A6EDB), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val points = listOf(
                    Offset(width * 0.15f, height * 0.2f),
                    Offset(width * 0.5f, height * 0.1f),
                    Offset(width * 0.8f, height * 0.25f),
                    Offset(width * 0.3f, height * 0.5f),
                    Offset(width * 0.7f, height * 0.55f),
                    Offset(width * 0.85f, height * 0.4f)
                )
                points.forEach { point ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = 12f,
                        center = point
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.TopStart)) {
                IconButton(onClick = { expandedMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Đăng xuất") },
                        onClick = {
                            expandedMenu = false
                            userViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Thông tin cá nhân + avatar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { showEditDialog = true }
                    ) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Center)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                                contentDescription = "Avatar mặc định",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Center)
                            )
                        }
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Chỉnh sửa avatar",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        text = "Hello, $username",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    )
                }

                // 3 nút BMI, Nước, Calo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { navController.navigate("bmi") },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Tính BMI",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tính BMI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { navController.navigate("water") },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Nước",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nước",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { navController.navigate("calories_daily_summary") },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A56DB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Calo",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Calo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Lịch tập luyện tuần
                WeeklyWorkoutSchedule(
                    weeklySchedule = weeklySchedule,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        if (showEditDialog) {
            var tempUsername by remember(user) { mutableStateOf(username) }
            var tempAvatarUri by remember(user) { mutableStateOf(avatarUri) }
            val context = LocalContext.current

            val dialogLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    coroutineScope.launch {
                        val path = copyUriToInternalStorage(
                            context,
                            uri,
                            "avatar_${System.currentTimeMillis()}.jpg"
                        )
                        if (path != null) {
                            tempAvatarUri = Uri.fromFile(File(path))
                        }
                    }
                }
            }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Chỉnh sửa hồ sơ") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempUsername,
                            onValueChange = { tempUsername = it },
                            label = { Text("Tên người dùng") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chọn ảnh đại diện:")
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { dialogLauncher.launch("image/*") }
                        ) {
                            if (tempAvatarUri != null) {
                                AsyncImage(
                                    model = tempAvatarUri,
                                    contentDescription = "Ảnh đại diện",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_avatar_placeholder),
                                    contentDescription = "Ảnh đại diện mặc định",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        username = tempUsername
                        avatarUri = tempAvatarUri
                        showEditDialog = false
                        userViewModel.updateProfile(
                            nickname = tempUsername,
                            avatarUriString = tempAvatarUri?.toString()
                        )
                    }) {
                        Text("Lưu")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}
@Composable
fun WeeklyWorkoutSchedule(
    weeklySchedule: Map<String, Boolean>, // key: ngày ("T2", "T3", ...), value: đã tập hay chưa
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A90E2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Lịch tập luyện tuần này",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

                days.forEach { day ->
                    val isWorkout = weeklySchedule[day] ?: false
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            day,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = if (isWorkout) Color(0xFF2ECC71) else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isWorkout) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Đã tập",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun InfoSavedScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Thông tin đã được lưu!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quay lại")
        }
    }
}
