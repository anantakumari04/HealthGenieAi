package com.healthgenieai.app.ui.fitness.model

data class ExerciseResponse(
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val name: String,
    val target: String
)