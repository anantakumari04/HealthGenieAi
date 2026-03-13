//package com.example.healthgenieai.ui.fitness.api
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitInstance {
//
//    val api: ExerciseApi by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://exercisedb.p.rapidapi.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ExerciseApi::class.java)
//    }
//}