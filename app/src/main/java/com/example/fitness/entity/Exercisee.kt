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
    var value: Int, // Thời gian hoặc Số lần
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
        // ============================================================
        // === 1. BÀI TẬP BỤNG (ABS)
        // ============================================================
        // === BỤNG BẮT ĐẦU (ID: 1 -> 99) ===
        Exercisee(1, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG,"dQw4w9WgXcQ",R.drawable.batnhay),

        Exercisee(2, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.batnhay2),

        Exercisee(3, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "wQq3ybaLZeA", R.drawable.leonui),

        Exercisee(4, "Gập bụng", "...", R.drawable.gapbung_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "wQq3ybaLZeA", R.drawable.gapbung),

        Exercisee(5, "Nâng chân", "...", R.drawable.nangchan_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangchan),

        Exercisee(6, "Đá chân cắt kéo", "...", R.drawable.dachancatkeo_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.dachancatkeo),

        Exercisee(7, "Nâng cầu mông", "...", R.drawable.nangcaumong_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangcaumong),

        Exercisee(8, "Chạm gót", "...", R.drawable.chamgot1_gif, ExerciseType.ABS, MeasureUnit.REPS, 26,
            BodyPart.BUNG, "9bR-elyolBQ",R.drawable.chamgot),

        Exercisee(9, "Bổ củi", "...", R.drawable.bocui_gif, ExerciseType.ABS, MeasureUnit.REPS, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.bocui),

        Exercisee(10, "Plank", "...", R.drawable.plank_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.plank),

        Exercisee(11, "Plank nghiêng - Trái", "...", R.drawable.planknghieng_giftrai, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),

        Exercisee(12, "Plank nghiêng - Phải", "...", R.drawable.planknghiengphai_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),

        Exercisee(13, "Tư thế Rắn Hổ Mang", "...", R.drawable.gianlung_bung_gif, ExerciseType.ABS, MeasureUnit.TIME, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gianlung_bung),

        // === BỤNG TRUNG BÌNH (ID: 100 -> 199) ===
        // Copy bài tập nhưng dùng ID mới và tăng độ khó mặc định
        Exercisee(101, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(102, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.ABS, MeasureUnit.TIME, 30, // Đổi sang Time cho nặng hơn
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.batnhay2),
        Exercisee(103, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.ABS, MeasureUnit.REPS, 10,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(104, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "wQq3ybaLZeA", R.drawable.leonui),

        Exercisee(105, "Nâng chân", "...", R.drawable.nangchan_gif, ExerciseType.ABS, MeasureUnit.TIME, 30, // Đổi sang Time
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangchan),
        Exercisee(106, "Đá chân cắt kéo", "...", R.drawable.dachancatkeo_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.dachancatkeo),

        Exercisee(107, "Gập bụng chéo", "...", R.drawable.gapbungcheo_gif, ExerciseType.ABS, MeasureUnit.REPS, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gapbungcheo),
        Exercisee(108, "Gập bụng", "...", R.drawable.gapbung_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gapbung),
        Exercisee(109, "Chạm gót", "...", R.drawable.chamgot1_gif, ExerciseType.ABS, MeasureUnit.REPS, 30,
            BodyPart.BUNG, "9bR-elyolBQ",R.drawable.chamgot),
        Exercisee(110, "Bổ củi", "...", R.drawable.bocui_gif, ExerciseType.ABS, MeasureUnit.REPS, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.bocui),

        Exercisee(111, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.ABS, MeasureUnit.REPS, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(112, "Plank", "...", R.drawable.plank_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.plank),
        Exercisee(113, "Plank nghiêng - Trái", "...", R.drawable.planknghieng_giftrai, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),
        Exercisee(114, "Plank nghiêng - Phải", "...", R.drawable.planknghiengphai_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),

        Exercisee(115, "Nâng cầu mông", "...", R.drawable.nangcaumong_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangcaumong),
        Exercisee(116, "Tư thế Rắn Hổ Mang", "...", R.drawable.gianlung_bung_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gianlung_bung),

        //BỤNG NÂNG CAO
        Exercisee(201, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(202, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.ABS, MeasureUnit.TIME, 40, // Đổi sang Time cho nặng hơn
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.batnhay2),
        Exercisee(203, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.ABS, MeasureUnit.REPS, 10,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(204, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "wQq3ybaLZeA", R.drawable.leonui),

        Exercisee(205, "Nâng chân", "...", R.drawable.nangchan_gif, ExerciseType.ABS, MeasureUnit.TIME, 40, // Đổi sang Time
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangchan),
        Exercisee(206, "Đá chân cắt kéo", "...", R.drawable.dachancatkeo_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.dachancatkeo),

        Exercisee(207, "Gập bụng chéo", "...", R.drawable.gapbungcheo_gif, ExerciseType.ABS, MeasureUnit.REPS, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gapbungcheo),
        Exercisee(208, "Gập bụng", "...", R.drawable.gapbung_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gapbung),
        Exercisee(209, "Chạm gót", "...", R.drawable.chamgot1_gif, ExerciseType.ABS, MeasureUnit.REPS, 30,
            BodyPart.BUNG, "9bR-elyolBQ",R.drawable.chamgot),
        Exercisee(210, "Bổ củi", "...", R.drawable.bocui_gif, ExerciseType.ABS, MeasureUnit.REPS, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.bocui),

        Exercisee(211, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.ABS, MeasureUnit.REPS, 20,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(212, "Plank", "...", R.drawable.plank_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.plank),
        Exercisee(213, "Plank nghiêng - Trái", "...", R.drawable.planknghieng_giftrai, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),
        Exercisee(214, "Plank nghiêng - Phải", "...", R.drawable.planknghiengphai_gif, ExerciseType.ABS, MeasureUnit.TIME, 30,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.planknghiengtrai),

        Exercisee(215, "Nâng cầu mông", "...", R.drawable.nangcaumong_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.nangcaumong),
        Exercisee(216, "Tư thế Rắn Hổ Mang", "...", R.drawable.gianlung_bung_gif, ExerciseType.ABS, MeasureUnit.TIME, 40,
            BodyPart.BUNG, "Fcbw82ykBvY", R.drawable.gianlung_bung),

        // ============================================================
        // === 2. BÀI TẬP TAY (ARM)
        // ============================================================
        // === BÀI TẬP TAY DỄ -- 301 --> 320
        Exercisee(301, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(302, "Tay sau ghế", "...", R.drawable.taysaughe_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "JhX1nBnirNw", R.drawable.taysaughe),
        Exercisee(303, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(304, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(305, "Đấm", "...", R.drawable.punch_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(306, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(307, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(308, "Nâng tạ hai bên sang trước", "...", R.drawable.nangtahaibensangtruoc_gif, ExerciseType.ARM, MeasureUnit.REPS, 10,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.nangtahaibensangtruoc),


        // === BÀI TẬP TAY TRUNG BÌNH -- 321 --> 340
        Exercisee(321, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(322, "Tay sau ghế", "...", R.drawable.taysaughe_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "JhX1nBnirNw", R.drawable.taysaughe),
        Exercisee(323, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(324, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(325, "Đấm", "...", R.drawable.punch_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(326, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(327, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(328, "Nâng tạ hai bên sang trước", "...", R.drawable.nangtahaibensangtruoc_gif, ExerciseType.ARM, MeasureUnit.REPS, 15,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.nangtahaibensangtruoc),

        // === BÀI TẬP TAY NÂNG CAO -- 341 - 360
        Exercisee(341, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(342, "Tay sau ghế", "...", R.drawable.taysaughe_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "JhX1nBnirNw", R.drawable.taysaughe),
        Exercisee(343, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(344, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(345, "Đấm", "...", R.drawable.punch_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(346, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(347, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(348, "Nâng tạ hai bên sang trước", "...", R.drawable.nangtahaibensangtruoc_gif, ExerciseType.ARM, MeasureUnit.REPS, 20,
            BodyPart.TAY, "WDIpL0pjun0", R.drawable.nangtahaibensangtruoc),

        // ============================================================
        // === 3. BÀI TẬP NGỰC (CHEST)
        // ============================================================
        // === BÀI TẬP NGỰC DỄ -- 361 --> 380
        Exercisee(361, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(362, "Bay tạ", "...", R.drawable.bayta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "JhX1nBnirNw", R.drawable.bayta),
        Exercisee(363, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(364, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(365, "Đấm", "...", R.drawable.punch_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20,
            BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(366, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(367, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(368, "Đẩy ngực tạ đôi", "...", R.drawable.daynguc_gif, ExerciseType.CHEST, MeasureUnit.REPS, 10,
            BodyPart.NGUC, "WDIpL0pjun0", R.drawable.daynguc),

        // --- Ngực Trung Bình (Plan 8) -- 381 --> 400
        Exercisee(381, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(382, "Bay tạ", "...", R.drawable.bayta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "JhX1nBnirNw", R.drawable.bayta),
        Exercisee(383, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(384, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(385, "Đấm", "...", R.drawable.punch_gif, ExerciseType.CHEST, MeasureUnit.REPS, 30, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(386, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(387, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(388, "Đẩy ngực tạ đôi", "...", R.drawable.daynguc_gif, ExerciseType.CHEST, MeasureUnit.REPS, 15, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.daynguc),

        // --- Ngực Nâng Cao (Plan 9) - 401 --> 420
        Exercisee(401, "Hít đất", "...", R.drawable.hitdat_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.pushup),
        Exercisee(402, "Bay tạ", "...", R.drawable.bayta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "JhX1nBnirNw", R.drawable.bayta),
        Exercisee(403, "Sâu đo", "...", R.drawable.saudo_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.saudo),
        Exercisee(404, "Plank kéo tạ", "...", R.drawable.keotaplank_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.keotaplank),
        Exercisee(405, "Đấm", "...", R.drawable.punch_gif, ExerciseType.CHEST, MeasureUnit.REPS, 40, BodyPart.NGUC, "Fcbw82ykBvY", R.drawable.punch),
        Exercisee(406, "Hít đất so le", "...", R.drawable.hitdatsole_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.hitdatsole),
        Exercisee(407, "Cuốn tạ", "...", R.drawable.cuonta_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.cuonta),
        Exercisee(408, "Đẩy ngực tạ đôi", "...", R.drawable.daynguc_gif, ExerciseType.CHEST, MeasureUnit.REPS, 20, BodyPart.NGUC, "WDIpL0pjun0", R.drawable.daynguc),

        // ============================================================
        // === 4. BÀI TẬP CHÂN (LEG)
        // ============================================================
        // ---Chân Dễ -- 421 - 440
        Exercisee(421, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(422, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.batnhay2),
        Exercisee(423, "Bật nhảy 180", "...", R.drawable.jumpsquat180_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.jumpsquat180),
        Exercisee(424, "Tấn", "...", R.drawable.splitsquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.splitsquat),
        Exercisee(425, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.leonui),
        Exercisee(426, "Nhảy tấn", "...", R.drawable.nhaytan_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.nhaytan),
        Exercisee(427, "Tấn sau", "...", R.drawable.reverselunge_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.reverselunge),
        Exercisee(428, "Sumo squat", "...", R.drawable.sumosquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.sumosquat),
        Exercisee(429, "Gập gối", "...", R.drawable.lunges_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.lunges),
        Exercisee(430,"Deadlift", "...", R.drawable.romaniandeadlift_gif, ExerciseType.LEG, MeasureUnit.TIME, 20,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.romaniandeadlift),

        // ---Chân Trung Bình -- 441 - 460
        Exercisee(441, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(442, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.batnhay2),
        Exercisee(443, "Bật nhảy 180", "...", R.drawable.jumpsquat180_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.jumpsquat180),
        Exercisee(444, "Tấn", "...", R.drawable.splitsquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.splitsquat),
        Exercisee(445, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.leonui),
        Exercisee(446, "Nhảy tấn", "...", R.drawable.nhaytan_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.nhaytan),
        Exercisee(447, "Tấn sau", "...", R.drawable.reverselunge_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.reverselunge),
        Exercisee(448, "Sumo squat", "...", R.drawable.sumosquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.sumosquat),
        Exercisee(449, "Gập gối", "...", R.drawable.lunges_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.lunges),
        Exercisee(440,"Deadlift", "...", R.drawable.romaniandeadlift_gif, ExerciseType.LEG, MeasureUnit.TIME, 30,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.romaniandeadlift),

        // ---Chân Nâng Cao -- 461 - 480
        Exercisee(461, "Bật nhảy", "...", R.drawable.jumpingjack, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN,"dQw4w9WgXcQ",R.drawable.batnhay),
        Exercisee(462, "Bật nhảy 2", "...", R.drawable.batnhay2_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.batnhay2),
        Exercisee(463, "Bật nhảy 180", "...", R.drawable.jumpsquat180_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.jumpsquat180),
        Exercisee(464, "Tấn", "...", R.drawable.splitsquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "Fcbw82ykBvY", R.drawable.splitsquat),
        Exercisee(465, "Leo núi", "...", R.drawable.leonui_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.leonui),
        Exercisee(466, "Nhảy tấn", "...", R.drawable.nhaytan_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.nhaytan),
        Exercisee(467, "Tấn sau", "...", R.drawable.reverselunge_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.reverselunge),
        Exercisee(468, "Sumo squat", "...", R.drawable.sumosquat_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.sumosquat),
        Exercisee(469, "Gập gối", "...", R.drawable.lunges_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.lunges),
        Exercisee(470,"Deadlift", "...", R.drawable.romaniandeadlift_gif, ExerciseType.LEG, MeasureUnit.TIME, 40,
            BodyPart.CHAN, "wQq3ybaLZeA", R.drawable.romaniandeadlift),
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
            1 -> listOfNotNull(getExCopy(1),
                getExCopy(2),
                getExCopy(3),
                getExCopy(4),
                getExCopy(5),
                getExCopy(6),
                getExCopy(7),
                getExCopy(8),
                getExCopy(9),
                getExCopy(10),
                getExCopy(11),
                getExCopy(12),
                getExCopy(13)
            )

            // Plan 2: Bụng Trung Bình -> Dùng bộ ID 101, 102, 103 (Độc lập hoàn toàn với Plan 1)
            2 -> listOfNotNull(getExCopy(101),
                getExCopy(102),
                getExCopy(103),
                getExCopy(104),
                getExCopy(105),
                getExCopy(106),
                getExCopy(107),
                getExCopy(108),
                getExCopy(109),
                getExCopy(110),
                getExCopy(111),
                getExCopy(112),
                getExCopy(113),
                getExCopy(114),
                getExCopy(115),
                getExCopy(116),
                )
            // Plan 3: Bụng Nâng Cao
            3 -> listOfNotNull(getExCopy(201),
                getExCopy(202),
                getExCopy(203),
                getExCopy(204),
                getExCopy(205),
                getExCopy(206),
                getExCopy(207),
                getExCopy(208),
                getExCopy(209),
                getExCopy(210),
                getExCopy(211),
                getExCopy(212),
                getExCopy(213),
                getExCopy(214),
                getExCopy(215),
                getExCopy(216),
            )

            // === TAY DỄ (Plan 4) ===
            4 -> listOfNotNull(
                getExCopy(305),
                getExCopy(303),
                getExCopy(301),
                getExCopy(302),
                getExCopy(307),
                getExCopy(308),
                getExCopy(304),
                getExCopy(306)
            )

            // === TAY TRUNG BÌNH (Plan 5) ===
            5 -> listOfNotNull(
                getExCopy(325),
                getExCopy(323),
                getExCopy(321),
                getExCopy(326),
                getExCopy(322),
                getExCopy(327),
                getExCopy(328),
                getExCopy(324)
            )

            // === TAY NÂNG CAO (Plan 6 ===
            6 -> listOfNotNull(
                getExCopy(345),
                getExCopy(343),
                getExCopy(346),
                getExCopy(341),
                getExCopy(342),
                getExCopy(347),
                getExCopy(348),
                getExCopy(344)
            )
            // Plan 7: Ngực Dễ
            7 -> listOfNotNull(
                getExCopy(365),
                getExCopy(363),
                getExCopy(368),
                getExCopy(361),
                getExCopy(366),
                getExCopy(362),
                getExCopy(367),
                getExCopy(364)
            )

            // Plan 8: Ngực Trung Bình (Reps 15)
            8 -> listOfNotNull(
                getExCopy(385),
                getExCopy(383),
                getExCopy(388),
                getExCopy(381),
                getExCopy(386),
                getExCopy(382),
                getExCopy(387),
                getExCopy(384)
            )

            // Plan 9: Ngực Nâng Cao
            9 -> listOfNotNull(
                getExCopy(405),
                getExCopy(403),
                getExCopy(408),
                getExCopy(401),
                getExCopy(406),
                getExCopy(402),
                getExCopy(407),
                getExCopy(404)
            )

            // Plan 10: Chân Dễ
            10 -> listOfNotNull(
                getExCopy(421),
                getExCopy(425),
                getExCopy(430),
                getExCopy(428),
                getExCopy(429),
                getExCopy(427),
                getExCopy(424),
                getExCopy(422)
            )

            // Plan 11: Chân Trung Bình
            11 -> listOfNotNull(
                getExCopy(441),
                getExCopy(445),
                getExCopy(450),
                getExCopy(448),
                getExCopy(449),
                getExCopy(444),
                getExCopy(447),
                getExCopy(443),
                getExCopy(446)
            )

            // Plan 12: Chân Nâng Cao
            12 -> listOfNotNull(
                getExCopy(461),
                getExCopy(465),
                getExCopy(463),
                getExCopy(466),
                getExCopy(468),
                getExCopy(470),
                getExCopy(469),
                getExCopy(464),
                getExCopy(467),
                getExCopy(462)
            )
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