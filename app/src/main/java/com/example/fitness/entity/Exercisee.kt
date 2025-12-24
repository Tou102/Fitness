package com.example.fitness.entity

import android.content.Context
import com.example.fitness.BodyPart
import com.example.fitness.R
import com.example.fitness.dao.UserExerciseDao
import com.example.fitness.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 1. Data Class
enum class ExerciseType { ABS, ARM, CHEST, LEG }
enum class MeasureUnit { TIME, REPS }

data class Exercisee(
    val id: Int,
    val name: String,
    val description: String,
    val imageRes: Int,
    val type: ExerciseType,
    val unit: MeasureUnit,
    var value: Int, // Thời gian hoặc Số lần (có thể sửa)
    val bodyPart: BodyPart,
    val videoId: String,   // ID Youtube
    val muscleImageRes: Int
)

// 2. Repository (Kho dữ liệu thông minh)
object AppRepository {

    var currentUserKey: String = "guest"
    private var exerciseDao: UserExerciseDao? = null

    // Bản đồ Swap: Key = ID Gốc (Slot), Value = ID Bài Đang Tập (Active)
    private val swapMap = mutableMapOf<Int, Int>()
    // Bản đồ Ngược: Key = ID Bài Đang Tập, Value = ID Gốc
    private val reverseSwapMap = mutableMapOf<Int, Int>()

    fun initialize(context: Context) {
        val db = AppDatabase.getDatabase(context)
        exerciseDao = db.userExerciseDao()
    }

