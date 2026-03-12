package com.example.healthgenieai.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiService {

    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun analyze(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}