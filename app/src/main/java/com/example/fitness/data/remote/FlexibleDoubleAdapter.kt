package com.example.fitness.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import com.squareup.moshi.JsonReader.Token

/**
 * Adapter giúp Moshi parse Double? dù server trả về số hoặc chuỗi.
 * Nếu gặp "Only available for premium subscribers." → trả null.
 */
class FlexibleDoubleAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Double? {
        return when (reader.peek()) {
            Token.NUMBER -> runCatching { reader.nextDouble() }.getOrNull()
            Token.STRING -> { reader.nextString(); null } // bỏ qua chuỗi
            Token.NULL -> { reader.nextNull<Unit>(); null }
            else -> { reader.skipValue(); null }
        }
    }

    @ToJson
    fun toJson(value: Double?): Double? = value
}
