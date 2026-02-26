package com.example.healthgenieai.ui.model


data class DailyHealthData(
    val day: String,
    val steps: Int,
    val calories: Float,
    val water: Int
)