package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.entity.WaterIntakeRecordEntity
import com.example.fitness.repository.WaterIntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WaterIntakeViewModel(private val repository: WaterIntakeRepository) : ViewModel() {
    private val _records = MutableStateFlow<List<WaterIntakeRecordEntity>>(emptyList())
    val records = _records.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            repository.getAllRecords().collect { list ->
                _records.value = list
            }
        }
    }


    fun addRecord(amountMl: Int) {
        val record = WaterIntakeRecordEntity(amountMl = amountMl, timestamp = System.currentTimeMillis())
        viewModelScope.launch {
            repository.insert(record)
            loadRecords() // Cập nhật lại dữ liệu sau khi thêm
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            loadRecords() // Cập nhật lại dữ liệu sau khi xóa
        }
    }
}


