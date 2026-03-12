//package com.example.healthgenieai.api
//
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.http.Body
//import retrofit2.http.POST
//import retrofit2.http.Query
//
//interface GeminiApi {
//
//    @POST("v1/models/gemini-1.5-flash:generateContent")
//    fun askGemini(
//        @Query("key") apiKey: String,
//        @Body body: RequestBody
//    ): Call<String>
//}