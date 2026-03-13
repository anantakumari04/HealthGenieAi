package com.example.healthgenieai.ui.fitness

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.healthgenieai.R

class FitnessFragment : Fragment() {

    private lateinit var tvExercise: TextView

    private var exerciseIndex = 0
    private val exercises = listOf(
        "Push Ups",
        "Squats",
        "Plank"
    )
    private var seconds = 60
    private var isRunning = false
    private lateinit var timerText: TextView
    private lateinit var startBtn: Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_fitness, container, false)

        timerText = view.findViewById(R.id.tvTimer)
        startBtn = view.findViewById(R.id.btnStartWorkout)
        tvExercise = view.findViewById(R.id.tvCurrentExercise)
        tvExercise.text = exercises[exerciseIndex]
        timerText.text = "Next: ${exercises[exerciseIndex]}"

        startBtn.setOnClickListener {

            if (!isRunning) {
                isRunning = true
                startBtn.text = "Workout Running..."

                tvExercise.text = exercises[exerciseIndex]
                val handler = Handler()



                handler.post(object : Runnable {
                    override fun run() {

                        if (seconds >= 0) {
                            timerText.text =
                                "00 : ${String.format("%02d", seconds)}"
                            seconds--
                            handler.postDelayed(this, 1000)
                        } else {

                            exerciseIndex++

                            if (exerciseIndex < exercises.size) {

                                timerText.text = "Next: ${exercises[exerciseIndex]}"
                                seconds = 60

                                handler.postDelayed(this, 2000)

                            } else {

                                timerText.text = "Workout Completed ✅"
                                startBtn.text = "START AGAIN"

                                exerciseIndex = 0
                                seconds = 60
                                isRunning = false
                            }
                        }
                    }
                })
            }
        }

        return view
    }
}