    // A. KHO GỐC (MASTER) - Đã tách ID riêng cho từng Level để tránh lỗi trùng lặp
    private val MASTER_LIBRARY = listOf(
        // === LEVEL BẮT ĐẦU (ID: 1 -> 99) ===
        Exercisee(1, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(2, "Chạm gót", "...", R.drawable.chamgot1_gif, ExerciseType.ABS, MeasureUnit.REPS, 26,
            BodyPart.BUNG, "9bR-elyolBQ",R.drawable.chamgot),
        Exercisee(3, "Plank", "...", android.R.drawable.ic_menu_gallery, ExerciseType.ABS, MeasureUnit.TIME, 45,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.plank),
        Exercisee(4, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "wQq3ybaLZeA", R.drawable.leonui),

        // === LEVEL TRUNG BÌNH (ID: 100 -> 199) ===
        // Copy bài tập nhưng dùng ID mới và tăng độ khó mặc định
        Exercisee(101, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.ABS, MeasureUnit.TIME, 45, // Tăng lên 45s
            BodyPart.BUNG,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(102, "Chạm gót", "...", R.drawable.chamgot1_gif, ExerciseType.ABS, MeasureUnit.REPS, 30, // Tăng lên 30 cái
            BodyPart.BUNG, "9bR-elyolBQ",R.drawable.chamgot),
        Exercisee(103, "Plank", "...", android.R.drawable.ic_menu_gallery, ExerciseType.ABS, MeasureUnit.TIME, 60, // Tăng lên 60s
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.plank),

        // === BÀI TẬP TAY/KHÁC (Dùng để Swap hoặc Plan Tay) ===
        Exercisee(10, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.ARM, MeasureUnit.REPS, 12,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(11, "Tay sau ghế", "...", android.R.drawable.ic_menu_gallery, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "JhX1nBnirNw", R.drawable.taysaughe)
    )

    private val userStorage = mutableMapOf<String, MutableList<Exercisee>>()

    // 1. LOAD DỮ LIỆU
    fun loadUserData() {
        if (currentUserKey == "guest") return

        swapMap.clear()
        reverseSwapMap.clear()

        // Tạo bản sao từ Master Library
        val myList = MASTER_LIBRARY.map { it.copy() }.toMutableList()

        runBlocking {
            try {
                val customData = exerciseDao?.getCustomExercisesByUser(currentUserKey)
                customData?.forEach { custom ->
                    // A. Cập nhật Time/Reps cho bài ACTIVE
                    val item = myList.find { it.id == custom.activeExerciseId }
                    item?.value = custom.customValue

                    // B. Cập nhật Swap Map
                    swapMap[custom.exerciseId] = custom.activeExerciseId
                    reverseSwapMap[custom.activeExerciseId] = custom.exerciseId
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        userStorage[currentUserKey] = myList
    }

    // 2. LẤY BÀI TẬP
    fun getExerciseById(id: Int): Exercisee? {
        if (!userStorage.containsKey(currentUserKey)) loadUserData()

        // Tìm trong kho -> Trả về một bản copy
        return userStorage[currentUserKey]?.find { it.id == id }?.copy()
    }
    // 3. LẤY DANH SÁCH GÓI TẬP (Đã Fix: Dùng ID riêng và .copy())
    fun getExercisesForPlan(planId: Int): List<Exercisee> {
        if (!userStorage.containsKey(currentUserKey)) loadUserData()

        // Hàm phụ: Tìm bài thực tế (nếu đã Swap)
        fun getActive(originalId: Int): Int = swapMap[originalId] ?: originalId

        // Hàm phụ: Lấy bài và COPY (An toàn bộ nhớ)
        fun getExCopy(slotId: Int): Exercisee? {
            val realId = getActive(slotId)
            return getExerciseById(realId)?.copy()
        }

        return when (planId) {
            // Plan 1: Bụng Bắt Đầu -> Dùng bộ ID 1, 2, 4
            1 -> listOfNotNull(getExCopy(1), getExCopy(2), getExCopy(4))

            // Plan 2: Bụng Trung Bình -> Dùng bộ ID 101, 102, 103 (Độc lập hoàn toàn với Plan 1)
            2 -> listOfNotNull(getExCopy(101), getExCopy(102), getExCopy(103))

            // Plan 4: Tay -> Dùng bộ ID 10, 11
            4 -> listOfNotNull(getExCopy(10), getExCopy(11))

            else -> emptyList()
        }
    }

    // Lấy danh sách để Swap (Loại trừ bài chính nó)
    fun getSwapCandidates(currentEx: Exercisee): List<Exercisee> {
        if (!userStorage.containsKey(currentUserKey)) loadUserData()
        // Chỉ lấy những bài cùng nhóm cơ và khác ID
        return userStorage[currentUserKey]!!.filter {
            it.bodyPart == currentEx.bodyPart && it.id != currentEx.id
        }
    }

    // 4. LƯU THAY ĐỔI (Đã chuẩn logic DB)
    fun saveUserChanges(exerciseIdInput: Int, newValue: Int, newActiveId: Int? = null) {
        val activeIdToSave = newActiveId ?: exerciseIdInput

        // Cập nhật RAM (Chỉ cần update vào userStorage để lần sau load lại vẫn có)
        val userList = userStorage[currentUserKey]
        userList?.find { it.id == activeIdToSave }?.value = newValue

        // Cập nhật Database
        CoroutineScope(Dispatchers.IO).launch {
            // Tìm Slot gốc
            val originalSlotId = reverseSwapMap[exerciseIdInput] ?: exerciseIdInput

            // Cập nhật Map
            swapMap[originalSlotId] = activeIdToSave
            reverseSwapMap[activeIdToSave] = originalSlotId

            // Tìm bản ghi cũ
            val existing = exerciseDao?.findExisting(currentUserKey, originalSlotId)

            val record = com.example.fitness.entity.UserExercise(
                id = existing?.id ?: 0,
                username = currentUserKey,
                exerciseId = originalSlotId, // Key chính là Slot Gốc (VD: 1 hoặc 101)
                customValue = newValue,
                activeExerciseId = activeIdToSave
            )
            exerciseDao?.insertOrUpdate(record)
        }
    }

    // 5. RESET
    fun resetToDefault(exerciseIdInput: Int): Exercisee? {
        val originalSlotId = reverseSwapMap[exerciseIdInput] ?: exerciseIdInput

        val userList = userStorage[currentUserKey]
        val originalLibItem = MASTER_LIBRARY.find { it.id == originalSlotId }

        // Reset giá trị RAM
        userList?.find { it.id == originalSlotId }?.value = originalLibItem?.value ?: 30

        // Xóa Map
        val currentActive = swapMap[originalSlotId]
        if (currentActive != null) reverseSwapMap.remove(currentActive)
        swapMap.remove(originalSlotId)

        // Xóa DB
        CoroutineScope(Dispatchers.IO).launch {
            exerciseDao?.deleteUserExercise(currentUserKey, originalSlotId)
        }

        // Trả về bản copy mới nhất của bài gốc
        return userList?.find { it.id == originalSlotId }?.copy()
    }
}