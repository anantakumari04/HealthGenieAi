package com.example.healthgenieai.network

import com.example.healthgenieai.models.GeminiRequest
import com.example.healthgenieai.models.GeminiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}