package com.example.fitness

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val hint: String,
    val imageRes: Int? = null,
    val gifRes: Int? = null
)

enum class WorkoutType {
    FULLBODY,
    ABS,
    CHEST,
    ARM
}

// ================== REPOSITORY ==================

object QuizQuestionRepository {

    fun getQuestions(workout: WorkoutType, level: Int): List<QuizQuestion> {
        return when (workout) {
            WorkoutType.FULLBODY -> when (level) {
                1 -> fullbodyLv1
                2 -> fullbodyLv2
                3 -> fullbodyLv3
                else -> emptyList()
            }

            WorkoutType.ABS -> when (level) {
                1 -> absLv1
                2 -> absLv2
                3 -> absLv3
                else -> emptyList()
            }

            WorkoutType.CHEST -> when (level) {
                1 -> chestLv1
                2 -> chestLv2
                3 -> chestLv3
                else -> emptyList()
            }

            WorkoutType.ARM -> when (level) {
                1 -> armLv1
                2 -> armLv2
                3 -> armLv3
                else -> emptyList()
            }
        }
    }

    // ========== FULLBODY ==========

    private val fullbodyLv1 = listOf(
        QuizQuestion(
                question = "Bài FULLBODY nên tập khoảng bao nhiêu buổi/tuần để người mới duy trì form mà không quá tải?",
                options = listOf(
                    "1 buổi/tuần",
                    "2–3 buổi/tuần",
                    "5–6 buổi/tuần",
                    "Ngày nào cũng fullbody cho nhanh"
                ),
                correctIndex = 1,
                hint = "2–3 buổi/tuần là đủ để cơ hồi phục. Fullbody mỗi ngày là tự đăng ký nội trú phòng gym 😵‍💫"
            ),
            QuizQuestion(
                question = "Trước buổi fullbody, phần nào nên được ưu tiên khởi động kỹ nhất?",
                options = listOf(
                    "Khớp vai, hông, gối",
                    "Mỗi tay vài cái xoay xoay là đủ",
                    "Chỉ cần chạy bộ 1 tí",
                    "Không cần khởi động, vào nâng luôn cho nóng"
                ),
                correctIndex = 0,
                hint = "Fullbody động vào gần như toàn thân, nên khớp lớn (vai, hông, gối) phải được xoay kỹ cho đỡ ‘kêu rốp rốp’."
            ),
            QuizQuestion(
                question = "Khi tập fullbody cho người mới, thứ tự nào hợp lý hơn?",
                options = listOf(
                    "Isolation trước, compound sau",
                    "Compound trước, isolation sau",
                    "Muốn sao cũng được",
                    "Lúc nào mỏi thì đổi bài"
                ),
                correctIndex = 1,
                hint = "Bài compound cần nhiều sức, nên để lên đầu. Isolation để cuối buổi ‘đuối thì đốt nốt’ là hợp lý."
            ),
            QuizQuestion(
                question = "Giữa các buổi fullbody nên nghỉ ít nhất bao lâu?",
                options = listOf(
                    "Không cần nghỉ ngày nào",
                    "Nghỉ tối thiểu 1 ngày",
                    "Nghỉ 3–4 ngày",
                    "Tuần tập 1 buổi cho chắc"
                ),
                correctIndex = 1,
                hint = "Fullbody quất hết cả người, nên cơ cần ít nhất 1 ngày nghỉ cho tử tế, không phải Iron Man 💀."
            ),
            QuizQuestion(
                question = "Trong buổi fullbody, bài nào nên làm trước?",
                options = listOf(
                    "Bài nặng cho chân hoặc lưng",
                    "Bài tay cho đỡ mệt",
                    "Bụng cho 6 múi sớm",
                    "Tùy hứng, thích bài nào làm bài đó"
                ),
                correctIndex = 0,
                hint = "Các nhóm cơ lớn (chân, lưng) tốn nhiều sức → nên giải quyết lúc còn ‘full pin’."
            ),
            QuizQuestion(
                question = "Khi chọn mức tạ cho buổi fullbody, cách nào hợp lý nhất?",
                options = listOf(
                    "Cố gắng chọn tạ nặng nhất phòng cho ngầu",
                    "Chọn tạ vừa đủ thực hiện đúng kỹ thuật",
                    "Nhẹ như lông hồng miễn là nhiều rep",
                    "Thấy người bên cạnh nâng bao nhiêu thì nâng bấy nhiêu"
                ),
                correctIndex = 1,
                hint = "Form xấu + tạ nặng = vé khuyến mãi vào khoa chấn thương chỉnh hình. Vừa sức mà chuẩn form vẫn lời hơn."
            ),
            QuizQuestion(
                question = "Một set fullbody hợp lý thường kéo dài khoảng:",
                options = listOf(
                    "5–10 giây",
                    "20–45 giây",
                    "2–3 phút",
                    "Càng lâu càng tốt"
                ),
                correctIndex = 1,
                hint = "Thời gian chịu lực 20–45 giây là vùng vàng cho hypertrophy (tăng cơ). Không phải kéo tạ như leo núi Everest."
            ),
            QuizQuestion(
                question = "Thời gian nghỉ giữa các set trong buổi fullbody cho người mới nên là:",
                options = listOf(
                    "10–15 giây cho hardcore",
                    "30–90 giây tùy độ nặng",
                    "5 phút mỗi set cho chắc cú",
                    "Nghỉ bao lâu cũng được, bấm điện thoại là chính"
                ),
                correctIndex = 1,
                hint = "Nghỉ 30–90 giây giúp tim phổi ổn lại mà cơ vẫn chưa ‘nguội’ hẳn. Nghỉ 5 phút là thành buổi check Facebook."
            ),
            QuizQuestion(
                question = "Dấu hiệu nào cho thấy buổi fullbody đang hơi quá sức với người mới?",
                options = listOf(
                    "Thở nhanh hơn bình thường",
                    "Ra mồ hôi nhẹ",
                    "Chóng mặt, buồn nôn, run tay chân",
                    "Hơi mỏi cơ 1 chút"
                ),
                correctIndex = 2,
                hint = "Chóng mặt, buồn nôn, run tay chân là body báo động đỏ. Lúc đó giảm độ nặng, nghỉ thêm, đừng cố ‘anh hùng’."
            ),
            QuizQuestion(
                question = "Mục tiêu chính của fullbody cho người mới là gì?",
                options = listOf(
                    "Tăng tạ càng nhanh càng tốt",
                    "Làm quen kỹ thuật, xây nền thể lực toàn thân",
                    "Thi đua body với PT trong phòng",
                    "Hôm nào cũng phải tập đến kiệt sức"
                ),
                correctIndex = 1,
                hint = "Giai đoạn đầu là xây nền: học form, quen nhịp tập, khỏe tổng thể. Không phải thi vô giải thể hình quốc gia ngay ngày mai."
            )
        )
    private val fullbodyLv2 = listOf(
        QuizQuestion(
            question = "Mục tiêu chính của FULLBODY 3 buổi/tuần cho người mới thường là:",
            options = listOf(
                "Tăng sức bền cơ bản và kỹ thuật",
                "Max tạ càng nhanh càng tốt",
                "Thi đấu powerlifting",
                "Chuẩn bị thi thể hình"
            ),
            correctIndex = 0,
            hint = "FULLBODY với người mới chủ yếu là làm quen chuyển động, tăng sức bền và form."
        ),
        QuizQuestion(
            question = "Khi tập FULLBODY, bài compound nên được đặt:",
            options = listOf(
                "Đầu buổi",
                "Cuối buổi",
                "Giữa buổi",
                "Không cần dùng compound"
            ),
            correctIndex = 0,
            hint = "Squat, deadlift, bench… nên đi đầu vì tốn nhiều sức nhất."
        ),
        QuizQuestion(
            question = "Vì sao không nên tập FULLBODY nặng 2 ngày liên tiếp?",
            options = listOf(
                "Vì gym đóng cửa cách ngày",
                "Cơ và hệ thần kinh cần thời gian hồi phục",
                "Chán, không vui",
                "Không có luật nào như vậy"
            ),
            correctIndex = 1,
            hint = "Cơ + hệ thần kinh trung ương đều cần nghỉ, đặc biệt sau bài nặng."
        ),
        QuizQuestion(
            question = "Khi cảm thấy kỹ thuật bắt đầu xấu đi trong buổi FULLBODY, nên:",
            options = listOf(
                "Cố gắng gồng tiếp cho xong",
                "Giảm tạ hoặc giảm số rep",
                "Bỏ hẳn bài đó khỏi lịch",
                "Chuyển qua chơi game trên máy chạy"
            ),
            correctIndex = 1,
            hint = "Kỹ thuật xấu = nguy cơ chấn thương. Giảm độ khó để giữ form đẹp."
        ),
        QuizQuestion(
            question = "Một buổi FULLBODY hiệu quả với người bận rộn nên có khoảng:",
            options = listOf(
                "3–4 bài compound + 1–2 bài bổ trợ",
                "Chỉ 1 bài duy nhất",
                "10–12 bài cho đủ mệt",
                "Toàn bài isolation"
            ),
            correctIndex = 0,
            hint = "Ít bài nhưng chất, tập trung vào compound và vài bài hỗ trợ là đủ."
        ),
        QuizQuestion(
            question = "Nếu trong buổi FULLBODY chân đã quá mỏi, bài squat cuối nên:",
            options = listOf(
                "Vẫn giữ tạ nặng như kế hoạch",
                "Giảm tạ hoặc đổi sang squat không tạ",
                "Bỏ luôn phần chân",
                "Thêm bài nhảy cho cháy đùi"
            ),
            correctIndex = 1,
            hint = "Ưu tiên an toàn. Mỏi quá thì giảm tải, dùng bodyweight hoặc bài dễ hơn."
        ),
        QuizQuestion(
            question = "Để theo dõi tiến bộ trong FULLBODY, yếu tố nào HỢP LÝ nhất?",
            options = listOf(
                "Cân nặng trên bàn cân mỗi ngày",
                "Số like ảnh tập gym",
                "Số rep/tạ tăng dần và cảm giác vận động",
                "Số ngày lên gym check-in"
            ),
            correctIndex = 2,
            hint = "Progress nên dựa trên hiệu suất tập và cảm nhận cơ thể, không chỉ là cân nặng."
        ),
        QuizQuestion(
            question = "Ngày OFF sau chuỗi buổi FULLBODY nên dùng để:",
            options = listOf(
                "Nằm im cả ngày cho “rest day chân chính”",
                "Đi bộ nhẹ, giãn cơ, ngủ đủ",
                "Ăn càng ít càng tốt",
                "Tập thêm cardio nặng"
            ),
            correctIndex = 1,
            hint = "Active recovery (đi bộ, giãn cơ, ngủ ngon) giúp hồi phục tốt hơn nằm im."
        ),
        QuizQuestion(
            question = "Nếu lịch FULLBODY 3 buổi/tuần, cách sắp xếp hợp lý là:",
            options = listOf(
                "T2–T3–T4",
                "T2–T4–T6",
                "T5–T6–CN",
                "T2–T7–CN"
            ),
            correctIndex = 1,
            hint = "T2–T4–T6 tạo khoảng nghỉ xen kẽ, cơ có thời gian phục hồi."
        ),
        QuizQuestion(
            question = "Điều gì quan trọng nhất để FULLBODY lâu dài không bị chán và bỏ giữa chừng?",
            options = listOf(
                "Đổi bài liên tục mỗi buổi",
                "Có lịch rõ ràng, nặng dần vừa phải và mục tiêu cụ thể",
                "Chỉ tập khi rảnh hứng lên",
                "Mỗi buổi phải “chết trên sàn” mới thấy đã"
            ),
            correctIndex = 1,
            hint = "Lịch rõ ràng + tăng độ khó hợp lý + mục tiêu cụ thể sẽ giữ động lực tốt hơn."
        )
    )
    private val fullbodyLv3 = listOf(
        // 5 GIF đầu
        QuizQuestion(
            question = "Đây là động tác gì?",
            options = listOf(
                "Deadlift",
                "Bench press",
                "Jump",
                "Squat"
            ),
            correctIndex = 3,
            hint = "Lưng mà gù thì cột sống chửi thề trước, cơ đùi chưa kịp to là lưng đi trước nha 😵‍💫",
            gifRes = R.drawable.quiz_fullbody_gif1
        ),
        QuizQuestion(
            question = "GIF: Người deadlift kéo tạ nhưng bar luôn cách ống chân khá xa. Đây là lỗi gì?",
            options = listOf(
                "Tạ nhẹ quá",
                "Chân đứng hẹp",
                "Để bar cách người quá xa",
                "Thở chưa đúng nhịp"
            ),
            correctIndex = 2,
            hint = "Deadlift mà để bar xa người là lưng phải gánh hết, chân với mông đứng nhìn cho vui thôi.",
            imageRes = R.drawable.quiz_fullbody_img6
        ),
        QuizQuestion(
            question = "Người tập lunge, gối trước vượt quá mũi chân khá nhiều. Điều gì dễ xảy ra nếu duy trì?",
            options = listOf(
                "Đùi to hơn bình thường",
                "Gối chịu lực quá lớn, lâu dài dễ đau",
                "Mông bị nhỏ lại",
                "Không có vấn đề gì"
            ),
            correctIndex = 1,
            hint = "Gối là hàng hiếm, đừng ném hết tải vào nó. Gối qua mũi chân sâu quá là hơi toang.",
            imageRes = R.drawable.quiz_fullbody_img7
        ),
        QuizQuestion(
            question = "Một buổi fullbody có quá nhiều bài isolation (cô lập 1 nhóm cơ). Điều gì sai ở đây?",
            options = listOf(
                "Không sai, càng nhiều bài càng tốt",
                "Fullbody nên ưu tiên compound, không phải isolation là chính",
                "Isolation giúp đốt mỡ tốt hơn",
                "Nên chỉ tập máy không tập tạ tự do"
            ),
            correctIndex = 1,
            hint = "Fullbody = dùng bài compound để tối ưu thời gian + năng lượng, isolation là topping thêm thôi.",
            imageRes = R.drawable.quiz_fullbody_img8
        ),
        QuizQuestion(
            question = "Người tập burpee nhưng không duỗi thẳng người ở pha đứng lên. Vấn đề là gì?",
            options = listOf(
                "Chỉ tốn ít calo hơn",
                "Phạm vi chuyển động không đủ, hiệu quả giảm",
                "Ảnh hưởng tim mạch",
                "Không có gì, miễn mệt là được"
            ),
            correctIndex = 1,
            hint = "Đã chơi burpee thì chơi tới, đứng lên mà không duỗi hết người là coi như làm nửa bài.",
            imageRes = R.drawable.quiz_fullbody_img9
        ),

        // 5 IMAGE sau
        QuizQuestion(
            question = "Hình minh họa 3 tư thế squat – 1 đúng, 2 sai. Tư thế đúng có đặc điểm nào?",
            options = listOf(
                "Lưng thẳng, ngực mở, gối cùng hướng mũi chân",
                "Gối chụm vào nhau cho đỡ mỏi",
                "Gót chân nhón lên cho dễ xuống",
                "Mông đẩy thẳng xuống, không đẩy ra sau"
            ),
            correctIndex = 0,
            hint = "Squat chuẩn là lưng thẳng, mông đẩy sau, gối không bị sụp vào trong.",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "Lịch 3 buổi fullbody/tuần. Cách sắp xếp nào hợp lý nhất?",
            options = listOf(
                "Thứ 2 - Thứ 3 - Thứ 4",
                "Thứ 2 - Thứ 5 - Chủ nhật",
                "Thứ 2 - Thứ 3 - Chủ nhật",
                "Thứ 6 - Thứ 7 - Chủ nhật"
            ),
            correctIndex = 1,
            hint = "Fullbody cần ngày nghỉ xen giữa để cơ hồi phục. Đừng hành xác như thi môn thể lực.",
            imageRes = R.drawable.quiz_fullbody_img2
        ),
        QuizQuestion(
            question = "Hình trước/sau 8 tuần fullbody. Điều gì QUAN TRỌNG NHẤT để đạt kết quả như vậy?",
            options = listOf(
                "Đổi bài tập liên tục cho đỡ chán",
                "Giữ lịch tập đều + ăn ngủ ổn định",
                "Tập càng nặng càng tốt",
                "Chỉ cần uống whey là được"
            ),
            correctIndex = 1,
            hint = "Không có phép màu, chỉ có đều đặn và kỷ luật. Whey chỉ là bonus nhỏ.",
            imageRes = R.drawable.quiz_fullbody_img3
        ),
        QuizQuestion(
            question = "Poster hướng dẫn khởi động trước fullbody. Tại sao warm-up lại quan trọng?",
            options = listOf(
                "Để đốt bớt calo trước khi tập",
                "Giúp khớp và cơ nóng lên, giảm chấn thương",
                "Chỉ để nhìn cho chuyên nghiệp",
                "Không quá quan trọng, có thể bỏ qua"
            ),
            correctIndex = 1,
            hint = "Warm-up = bảo hiểm cho khớp. Bỏ warm-up giống chạy xe không thắng vậy.",
            imageRes = R.drawable.quiz_fullbody_img4
        ),
        QuizQuestion(
            question = "Biểu đồ thể hiện nhịp tim trong buổi fullbody. Vùng màu nào nên được giữ lâu nhất?",
            options = listOf(
                "Zone siêu nhẹ, hít thở là chính",
                "Zone vừa phải – tim tăng nhưng vẫn nói chuyện được",
                "Zone tối đa – tập như thi Olympic",
                "Không cần quan tâm nhịp tim"
            ),
            correctIndex = 1,
            hint = "Tập để khỏe chứ không phải để xỉu. Zone vừa phải là best cho người phổ thông.",
            imageRes = R.drawable.quiz_fullbody_img5
        )
    )



