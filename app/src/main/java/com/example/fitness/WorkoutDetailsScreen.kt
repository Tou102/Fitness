package com.example.fitness

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember

import androidx.navigation.NavHostController
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fitness.entity.Exercise
import com.example.fitness.viewModel.ExerciseViewModel
import pl.droidsonroids.gif.GifImageView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

@Composable
fun WorkoutDetailScreen(
    navController: NavHostController,
    workoutType: String?,
    content: String,
    additionalContent: @Composable () -> Unit,
    onAdd: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Bài tập: $workoutType",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            additionalContent()
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Quay về")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onAdd,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Thêm")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Xóa")
                }
            }
        }
    }
}

@Composable
fun BaiTap18den29(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel
) {
    val exercises by exerciseViewModel.exercises.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(Unit) {
        exerciseViewModel.loadExercises()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tuổi: 18-29",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Bài tập phù hợp với độ tuổi này bao gồm các bài tập thể lực cơ bản và các bài tập tăng cường sức bền.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Các bài tập thể lực cơ bản", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Squats, Push-ups, Lunges...")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị danh sách bài tập lấy từ ViewModel (Room)
        exercises.forEach { exercise ->
            ExerciseItem(
                exercise = exercise,
                onEdit = {
                    editingExercise = it
                    showDialog = true
                },
                onDelete = { exerciseViewModel.deleteExercise(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GifImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 16.dp),
            resId = R.drawable.exercise_2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                editingExercise = null
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm bài tập")
        }
    }

    if (showDialog) {
        ExerciseDialog(
            exercise = editingExercise,
            onDismiss = { showDialog = false },
            onSave = {
                if (it.id == 0) exerciseViewModel.addExercise(it)
                else exerciseViewModel.updateExercise(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun GifImage(modifier: Modifier = Modifier, resId: Int) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GifImageView(context).apply {
                setImageResource(resId)
            }
        }
    )
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onEdit: (Exercise) -> Unit,
    onDelete: (Exercise) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = exercise.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị GIF ngay dưới mô tả
            GifImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                resId = exercise.gifRes ?: R.drawable.exercise_1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onEdit(exercise) }) {
                    Text("Sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(exercise) }) {
                    Text("Xóa")
                }
            }
        }
    }
}

@Composable
fun ExerciseDialog(
    exercise: Exercise?,
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(exercise?.name ?: "") }
    var description by remember { mutableStateOf(exercise?.description ?: "") }
    var durationText by remember { mutableStateOf(exercise?.duration?.toString() ?: "") }
    var selectedGif by remember { mutableStateOf(exercise?.gifRes ?: R.drawable.exercise_1) }

    val gifOptions = listOf(
        R.drawable.exercise_1,
        R.drawable.exercise_2,
        R.drawable.exercise_3,
        R.drawable.exercise_4,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exercise == null) "Thêm bài tập" else "Sửa bài tập") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên bài tập") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") }
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = {
                        durationText = it.filter { c -> c.isDigit() }
                    },
                    label = { Text("Thời gian (phút)") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Chọn GIF:")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gifOptions.forEach { gifRes ->
                        GifImage(
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = if (selectedGif == gifRes) 3.dp else 1.dp,
                                    color = if (selectedGif == gifRes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                                .clickable { selectedGif = gifRes },
                            resId = gifRes
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        onSave(
                            Exercise(
                                id = exercise?.id ?: 0,
                                name = name,
                                description = description,
                                duration = duration,
                                gifRes = selectedGif
                            )
                        )
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}




