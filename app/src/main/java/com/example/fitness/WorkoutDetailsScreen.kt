//package com.example.fitness
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.runtime.remember
//import androidx.navigation.NavHostController
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import coil.compose.AsyncImage
//import com.example.fitness.entity.Exercise
//import com.example.fitness.R
//import com.example.fitness.viewModel.ExerciseViewModel
//import kotlinx.coroutines.launch
//import pl.droidsonroids.gif.GifImageView
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.LaunchedEffect
//
//@Composable
//fun WorkoutDetailScreen(
//    navController: NavHostController,
//    workoutType: String?,
//    content: String,
//    additionalContent: @Composable () -> Unit,
//    isAdmin: Boolean,
//    onAdd: () -> Unit = {},
//    onEdit: () -> Unit = {},
//    onDelete: () -> Unit = {}
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ) {
//            Text(
//                text = "Bài tập: $workoutType",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.CenterHorizontally)
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//            Text(
//                text = content,
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            additionalContent()
//        }
//
//        Column {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = { navController.popBackStack() },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Quay về")
//                }
//                if (isAdmin) {
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = onAdd,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Thêm")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = onEdit,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Sửa")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = onDelete,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Xóa")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ExerciseGroupScreen(
//    navController: NavHostController,
//    exerciseViewModel: ExerciseViewModel,
//    isAdmin: Boolean,
//    groupName: String,
//    title: String,
//    description: String,
//    benefits: List<String>
//) {
//    val exercises by exerciseViewModel.exercises.collectAsState()
//
//    var showDialog by remember { mutableStateOf(false) }
//    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
//
//    LaunchedEffect(Unit) {
//        exerciseViewModel.loadExercises()
//    }
//
//    val filteredExercises = exercises.filter { it.group == groupName }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//        Text(text = title, style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(12.dp))
//        Text(text = description, style = MaterialTheme.typography.bodyLarge)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(text = "Lợi ích của bài tập:", style = MaterialTheme.typography.titleMedium)
//                Spacer(modifier = Modifier.height(8.dp))
//                benefits.forEach { benefit ->
//                    Text(text = "- $benefit", style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        filteredExercises.forEach { exercise ->
//            ExerciseItem(
//                exercise = exercise,
//                isAdmin = isAdmin,
//                onEdit = {
//                    if (isAdmin) {
//                        editingExercise = it
//                        showDialog = true
//                    }
//                },
//                onDelete = {
//                    if (isAdmin) {
//                        exerciseViewModel.deleteExercise(it)
//                    }
//                },
//                onStartExercise = {
//                    navController.navigate("exercise_camera/${it.id}")
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isAdmin) {
//            Button(
//                onClick = {
//                    editingExercise = null
//                    showDialog = true
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Thêm bài tập")
//            }
//        }
//    }
//
//    if (showDialog) {
//        ExerciseDialog(
//            exercise = editingExercise,
//            group = groupName,
//            onDismiss = { showDialog = false },
//            onSave = { exercise ->
//                val updatedExercise = exercise.copy(group = groupName)
//                if (exercise.id == 0) exerciseViewModel.addExercise(updatedExercise)
//                else exerciseViewModel.updateExercise(updatedExercise)
//                showDialog = false
//            }
//        )
//    }
//}
//
//@Composable
//fun ExerciseItem(
//    exercise: Exercise,
//    isAdmin: Boolean,
//    onEdit: (Exercise) -> Unit,
//    onDelete: (Exercise) -> Unit,
//    onStartExercise: (Exercise) -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = exercise.name,
//                style = MaterialTheme.typography.titleMedium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = exercise.description ?: "",
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            GifImage(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp).width(300.dp),
//
//                resId = exercise.gifRes ?: R.drawable.exercise_1
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Row(
//                horizontalArrangement = Arrangement.End,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Button(onClick = { onStartExercise(exercise) }) {
//                    Text("Tập bài này")
//                }
//                if (isAdmin) {
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(onClick = { onEdit(exercise) }) {
//                        Text("Sửa")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(onClick = { onDelete(exercise) }) {
//                        Text("Xóa")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun GifImage(modifier: Modifier = Modifier, resId: Int) {
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            GifImageView(context).apply {
//                setImageResource(resId)
//            }
//        }
//    )
//}
//
//@Composable
//fun ExerciseDialog(
//    exercise: Exercise?,
//    group: String,
//    onDismiss: () -> Unit,
//    onSave: (Exercise) -> Unit
//) {
//    var name by remember { mutableStateOf(exercise?.name ?: "") }
//    var description by remember { mutableStateOf(exercise?.description ?: "") }
//    var repText by remember { mutableStateOf(exercise?.rep?.toString() ?: "") }
//    var selectedGif by remember { mutableStateOf(exercise?.gifRes ?: R.drawable.exercise_1) }
//
//    val gifOptions = listOf(
//        R.drawable.exercise_1,
//        R.drawable.exercise_2,
//        R.drawable.exercise_3,
////        R.drawable.exercise_4,
//        R.drawable.jumpingjack,
//    )
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(if (exercise == null) "Thêm bài tập" else "Sửa bài tập") },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Tên bài tập") },
//                    singleLine = true
//                )
//                OutlinedTextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Mô tả") }
//                )
//                OutlinedTextField(
//                    value = repText,
//                    onValueChange = {
//                        repText = it.filter { c -> c.isDigit() }
//                    },
//                    label = { Text("Số lần (rep)") },
//                    singleLine = true
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                Text(text = "Chọn GIF:")
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    gifOptions.forEach { gifRes ->
//                        GifImage(
//                            modifier = Modifier
//                                .size(60.dp)
//                                .border(
//                                    width = if (selectedGif == gifRes) 3.dp else 1.dp,
//                                    color = if (selectedGif == gifRes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
//                                )
//                                .clickable { selectedGif = gifRes },
//                            resId = gifRes
//                        )
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    val rep = repText.toIntOrNull() ?: 0
//                    if (name.isNotBlank()) {
//                        onSave(
//                            Exercise(
//                                id = exercise?.id ?: 0,
//                                name = name,
//                                description = description,
//                                rep = rep,
//                                gifRes = selectedGif,
//                                group = group
//                            )
//                        )
//                    }
//                }
//            ) {
//                Text("Lưu")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Hủy")
//            }
//        }
//    )
//}
