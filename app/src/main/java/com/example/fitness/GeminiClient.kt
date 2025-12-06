package com.example.fitness.api

import android.util.Base64
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val MODEL_NAME = "gemini-2.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun askGemini(question: String, apiKey: String, callback: (String) -> Unit) {
        askGeminiWithImage(question, null, apiKey, callback)
    }

    fun askGeminiWithImage(
        question: String? = null,
        imageBytes: ByteArray? = null,
        apiKey: String,
        callback: (String) -> Unit
    ) {
        val url = "https://generativelanguage.googleapis.com/v1/models/$MODEL_NAME:generateContent?key=$apiKey"

        val partsArray = JSONArray()

        question?.takeIf { it.isNotBlank() }?.let {
            partsArray.put(JSONObject().apply { put("text", it) })
        }

        imageBytes?.takeIf { it.isNotEmpty() }?.let { bytes ->
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val mime = if (bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte()) "image/jpeg" else "image/png"
            partsArray.put(JSONObject().apply {
                put("inlineData", JSONObject().apply {
                    put("mimeType", mime)
                    put("data", base64)
                })
            })
        }

        if (partsArray.length() == 0) {
            callback("Không có nội dung để gửi")
            return
        }

        val json = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("parts", partsArray)
            }))
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback("Lỗi mạng: ${e.message}")
            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string() ?: ""
                try {
                    val jsonRes = JSONObject(res)
                    if (jsonRes.has("error")) {
                        callback("Lỗi API: ${jsonRes.getJSONObject("error").optString("message")}")
                        return
                    }
                    val text = jsonRes.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    callback(text.trim())
                } catch (e: Exception) {
                    callback("Lỗi xử lý: ${e.message}\nRaw: $res")
                }
            }
        })
    }

    fun listModels(apiKey: String, callback: (String) -> Unit) {
        val url = "https://generativelanguage.googleapis.com/v1/models?key=$apiKey"
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback("Lỗi: ${e.message}")
            override fun onResponse(call: Call, response: Response) = callback(response.body?.string() ?: "")
        })
    }
}