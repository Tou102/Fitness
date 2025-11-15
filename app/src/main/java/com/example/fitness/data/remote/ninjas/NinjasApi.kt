package com.example.fitness.data.remote.ninjas

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Giao tiếp với API Ninjas (https://api.api-ninjas.com/v1/nutrition)
 *
 * Header X-Api-Key đã được thêm tự động trong NetworkModule,
 * nên KHÔNG cần truyền tham số key ở đây.
 */
interface NinjasApi {
    @GET("v1/nutrition")
    suspend fun getNutrition(
        @Query("query") query: String
    ): List<NinjaFood>
}
