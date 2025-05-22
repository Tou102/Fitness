package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.entity.Exercise
import com.example.fitness.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises
    fun loadExercises() {
        viewModelScope.launch {
            val list = repository.getAllExercises()
            _exercises.value = list
        }
    }

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.insert(exercise)
            loadExercises()  // tải lại dữ liệu sau khi thêm
        }
    }
    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.update(exercise)
            loadExercises()  // tải lại dữ liệu sau khi sửa
        }
    }
    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.delete(exercise)
            loadExercises()  // tải lại dữ liệu sau khi xóa
        }
    }
}