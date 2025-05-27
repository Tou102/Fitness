package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.entity.NutritionDetail
import com.example.fitness.repository.NutritionDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NutritionDetailViewModel(private val repository: NutritionDetailRepository) : ViewModel() {
    private val _nutritionDetails = MutableStateFlow<List<NutritionDetail>>(emptyList())
    val nutritionDetails: StateFlow<List<NutritionDetail>> = _nutritionDetails

    private val _groupNutritionDetails = MutableStateFlow<List<NutritionDetail>>(emptyList())
    val groupNutritionDetails: StateFlow<List<NutritionDetail>> = _groupNutritionDetails

    fun loadNutritionDetails() {
        viewModelScope.launch {
            val list = repository.getAllNutritionDetails()
            _nutritionDetails.value = list
        }
    }

    fun loadNutritionDetailsByGroup(groupName: String) {
        viewModelScope.launch {
            _groupNutritionDetails.value = repository.getNutritionDetailsByGroup(groupName)
            loadNutritionDetails()
        }
    }

    fun addNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            repository.insert(nutritionDetail)
            loadNutritionDetails() // Tải lại dữ liệu sau khi thêm

        }
    }

    fun updateNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            repository.update(nutritionDetail)
            loadNutritionDetails() // Tải lại dữ liệu sau khi sửa
        }
    }

    fun deleteNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            repository.delete(nutritionDetail)
            loadNutritionDetails() // Tải lại dữ liệu sau khi xóa
            loadNutritionDetails()
        }
    }
}