    // ---------------- ABS ----------------

    private val absLv1 = listOf(
        QuizQuestion(
                question = "Bài ABS nên được sắp vào thời điểm nào trong buổi tập để hiệu quả hơn?",
                options = listOf(
                    "Ngay khi vừa vào phòng",
                    "Sau khởi động/cardio nhẹ hoặc cuối buổi",
                    "Giữa buổi khi đang mệt nhất",
                    "Không quan trọng, thích thì tập"
                ),
                correctIndex = 1,
                hint = "Core cần nóng và khớp đã hoạt động rồi. Đập ABS khi người còn lạnh là hơi liều."
            ),
            QuizQuestion(
                question = "Đối với người mới, nên tập ABS với tần suất nào là hợp lý?",
                options = listOf(
                    "Mỗi ngày, không nghỉ",
                    "2–3 buổi/tuần",
                    "1 tháng 1 lần cho đỡ đau bụng",
                    "Chỉ tập khi thấy bụng mỡ"
                ),
                correctIndex = 1,
                hint = "Core cũng là cơ, tập xong phải cho nghỉ. 2–3 buổi/tuần là đủ ‘chào hỏi 6 múi’ rồi."
            ),
            QuizQuestion(
                question = "Bài plank chủ yếu tác động vào:",
                options = listOf(
                    "Chân",
                    "Vai",
                    "Cơ core (bụng, lưng dưới)",
                    "Cổ"
                ),
                correctIndex = 2,
                hint = "Plank là bài ‘anti-movement’, giữ ổn định thân người. Core làm việc chính, không phải cổ."
            ),
            QuizQuestion(
                question = "Khi tập gập bụng (crunch), lỗi phổ biến nhất là:",
                options = listOf(
                    "Nín thở",
                    "Dùng tay kéo cổ",
                    "Co chân",
                    "Thở ra khi lên"
                ),
                correctIndex = 1,
                hint = "Nhiều người gồng cổ kéo đầu lên → đau cổ chứ không phải đau bụng. Tay chỉ nên đỡ nhẹ, không kéo."
            ),
            QuizQuestion(
                question = "Khi tập ABS, cách hít thở nào ổn hơn?",
                options = listOf(
                    "Hít khi xuống, thở ra khi gồng lên",
                    "Nín thở cho đỡ mệt",
                    "Thở sao cũng được",
                    "Chỉ cần hít sâu trước set rồi nhịn tới cuối"
                ),
                correctIndex = 0,
                hint = "Hít xuống – thở ra khi gồng lên giúp kiểm soát core, đỡ bị ‘nghẹt’ và chóng mặt."
            ),
            QuizQuestion(
                question = "Tập ABS bằng bài nặng, nhiều tạ cho người mới có phải là ý hay?",
                options = listOf(
                    "Rất hay, bụng sẽ to nhanh",
                    "Không, nên bắt đầu với bodyweight và kiểm soát form",
                    "Càng nặng càng tốt",
                    "Tạ bao nhiêu không quan trọng, miễn đau là được"
                ),
                correctIndex = 1,
                hint = "Core yếu mà chơi tạ nặng sớm rất dễ trẹo lưng. Bodyweight mà chuẩn form còn lời hơn."
            ),
            QuizQuestion(
                question = "Bài leg raise/chân nâng chủ yếu ‘ăn’ vào phần nào?",
                options = listOf(
                    "Bụng dưới",
                    "Bụng trên",
                    "Vai",
                    "Cẳng tay"
                ),
                correctIndex = 0,
                hint = "Leg raise tập trung nhiều vào vùng bụng dưới – vùng mà ai cũng ghét nhưng ai cũng muốn mất nó."
            ),
            QuizQuestion(
                question = "Khi tập ABS, cảm giác nào sau đây bình thường?",
                options = listOf(
                    "Bụng nóng, căng, hơi rát",
                    "Đau nhói vùng cổ hoặc lưng dưới",
                    "Tê chân, tê tay",
                    "Đau đầu, hoa mắt"
                ),
                correctIndex = 0,
                hint = "Bụng rát rát là chuyện tình yêu. Cổ/lưng dưới đau nhói thì nên xem lại form ngay."
            ),
            QuizQuestion(
                question = "Muốn bụng rõ hơn, ngoài ABS còn cần gì?",
                options = listOf(
                    "Chỉ cần tập bụng thật nhiều",
                    "Chế độ ăn, ngủ nghỉ và tổng mức vận động hợp lý",
                    "Mua đai nịt bụng là xong",
                    "Hít đất là bụng lên"
                ),
                correctIndex = 1,
                hint = "ABS chỉ 1 phần. Ăn ngủ sinh hoạt mới quyết định lớp mỡ có chịu nhả không."
            ),
            QuizQuestion(
                question = "Nếu hôm đó đã tập fullbody khá nặng, phần ABS nên:",
                options = listOf(
                    "Bỏ hẳn cho đỡ mệt",
                    "Giảm volume, chọn bài basic nhẹ",
                    "Vẫn full 10 bài cho cứng",
                    "Chuyển thành plank 10 phút"
                ),
                correctIndex = 1,
                hint = "Fullbody đã ‘quật’ khá nhiều vào core rồi, nên ABS chỉ cần nhẹ nhàng thêm, không cần phá hoại bản thân."
            )
        )
private val absLv2 = listOf(
    QuizQuestion(
        question = "Tập ABS quá nhiều mỗi ngày có thể gây ra vấn đề gì?",
        options = listOf(
            "Không vấn đề gì, càng nhiều càng tốt",
            "Cơ bụng mệt, form xấu và đau lưng",
            "Chỉ làm bụng to lên vì cơ",
            "Giảm nhịp tim nguy hiểm"
        ),
        correctIndex = 1,
        hint = "Bụng cũng là cơ, cần thời gian hồi phục, quá tải dễ kéo theo đau lưng, sai tư thế."
    ),
    QuizQuestion(
        question = "Bài ABS nào sau đây là bài compound tốt cho core?",
        options = listOf(
            "Crunch nằm gập bụng nhẹ",
            "Plank",
            "Xoay eo đứng vặn người tốc độ cao",
            "Nằm yên, hóp bụng"
        ),
        correctIndex = 1,
        hint = "Plank huy động nhiều nhóm cơ và phù hợp cho người mới nếu giữ form đúng."
    ),
    QuizQuestion(
        question = "Khi plank, sai lầm phổ biến nhất là:",
        options = listOf(
            "Giữ lưng thẳng",
            "Siết nhẹ bụng và mông",
            "Hông võng xuống hoặc đẩy lên quá cao",
            "Nhìn xuống sàn"
        ),
        correctIndex = 2,
        hint = "Hông võng hoặc quá cao làm mất tác dụng vào core và tăng áp lực lên lưng."
    ),
    QuizQuestion(
        question = "Muốn giảm mỡ bụng hiệu quả, nên:",
        options = listOf(
            "Chỉ tập thật nhiều ABS",
            "Kết hợp tập toàn thân + ăn uống hợp lý",
            "Đeo đai nịt bụng khi ngủ",
            "Chỉ chạy bộ, không cần tạ"
        ),
        correctIndex = 1,
        hint = "Không có chuyện “giảm mỡ tại chỗ”. Cần hoạt động toàn thân + chế độ ăn."
    ),
    QuizQuestion(
        question = "Khi tập ABS, hơi thở đúng là:",
        options = listOf(
            "Nín thở cho tập trung",
            "Hít vào khi gồng bụng, thở ra khi thả lỏng",
            "Thở đều, thở ra khi gồng, hít vào khi về vị trí ban đầu",
            "Thở thế nào cũng được"
        ),
        correctIndex = 2,
        hint = "Thở ra khi gồng giúp ổn định core và dễ kiểm soát chuyển động hơn."
    ),
    QuizQuestion(
        question = "Số buổi ABS/tuần hợp lý khi đã tập toàn thân 3 buổi là:",
        options = listOf(
            "Mỗi ngày 1 lần",
            "2–3 buổi ABS xen kẽ",
            "Chỉ 1 buổi/tuần",
            "Không cần ABS riêng"
        ),
        correctIndex = 1,
        hint = "2–3 buổi/tuần cho core là đủ nếu đã tập tạ toàn thân."
    ),
    QuizQuestion(
        question = "Bài leg raise (nâng chân) khi bụng chưa đủ khỏe dễ gây:",
        options = listOf(
            "Đau cổ",
            "Đau vai",
            "Đau lưng dưới nếu võng lưng",
            "Không có vấn đề gì"
        ),
        correctIndex = 2,
        hint = "Nếu lưng dưới không áp sát sàn mà bị võng là dễ đau lưng dưới nhất."
    ),
    QuizQuestion(
        question = "Để ABS nhìn rõ hơn, yếu tố quan trọng NHẤT là:",
        options = listOf(
            "Tập thật nhiều bài bụng",
            "Giảm mỡ toàn thân xuống mức phù hợp",
            "Uống nhiều nước",
            "Đeo đai nịt bụng 24/7"
        ),
        correctIndex = 1,
        hint = "Cơ bụng ai cũng có, nhưng mỡ thấp thì mới nhìn thấy rõ."
    ),
    QuizQuestion(
        question = "Nếu cơ bụng bị căng cứng nhiều ngày liền, nên:",
        options = listOf(
            "Tiếp tục tập nặng hơn",
            "Nghỉ hoặc giảm độ khó, tập giãn cơ",
            "Không tập gì khác",
            "Chỉ chạy bộ"
        ),
        correctIndex = 1,
        hint = "Đau cơ kéo dài = cần thêm thời gian hồi phục và giãn cơ."
    ),
    QuizQuestion(
        question = "Bài ABS phù hợp để kết thúc buổi tập nặng là:",
        options = listOf(
            "Sit-up tốc độ cao",
            "Plank hoặc dead bug chậm, kiểm soát",
            "Russian twist xoay càng mạnh càng tốt",
            "Không nên tập bụng cuối buổi"
        ),
        correctIndex = 1,
        hint = "Các bài kiểm soát, chậm rãi sẽ an toàn hơn khi cơ đã mệt."
    )
)
    private val absLv3 = listOf(
        QuizQuestion(
            question = "GIF: Người tập plank nhưng mông bị chổng lên cao. Vấn đề lớn nhất là gì?",
            options = listOf(
                "Chưa siết cơ bụng tốt",
                "Vai đặt sai vị trí",
                "Tay chống quá gần nhau",
                "Không có vấn đề gì"
            ),
            correctIndex = 0,
            hint = "Plank mà đẩy mông lên là thành yoga luôn rồi 😭 cơ bụng chẳng làm gì hết.",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Khi crunch, người tập kéo cổ quá nhiều bằng tay. Điều này dễ gây ra điều gì?",
            options = listOf(
                "Đau lưng dưới",
                "Đau cổ và cột sống cổ",
                "Đau vai",
                "Không ảnh hưởng gì"
            ),
            correctIndex = 1,
            hint = "Crunch là dùng bụng, không phải bài kéo cổ đâu nha 🙃",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập leg raise nhưng lưng dưới không áp sát sàn. Hậu quả?",
            options = listOf(
                "Đau vai",
                "Đau cổ",
                "Đau lưng dưới",
                "Không sao hết"
            ),
            correctIndex = 2,
            hint = "Lưng dưới mà hở là lưng ăn hành trước bụng luôn 😵‍💫",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập Russian Twist nhưng xoay thân quá nhanh. Sai ở đâu?",
            options = listOf(
                "Không siết bụng, phụ thuộc quán tính",
                "Không thở đúng",
                "Tay cầm sai",
                "Không sai gì"
            ),
            correctIndex = 0,
            hint = "Bài xoay bụng mà thành bài xoay đồ chơi con quay thì thôi 😆",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập sit-up nhưng bật người như đàn hồi. Điều gì không ổn?",
            options = listOf(
                "Không kiểm soát chuyển động",
                "Bụng to ra",
                "Tim mệt hơn",
                "Không sao"
            ),
            correctIndex = 0,
            hint = "Bài bụng ưu tiên kiểm soát. Bật như lò xo thì lưng chịu hết.",
            imageRes = R.drawable.quiz_fullbody_img1
        ),

        // IMG
        QuizQuestion(
            question = "Hình minh họa tư thế plank chuẩn. Đặc điểm đúng là gì?",
            options = listOf(
                "Lưng thẳng, mông không cao, bụng siết",
                "Mông cao hơn lưng",
                "Ngực chạm sàn",
                "Vai nhô về trước quá nhiều"
            ),
            correctIndex = 0,
            hint = "Plank chuẩn nhìn như cái bàn thẳng, không phải cây cầu 😎",
            imageRes = R.drawable.quiz_abs_img1
        ),
        QuizQuestion(
            question = "Hai tư thế crunch – một đúng, một sai. Thế nào là đúng?",
            options = listOf(
                "Dùng tay kéo cổ lên",
                "Chỉ nâng phần trên thân bằng cơ bụng",
                "Nâng hết nguyên người",
                "Không cần siết bụng"
            ),
            correctIndex = 1,
            hint = "Crunch là nâng thân trên bằng bụng, không phải bài gập người toàn phần.",
            imageRes = R.drawable.quiz_abs_img2
        ),
        QuizQuestion(
            question = "So sánh kết quả bụng 8 tuần tập. Điều gì quan trọng nhất?",
            options = listOf(
                "Chỉ tập bụng hằng ngày",
                "Tập đều + ăn uống hợp lý",
                "Chỉ cần uống whey",
                "Chỉ cần cardio"
            ),
            correctIndex = 1,
            hint = "Muốn bụng đẹp mà ăn như phá là coi như đi tong 🥲",
            imageRes = R.drawable.quiz_abs_img3
        ),
        QuizQuestion(
            question = "Hình hướng dẫn thở khi tập bụng. Cách đúng là?",
            options = listOf(
                "Hít vào khi gồng bụng",
                "Thở ra khi siết bụng",
                "Nín thở",
                "Thở sao cũng được"
            ),
            correctIndex = 1,
            hint = "Thở đúng mới truyền lực tốt. Nín thở là đi gặp ông bà 😵",
            imageRes = R.drawable.quiz_abs_img4
        ),
        QuizQuestion(
            question = "Minh hoạ các vùng cơ bụng. Bài ABS nên ưu tiên?",
            options = listOf(
                "Chỉ tập bụng trên",
                "Chỉ tập bụng dưới",
                "Tập đều bụng trên – dưới – oblique",
                "Không quan trọng, tập đại"
            ),
            correctIndex = 2,
            hint = "Bụng đâu phải chỉ có 6 múi phía trên 😎 phải đều mới đẹp.",
            imageRes = R.drawable.quiz_abs_img5
        )
    )
    // ---------------- CHEST ----------------

