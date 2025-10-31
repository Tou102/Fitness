package com.example.fitness.data.remote

import com.example.fitness.BuildConfig
import com.example.fitness.data.remote.ninjas.NinjasApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Module cấu hình Retrofit + OkHttpClient + Moshi.
 * Tự động gắn X-Api-Key từ BuildConfig.NINJAS_API_KEY.
 */
object NetworkModule {

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            // log request + response để debug
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            // Interceptor gắn API Key vào mọi request
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("X-Api-Key", BuildConfig.NINJAS_API_KEY)
                    .build()
                chain.proceed(req)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(FlexibleDoubleAdapter()) // Cho phép parse số hoặc chuỗi ("premium only")
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.NINJAS_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    // API duy nhất cho Nutrition
    val ninjasApi: NinjasApi by lazy {
        retrofit.create(NinjasApi::class.java)
    }
}
