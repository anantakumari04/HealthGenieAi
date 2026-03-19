//package com.example.healthgenieai.ui.fitness.api
//
//import com.example.healthgenieai.ui.fitness.model.ExerciseResponse
//import retrofit2.Call
//import retrofit2.http.GET
//import retrofit2.http.Headers
//
//interface ExerciseApi {
//
//    @Headers(
//        "X-RapidAPI-Key: YOUR_API_KEY",
//        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
//    )
//    @GET("exercises?limit=20")
//    fun getExercises(): Call<List<ExerciseResponse>>
//}