    private val chestLv1 = listOf(
        QuizQuestion(

                question = "Khi tập CHEST, bài nào thường được ưu tiên đầu buổi?",
                options = listOf(
                    "Fly cable nhẹ nhàng cho đỡ mệt",
                    "Đẩy tạ tự do (bench press)",
                    "Chống đẩy gác chân lên ghế",
                    "Plank 5 phút cho nóng người"
                ),
                correctIndex = 1,
                hint = "Bench press là compound chính của ngực, nên được ưu tiên khi cơ và thần kinh còn tươi."
            ),
            QuizQuestion(
                question = "Khi bench press, đường đi thanh tạ hợp lý là:",
                options = listOf(
                    "Thẳng đứng lên xuống",
                    "Chéo nhẹ từ ngang ngực xuống gần xương ức",
                    "Tạt ngang qua cổ",
                    "Muốn sao cũng được, miễn đẩy lên"
                ),
                correctIndex = 1,
                hint = "Thanh tạ thường đi hơi chéo: trên ngực – xuống gần chỗ dưới ngực một chút. Đừng kéo lên vùng cổ."
            ),
            QuizQuestion(
                question = "Góc ghế nào tập trung nhiều hơn vào ngực trên?",
                options = listOf(
                    "Ghế phẳng (flat)",
                    "Ghế dốc xuống (decline)",
                    "Ghế dốc lên (incline)",
                    "Ngồi thẳng 90°"
                ),
                correctIndex = 2,
                hint = "Incline bench = ngực trên. Decline = ngực dưới. Flat = tổng thể."
            ),
            QuizQuestion(
                question = "Khi bench press, chân nên đặt thế nào?",
                options = listOf(
                    "Đung đưa cho đỡ chán",
                    "Đặt chắc trên sàn để tạo lực trụ",
                    "Co lên ghế cho ‘ngầu’",
                    "Không quan trọng"
                ),
                correctIndex = 1,
                hint = "Chân là điểm trụ. Đặt chắc trên sàn giúp ổn định toàn thân và đẩy được an toàn hơn."
            ),
            QuizQuestion(
                question = "Ở cuối rep fly (mở ngực), điều gì cần tránh?",
                options = listOf(
                    "Kéo tay quá sâu, vai kéo ngược ra sau quá đà",
                    "Giữ tay hơi cong",
                    "Kiểm soát tạ chậm",
                    "Thở ra"
                ),
                correctIndex = 0,
                hint = "Kéo quá sâu làm khớp vai chịu lực nhiều, dễ chấn thương. Vừa căng ngực là được."
            ),
            QuizQuestion(
                question = "Tín hiệu nào báo form bench press đang sai khá nặng?",
                options = listOf(
                    "Ngực mỏi",
                    "Triceps mỏi",
                    "Đau nhói vai trước, khớp vai khó chịu",
                    "Tim đập nhanh"
                ),
                correctIndex = 2,
                hint = "Vai trước đau nhói, khó chịu là dấu hiệu chèn ép vai. Cần chỉnh lại đường tạ, độ mở khuỷu tay, góc ghế."
            ),
            QuizQuestion(
                question = "Khi tập CHEST, để tránh ‘đuối’ quá sớm, nên:",
                options = listOf(
                    "Vào là max tạ luôn cho nhanh to",
                    "Tăng tạ dần qua từng set, tập trung form",
                    "Đổi bài liên tục",
                    "Không cần warm-up"
                ),
                correctIndex = 1,
                hint = "Warm-up rồi tăng tạ dần giúp cơ – khớp thích nghi, đỡ bị sock tạ."
            ),
            QuizQuestion(
                question = "Chống đẩy (push-up) là:",
                options = listOf(
                    "Bài chơi cho vui, không ăn thua",
                    "Bài bodyweight rất tốt cho ngực, vai, tay sau và core",
                    "Chỉ ăn vào tay",
                    "Tập thì càng nhanh càng tốt"
                ),
                correctIndex = 1,
                hint = "Push-up đúng form là bài compound xịn, không phải trò khởi động ‘cho vui’."
            ),
            QuizQuestion(
                question = "Khi tập CHEST, số rep phổ biến cho mục tiêu tăng cơ (hypertrophy) là:",
                options = listOf(
                    "1–3 rep",
                    "4–6 rep",
                    "8–15 rep với tạ vừa sức",
                    "30–50 rep"
                ),
                correctIndex = 2,
                hint = "8–15 rep với tạ hợp lý là vùng ngọt cho tăng cơ với đa số người tập."
            ),
            QuizQuestion(
                question = "Sau buổi CHEST nặng, ngày hôm sau cảm giác nào là bình thường?",
                options = listOf(
                    "Đau ê ẩm cơ ngực khi duỗi tay",
                    "Đau nhói khớp vai khi không làm gì",
                    "Đau lan xuống cổ, tê tay",
                    "Khó thở, tức ngực dữ dội"
                ),
                correctIndex = 0,
                hint = "Đau cơ (DOMS) là bình thường. Đau khớp, tê tay, khó thở là chuyện khác, nên đi kiểm tra nếu bất thường."
            )
        )
    private val chestLv2 = listOf(
        QuizQuestion(
            question = "Khi bench press, khoảng chạm tạ hợp lý nhất là:",
            options = listOf(
                "Chạm cổ",
                "Chạm giữa ngực",
                "Chạm bụng dưới",
                "Không cần chạm, chỉ hạ nửa đường"
            ),
            correctIndex = 1,
            hint = "Đa số người trưởng thành hạ tạ đến vùng giữa ngực là vừa an toàn vừa hiệu quả."
        ),
        QuizQuestion(
            question = "Để ưu tiên ngực trên (upper chest), nên dùng biến thể nào?",
            options = listOf(
                "Flat bench press",
                "Incline bench press",
                "Decline bench press",
                "Chỉ tập push-up"
            ),
            correctIndex = 1,
            hint = "Incline bench (ghế dốc lên) nhấn mạnh phần ngực trên."
        ),
        QuizQuestion(
            question = "Sai lầm phổ biến khi tập chest fly là:",
            options = listOf(
                "Hơi cong khuỷu tay",
                "Mở tay quá rộng, kéo căng khớp vai",
                "Giữ vai sau cố định",
                "Kiểm soát đường đi tạ"
            ),
            correctIndex = 1,
            hint = "Mở quá rộng + tạ nặng dễ làm vai chịu lực nhiều hơn ngực."
        ),
        QuizQuestion(
            question = "Khi bench press, để bảo vệ vai, nên:",
            options = listOf(
                "Dang tay vuông góc thân người",
                "Khép khuỷu tay vào thân người vừa phải",
                "Đưa tạ lên xuống thật nhanh",
                "Nín thở từ đầu tới cuối set"
            ),
            correctIndex = 1,
            hint = "Khép khuỷu 30–45° so với thân giúp vai bớt căng."
        ),
        QuizQuestion(
            question = "Số buổi CHEST/tuần hợp lý với người mới là:",
            options = listOf(
                "1–2 buổi, cách nhau ít nhất 48 giờ",
                "Mỗi ngày một buổi",
                "3–4 buổi liên tiếp",
                "Không cần ngày nghỉ"
            ),
            correctIndex = 0,
            hint = "Cơ cần thời gian hồi phục, nhất là sau bench nặng."
        ),
        QuizQuestion(
            question = "Để ngực phát triển đều, ngoài bench press nên:",
            options = listOf(
                "Chỉ tập thêm push-up",
                "Thêm các bài fly, dip hoặc máy chest press",
                "Không cần thêm gì",
                "Tăng cardio"
            ),
            correctIndex = 1,
            hint = "Kết hợp compound + isolation giúp ngực phát triển toàn diện."
        ),
        QuizQuestion(
            question = "Nếu cảm thấy đau khớp vai khi tập ngực, nên làm gì trước?",
            options = listOf(
                "Bỏ qua, cố tập tiếp",
                "Giảm tạ, kiểm tra lại form và khởi động kỹ vai",
                "Tăng tạ xem có đỡ không",
                "Chuyển qua tập chân"
            ),
            correctIndex = 1,
            hint = "Khởi động vai, giảm tạ và chỉnh form là bước đầu tiên trước khi nghĩ tới chấn thương nghiêm trọng."
        ),
        QuizQuestion(
            question = "Bài nào sau đây cũng hỗ trợ phát triển ngực dù không phải chest thuần?",
            options = listOf(
                "Dumbbell pullover",
                "Squat",
                "Deadlift",
                "Leg press"
            ),
            correctIndex = 0,
            hint = "Pullover tác động nhiều vào ngực và lưng trên."
        ),
        QuizQuestion(
            question = "Điều gì QUAN TRỌNG nhất khi chọn mức tạ cho bench press?",
            options = listOf(
                "Tạ càng nặng càng tốt",
                "Tạ vừa đủ để giữ form đúng trong suốt set",
                "Tạ thật nhẹ cho đỡ mệt",
                "Chọn bằng người bên cạnh"
            ),
            correctIndex = 1,
            hint = "Form chuẩn quan trọng hơn số ký, nhất là với ngực và vai."
        ),
        QuizQuestion(
            question = "Sau buổi ngực nặng, ngày hôm sau cảm giác thế nào là bình thường?",
            options = listOf(
                "Đau nhói khớp vai khi nhấc tay",
                "Đau cơ âm ỉ ở vùng ngực khi co duỗi",
                "Không có cảm giác gì mới là tốt",
                "Tê tay kéo dài"
            ),
            correctIndex = 1,
            hint = "DOMS nhẹ ở cơ là chuyện bình thường, đau khớp sắc nhói thì nên kiểm tra lại."
        )
    )
    private val chestLv3 = listOf(
        QuizQuestion(
            question = "GIF: Người bench press nhưng cong lưng quá mức và nhấc mông khỏi ghế. Sai ở đâu?",
            options = listOf(
                "Không sai, càng cong càng mạnh",
                "Mất an toàn và sai form",
                "Thanh tạ quá nặng",
                "Chân đặt sai vị trí"
            ),
            correctIndex = 1,
            hint = "Nhấc mông là thành bài tập lưng – không còn là bench nữa 😤",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Khi push-up, người tập hạ nửa chừng rồi đẩy lên. Sai gì?",
            options = listOf(
                "Không full ROM, hiệu quả giảm",
                "Thở không đúng",
                "Tay đặt hơi rộng",
                "Không có vấn đề"
            ),
            correctIndex = 0,
            hint = "Không xuống hết là ngực chưa làm việc đủ đâu 😎",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập chest fly nhưng tay khóa thẳng. Điều gì nguy hiểm?",
            options = listOf(
                "Đau cổ tay",
                "Đau vai",
                "Đau bụng",
                "Không sao"
            ),
            correctIndex = 1,
            hint = "Fly mà khóa tay là vai chịu hành trước ngực.",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Bench press nhưng thanh tạ chạm cổ thay vì ngực. Điều gì có thể xảy ra?",
            options = listOf(
                "Đau bụng",
                "Chấn thương vai – cổ",
                "Không sao",
                "Ngực to nhanh hơn"
            ),
            correctIndex = 1,
            hint = "Thanh tạ phải chạm ngực giữa. Chạm cổ là ticket đi bệnh viện 😭",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập incline bench quá dốc. Hệ quả?",
            options = listOf(
                "Thành bài vai nhiều hơn ngực trên",
                "Không ảnh hưởng",
                "Ngực to nhanh",
                "Tim đập nhanh hơn"
            ),
            correctIndex = 0,
            hint = "Incline quá dốc = tập vai, không còn chest nữa 😆",
            imageRes = R.drawable.quiz_fullbody_img1
        ),

        // IMG
        QuizQuestion(
            question = "So sánh form push-up đúng và sai. Form đúng là?",
            options = listOf(
                "Hông chổng lên",
                "Lưng thẳng, ngực hạ sâu",
                "Điều khiển bằng cổ tay",
                "Không cần xuống sâu"
            ),
            correctIndex = 1,
            hint = "Push-up chuẩn nhìn như plank di chuyển 😎",
            imageRes = R.drawable.quiz_chest_img1
        ),
        QuizQuestion(
            question = "Hình minh họa góc incline bench hợp lý. Góc đẹp nhất là?",
            options = listOf(
                "10°",
                "30–45°",
                "60°",
                "90°"
            ),
            correctIndex = 1,
            hint = "30–45° là chuẩn chỉnh cho ngực trên 💪",
            imageRes = R.drawable.quiz_chest_img2
        ),
        QuizQuestion(
            question = "Biểu đồ cơ tham gia khi bench. Cơ chính hoạt động là?",
            options = listOf(
                "Ngực",
                "Vai",
                "Bụng",
                "Lưng"
            ),
            correctIndex = 0,
            hint = "Bench mà không cảm nhận ngực thì form có vấn đề rồi đấy.",
            imageRes = R.drawable.quiz_chest_img3
        ),
        QuizQuestion(
            question = "Lịch chest 2 lần/tuần. Điều gì quan trọng nhất?",
            options = listOf(
                "Tập cả 2 buổi cực nặng",
                "Có ngày nghỉ hồi phục giữa buổi",
                "Chỉ tập bài máy",
                "Chỉ tập push-up"
            ),
            correctIndex = 1,
            hint = "Tập nặng nhưng phải cho cơ hồi phục nữa 😎",
            imageRes = R.drawable.quiz_chest_img4
        ),
        QuizQuestion(
            question = "Trước – sau 8 tuần tập chest. Yếu tố quyết định?",
            options = listOf(
                "Thay bài liên tục",
                "Kiên trì + tăng tải hợp lý",
                "Ăn càng nhiều càng tốt",
                "Uống supplement là chính"
            ),
            correctIndex = 1,
            hint = "Tăng progressive overload + đều đặn là chân ái.",
            imageRes = R.drawable.quiz_chest_img5
        )
    )


