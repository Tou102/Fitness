// CaloriesViewModel.kt
package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.entity.CaloriesRecordEntity
import com.example.fitness.repository.CaloriesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CaloriesViewModel(private val repository: CaloriesRepository) : ViewModel() {

    private val _records = MutableStateFlow<List<CaloriesRecordEntity>>(emptyList())
    val records: StateFlow<List<CaloriesRecordEntity>> = _records

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _records.value = repository.getAllRecords()
        }
    }

    fun addRecord(record: CaloriesRecordEntity) {
        viewModelScope.launch {
            repository.insert(record)
            loadRecords()
        }
    }

    fun deleteRecord(record: CaloriesRecordEntity) {
        viewModelScope.launch {
            repository.delete(record)
            loadRecords()
        }
    }
}
