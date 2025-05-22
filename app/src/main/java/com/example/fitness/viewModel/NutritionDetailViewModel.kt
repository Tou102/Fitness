import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.dao.NutritionDetailDao

import com.example.fitness.entity.NutritionDetail
import kotlinx.coroutines.launch

class NutritionDetailViewModel(private val dao: NutritionDetailDao) : ViewModel() {
    val allNutritionDetails: LiveData<List<NutritionDetail>> = dao.getAllNutritionDetails()

    fun addNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            dao.insert(nutritionDetail)
        }
    }

    fun updateNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            dao.update(nutritionDetail)
        }
    }

    fun deleteNutritionDetail(nutritionDetail: NutritionDetail) {
        viewModelScope.launch {
            dao.delete(nutritionDetail)
        }
    }
}