package com.example.fitness.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.api.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages = _messages.asStateFlow()

    private val SYSTEM_PROMPT = """
        Bạn là một huấn luyện viên gym chuyên nghiệp, giàu kinh nghiệm, nhiệt tình và rất quan tâm đến học viên.
        Bạn đang trò chuyện trực tiếp 1-1 với học viên của mình như một người thầy đáng tin cậy.
        Phong cách trả lời:
        - Gần gũi, tích cực, luôn động viên, khích lệ.
        - Ngắn gọn nhưng đủ ý, dễ hiểu, không dài dòng.
        - Dùng tiếng Việt tự nhiên, xưng "Tôi" với học viên.
        - Không chào hỏi hay kết thúc kiểu xã giao.
        - Nếu khen thì khen thật, nếu sửa thì sửa rõ ràng kèm cách khắc phục tích cực.
        - Luôn tạo cảm giác học viên đang được hướng dẫn tận tình.
    """.trimIndent()

    fun sendMessage(question: String) {
        if (question.trim().isBlank()) return
        addMessage(question, true)

        viewModelScope.launch {
            GeminiClient.askGeminiWithImage(
                question = "$SYSTEM_PROMPT\n\nUser: $question",
                imageBytes = null,
                apiKey = Constants.apiKey
            ) { reply ->
                addMessage(reply.trim(), false)
            }
        }
    }

    fun sendImageForAnalysis(imageBytes: ByteArray) {
        addMessage("Để mình xem kỹ tư thế của bạn nào... một chút nhé!", false)

        viewModelScope.launch {
            GeminiClient.askGeminiWithImage(
                question = """
                    $SYSTEM_PROMPT
                    
                    Đây là ảnh tập gym của học viên gửi lên.
                    Hãy phân tích tư thế:
                    - Chỉ ra 1-2 lỗi nghiêm trọng nhất (nếu có).
                    - Đưa cách sửa cụ thể, dễ làm theo.
                    - Luôn động viên, khích lệ dù có lỗi.
                    - Nếu tư thế đẹp thì khen thật to và góp ý nhỏ để hoàn hảo hơn.
                """.trimIndent(),
                imageBytes = imageBytes,
                apiKey = Constants.apiKey
            ) { answer ->
                removeLoadingMessage()
                addMessage(answer.trim(), false)
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        _messages.value = _messages.value + (text to isUser)
    }

    private fun removeLoadingMessage() {
        _messages.value = _messages.value.dropLastWhile {
            !it.second && it.first.contains("xem kỹ tư thế", ignoreCase = true)
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    fun checkModels(onResult: (String) -> Unit) {
        GeminiClient.listModels(Constants.apiKey, onResult)
    }
}
