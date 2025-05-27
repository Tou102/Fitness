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
import com.example.fitness.viewModel.ExerciseViewModel

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember

import androidx.navigation.NavHostController
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fitness.entity.Exercise

import pl.droidsonroids.gif.GifImageView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect


import androidx.compose.runtime.getValue

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fitness.NutritionItem
import com.example.fitness.R



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
fun ExerciseGroupScreen(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel,
    groupName: String,
    title: String,
    description: String,
    benefits: List<String>
) {
    val exercises by exerciseViewModel.exercises.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(Unit) {
        exerciseViewModel.loadExercises()
    }

    val filteredExercises = exercises.filter { it.group == groupName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Lợi ích của bài tập:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                benefits.forEach { benefit ->
                    Text(text = "- $benefit", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredExercises.forEach { exercise ->
            ExerciseItem(
                exercise = exercise,
                onEdit = {
                    editingExercise = it
                    showDialog = true
                },
                onDelete = { exerciseViewModel.deleteExercise(it) },
                onStartExercise = {
                    navController.navigate("exercise_camera/${it.id}") // 👈 điều hướng sang camera screen
                }
            )

        }

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
            group = groupName,
            onDismiss = { showDialog = false },
            onSave = { exercise ->
                val updatedExercise = exercise.copy(group = groupName)
                if (exercise.id == 0) exerciseViewModel.addExercise(updatedExercise)
                else exerciseViewModel.updateExercise(updatedExercise)
                showDialog = false
            }
        )
    }
}


@Composable
fun FullBody(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "FullBody",
        title = "Bài tập full body",
        description = "Bài tập toàn thân phù hợp cho mọi cấp độ, giúp tăng sức mạnh và sự dẻo dai.",
        benefits = listOf(
            "Tăng cường sức mạnh cơ bắp toàn diện",
            "Cải thiện sức bền và khả năng vận động",
            "Đốt cháy calo hiệu quả",
            "Hỗ trợ giảm cân và duy trì vóc dáng",
            "Tăng cường sức khỏe tim mạch",
            "Phù hợp với mọi trình độ tập luyện"
        )
    )
}

@Composable
fun Abs(navController: NavHostController, exerciseViewModel: ExerciseViewModel) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Abs",
        title = "Bài tập cơ bụng (Abs)",
        description = "Bài tập giúp tăng cường sức mạnh cơ bụng, hỗ trợ giữ thăng bằng và cải thiện vóc dáng.",
        benefits = listOf(
            "Tăng sức mạnh vùng bụng",
            "Cải thiện tư thế và thăng bằng",
            "Hỗ trợ giảm mỡ vùng bụng",
            "Giúp cơ thể săn chắc hơn"
        )
    )
}

@Composable
fun Chest(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Chest",
        title = "Bài tập ngực (Chest)",
        description = "Các bài tập tăng cường sức mạnh cơ ngực, giúp phát triển cơ bắp và cải thiện sức mạnh tổng thể.",
        benefits = listOf(
            "Tăng cường sức mạnh cơ ngực",
            "Cải thiện tư thế và sức khỏe tổng thể",
            "Phát triển cơ bắp săn chắc",
            "Hỗ trợ thực hiện các hoạt động thể chất khác"
        )
    )
}

@Composable
fun Arm(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel
) {
    ExerciseGroupScreen(
        navController = navController,
        exerciseViewModel = exerciseViewModel,
        groupName = "Arm",
        title = "Bài tập tay (Arm)",
        description = "Các bài tập giúp phát triển cơ tay, tăng sức mạnh và sự săn chắc.",
        benefits = listOf(
            "Tăng sức mạnh cơ tay",
            "Cải thiện sức bền và độ săn chắc",
            "Hỗ trợ vận động và nâng đỡ",
            "Giúp phát triển vóc dáng cân đối"
        )
    )
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
    onDelete: (Exercise) -> Unit,
    onStartExercise: (Exercise) -> Unit
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
                Button(onClick = { onStartExercise(exercise) }) {
                    Text("Tập bài này")
                }
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
    group: String,
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(exercise?.name ?: "") }
    var description by remember { mutableStateOf(exercise?.description ?: "") }
    var repText by remember { mutableStateOf(exercise?.rep?.toString() ?: "") }
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
                    value = repText,
                    onValueChange = {
                        repText = it.filter { c -> c.isDigit() }
                    },
                    label = { Text("Số lần (rep)") },
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
                    val rep = repText.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        onSave(
                            Exercise(
                                id = exercise?.id ?: 0,
                                name = name,
                                description = description,
                                rep = rep,
                                gifRes = selectedGif,
                                group = group
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