    // ---------------- ARM ----------------

    private val armLv1 = listOf(
            QuizQuestion(
                question = "Với buổi ARM, điều gì quan trọng nhất để tay không bị lệch size?",
                options = listOf(
                    "Chỉ tập tay thuận cho to trước",
                    "Tập đủ rep, tạ đều cho cả hai tay",
                    "Tăng tạ liên tục, bất chấp form",
                    "Tập tay trước, bỏ tay sau"
                ),
                correctIndex = 1,
                hint = "Rep và tạ cân bằng cho cả hai tay. Đừng để tay phải là Hulk, tay trái là civilian."
            ),
            QuizQuestion(
                question = "Khi curl tạ tay (biceps curl), lỗi phổ biến là:",
                options = listOf(
                    "Gồng core",
                    "Lắc cả người, dùng lưng và vai để kéo tạ",
                    "Giữ khuỷu tay cố định",
                    "Thở ra khi lên"
                ),
                correctIndex = 1,
                hint = "Lắc người là dùng quán tính, bắp tay thì nghỉ ngơi. Biceps không cần nhờ lưng ‘kéo hộ’."
            ),
            QuizQuestion(
                question = "Nhóm cơ nào chiếm phần lớn kích thước cánh tay?",
                options = listOf(
                    "Biceps (tay trước)",
                    "Triceps (tay sau)",
                    "Cẳng tay",
                    "Vai"
                ),
                correctIndex = 1,
                hint = "Triceps chiếm phần lớn khối lượng tay. Muốn tay to mà bỏ tay sau là sai bài toán."
            ),
            QuizQuestion(
                question = "Khi tập triceps với rope pushdown, nên chú ý điều gì?",
                options = listOf(
                    "Kéo bằng vai",
                    "Khuỷu tay cố định sát người, chỉ duỗi cẳng tay",
                    "Đung đưa người cho dễ kéo",
                    "Gập lưng để cúi xuống"
                ),
                correctIndex = 1,
                hint = "Triceps làm việc chính bằng động tác duỗi khuỷu tay. Vai, lưng chỉ giữ ổn định."
            ),
            QuizQuestion(
                question = "Tập ARM cho người mới, số buổi/tuần hợp lý là:",
                options = listOf(
                    "Mỗi ngày cho nhanh to",
                    "2 lần/tuần, kèm trong lịch PUSH/PULL/UPPER",
                    "1 tháng 1 lần",
                    "Chỉ tập khi muốn ‘show’ tay"
                ),
                correctIndex = 1,
                hint = "Tay làm việc trong nhiều bài compound rồi. Thêm 1–2 buổi tập riêng là đủ ‘nhiệt’."
            ),
            QuizQuestion(
                question = "Bài close-grip bench press chủ yếu nhắm vào:",
                options = listOf(
                    "Ngực giữa",
                    "Triceps",
                    "Vai trước",
                    "Cẳng tay"
                ),
                correctIndex = 1,
                hint = "Close-grip bench giảm ‘ăn’ ngực, tăng tải cho triceps – bài tay sau khá xịn."
            ),
            QuizQuestion(
                question = "Khi tập ARM, chọn tạ như thế nào là ổn?",
                options = listOf(
                    "Tạ nặng hết cỡ, rep được 2 cái là thôi",
                    "Tạ vừa để làm 8–15 rep với form đẹp",
                    "Tạ siêu nhẹ nhưng làm 100 rep",
                    "Tạ bằng với người tập bên cạnh"
                ),
                correctIndex = 1,
                hint = "Giống như ngực, tay cũng tăng tốt với khoảng 8–15 rep, tạ vừa sức, kiểm soát được."
            ),
            QuizQuestion(
                question = "Dấu hiệu nên dừng buổi ARM lại là:",
                options = listOf(
                    "Cơ tay mỏi, căng, hơi run",
                    "Đau nhói khớp cổ tay/khuỷu tay khi không nâng tạ",
                    "Cảm giác pump tay nhiều",
                    "Ra mồ hôi"
                ),
                correctIndex = 1,
                hint = "Đau khớp là chuyện khác với mỏi cơ. Khớp đau thì nghỉ và xem lại kỹ thuật, đừng cố thêm set."
            ),
            QuizQuestion(
                question = "Tập ARM chỉ tập tay trước mà bỏ tay sau sẽ dẫn đến:",
                options = listOf(
                    "Không sao, tay vẫn cân",
                    "Tay mất cân đối, dễ đau khuỷu do mất cân bằng cơ",
                    "Tay to nhanh hơn",
                    "Vai tự to theo"
                ),
                correctIndex = 1,
                hint = "Mặt trước – mặt sau phải cân nhau. Biceps/triceps cũng vậy, không thì khớp chịu trận."
            ),
            QuizQuestion(
                question = "Sau buổi ARM, cảm giác nào là bình thường?",
                options = listOf(
                    "Tay căng, khó gập/duỗi mạnh",
                    "Tê bì kéo dài, mất cảm giác",
                    "Đau khớp sắc bén khi nghỉ ngơi",
                    "Đau lan lên cổ"
                ),
                correctIndex = 0,
                hint = "Pump tay, căng tay là bình thường. Tê bì, đau khớp sắc bén thì nên dừng và đi check nếu kéo dài."
            )
        )
    private val armLv2 = listOf(
        QuizQuestion(
            question = "Khi curl tay trước, sai lầm phổ biến nhất là:",
            options = listOf(
                "Đứng thẳng, siết nhẹ core",
                "Vung người, nhún lưng để kéo tạ",
                "Giữ khuỷu gần thân người",
                "Điều khiển tạ lên xuống chậm"
            ),
            correctIndex = 1,
            hint = "Vung người là dùng lưng và quán tính, tay hưởng ké rất ít."
        ),
        QuizQuestion(
            question = "Để tay sau (triceps) phát triển, bài nào sau đây là compound tốt?",
            options = listOf(
                "Triceps pushdown",
                "Close-grip bench press",
                "Hammer curl",
                "Concentration curl"
            ),
            correctIndex = 1,
            hint = "Close-grip bench press là compound nhấn mạnh tay sau."
        ),
        QuizQuestion(
            question = "Nếu một tay yếu hơn rõ rệt, nên:",
            options = listOf(
                "Tập thêm rep cho tay khỏe",
                "Tập tạ đơn, số rep bằng nhau cho hai tay",
                "Chỉ tập tay yếu",
                "Không cần quan tâm"
            ),
            correctIndex = 1,
            hint = "Tạ đơn giúp hai tay làm việc độc lập, rep bằng nhau để cân bằng."
        ),
        QuizQuestion(
            question = "Số set arm trong 1 buổi riêng hợp lý cho người mới là:",
            options = listOf(
                "2–3 set tổng",
                "6–10 set cho cả tay trước và tay sau",
                "20 set mỗi bên tay",
                "Càng nhiều càng tốt"
            ),
            correctIndex = 1,
            hint = "6–10 set/nhóm cơ cho người mới là mức khá phổ biến."
        ),
        QuizQuestion(
            question = "Khi tập arm, cảm giác “cháy” cơ nhưng KHÔNG đau khớp nghĩa là:",
            options = listOf(
                "Tập sai hoàn toàn",
                "Cơ đang làm việc bình thường",
                "Phải dừng ngay lập tức",
                "Chưa đủ nặng"
            ),
            correctIndex = 1,
            hint = "Cảm giác nóng, căng cơ là bình thường. Đau khớp mới đáng lo."
        ),
        QuizQuestion(
            question = "Bài nào sau đây cũng ăn vào tay khá nhiều dù không phải buổi arm?",
            options = listOf(
                "Pull-up / lat pulldown",
                "Leg curl",
                "Calf raise",
                "Hip thrust"
            ),
            correctIndex = 0,
            hint = "Các bài kéo cho lưng đều huy động biceps hỗ trợ."
        ),
        QuizQuestion(
            question = "Tại sao không nên chỉ tập bài tập tay trước mà bỏ tay sau?",
            options = listOf(
                "Vì tay sau mới là “tay trước thật sự”",
                "Tay dễ bị mất cân bằng, khớp khuỷu chịu lực lệch",
                "Không vấn đề gì, miễn tay to",
                "Vì trông xấu"
            ),
            correctIndex = 1,
            hint = "Tay sau chiếm phần lớn khối lượng cánh tay, cần phát triển cân bằng với tay trước."
        ),
        QuizQuestion(
            question = "Khi pump tay xong thấy gân nổi và tay to hơn bình thường, điều này nghĩa là:",
            options = listOf(
                "Đã tăng cơ vĩnh viễn",
                "Chỉ là máu dồn vào cơ tạm thời",
                "Bị gì đó nguy hiểm",
                "Do thiếu nước"
            ),
            correctIndex = 1,
            hint = "Pump chỉ là hiệu ứng tạm thời do máu dồn vào cơ, không phải tăng cơ ngay lập tức."
        ),
        QuizQuestion(
            question = "Ngày hôm sau tay còn hơi đau cơ (DOMS) nhẹ, buổi arm tiếp theo nên:",
            options = listOf(
                "Vẫn tập nhưng giảm tạ một chút",
                "Tăng tạ mạnh để “phá cơ”",
                "Hủy tập toàn thân",
                "Bỏ luôn bài tay"
            ),
            correctIndex = 0,
            hint = "Đau nhẹ thì vẫn có thể tập, chỉ cần kiểm soát volume và tạ."
        ),
        QuizQuestion(
            question = "Vì sao nên siết nhẹ core khi tập arm đứng?",
            options = listOf(
                "Cho… đẹp dáng",
                "Ổn định người, tránh vung lưng và bảo vệ cột sống",
                "Không có lý do gì",
                "Để tập thêm cơ bụng"
            ),
            correctIndex = 1,
            hint = "Core ổn định giúp lực truyền vào tay tốt hơn và giảm vung người."
        )
    )
    private val armLv3 = listOf(
        QuizQuestion(
            question = "GIF: Người tập bicep curl nhưng dùng đà quá nhiều. Sai gì?",
            options = listOf(
                "Không kích hoạt tay trước hiệu quả",
                "Vai hoạt động nhiều hơn",
                "Không sao",
                "Chỉ là phong cách tập"
            ),
            correctIndex = 0,
            hint = "Curl mà như vung gậy thì tay trước nghỉ khỏe luôn 😆",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Tricep pushdown nhưng khuỷu tay bung ra ngoài. Sai ở đâu?",
            options = listOf(
                "Không tập trúng tay sau",
                "Cơ ngực hoạt động nhiều hơn",
                "Đùi to hơn",
                "Không sao"
            ),
            correctIndex = 0,
            hint = "Khuỷu phải cố định, bung ra là lệch bài liền.",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Hammer curl nhưng cổ tay gập mạnh. Hệ quả?",
            options = listOf(
                "Đau cổ tay",
                "Đau vai",
                "Không sao",
                "Ngực hoạt động nhiều hơn"
            ),
            correctIndex = 0,
            hint = "Tập tay mà cổ tay chịu trận là sai sai rồi 😭",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Close grip push-up nhưng tay đặt quá hẹp. Điều gì xảy ra?",
            options = listOf(
                "Chấn thương cổ tay",
                "Không tác động tay sau",
                "Không sao",
                "Tập vai nhiều hơn"
            ),
            correctIndex = 0,
            hint = "Quá hẹp là cổ tay khóc trước tay sau 😵‍💫",
            imageRes = R.drawable.quiz_fullbody_img1
        ),
        QuizQuestion(
            question = "GIF: Người tập cable curl nhưng ngả người ra sau. Sai ở đâu?",
            options = listOf(
                "Dùng lưng hỗ trợ",
                "Không ăn tay",
                "Sai nhịp thở",
                "Không sao"
            ),
            correctIndex = 0,
            hint = "Đứng thẳng – siết tay trước – đừng thành bài tập lưng 😂",
            imageRes = R.drawable.quiz_fullbody_img1
        ),

        // IMG
        QuizQuestion(
            question = "Minh họa form curl chuẩn. Điều gì đúng?",
            options = listOf(
                "Khuỷu cố định – nâng có kiểm soát",
                "Đánh đà mạnh",
                "Ngả người sau",
                "Khóa thẳng tay liên tục"
            ),
            correctIndex = 0,
            hint = "Curl đẹp là curl gọn – chắc – chuẩn 😎",
            imageRes = R.drawable.quiz_arm_img1
        ),
        QuizQuestion(
            question = "So sánh 3 bài tay trước. Bài nào ăn tay trước nhất?",
            options = listOf(
                "Preacher curl",
                "Lat pulldown",
                "Bench press",
                "Leg raise"
            ),
            correctIndex = 0,
            hint = "Preacher curl là thần thánh của bicep 😤",
            imageRes = R.drawable.quiz_arm_img2
        ),
        QuizQuestion(
            question = "Sơ đồ cơ tay sau. Bài nào kích hoạt mạnh nhất?",
            options = listOf(
                "Kickback",
                "Bench press",
                "Pull up",
                "Crunch"
            ),
            correctIndex = 0,
            hint = "Kickback đập đúng vào tay sau 🎯",
            imageRes = R.drawable.quiz_arm_img3
        ),
        QuizQuestion(
            question = "Lịch tập arm 2 lần/tuần hợp lý nhất?",
            options = listOf(
                "Liền 2 ngày",
                "Cách 2-3 ngày",
                "1 tuần 1 lần",
                "Ngày nào cũng tập"
            ),
            correctIndex = 1,
            hint = "Cho tay hồi phục mới to được 😉",
            imageRes = R.drawable.quiz_arm_img4
        ),
        QuizQuestion(
            question = "Trước / sau 6 tuần tập tay. Điều gì quan trọng nhất?",
            options = listOf(
                "Ăn nhiều thật nhiều",
                "Tăng tải dần + kỹ thuật chuẩn",
                "Chỉ tập tay mỗi ngày",
                "Uống supplement"
            ),
            correctIndex = 1,
            hint = "Form chuẩn + tăng tải hợp lý = tay to 😎",
            imageRes = R.drawable.quiz_arm_img5
        )
    )

}
