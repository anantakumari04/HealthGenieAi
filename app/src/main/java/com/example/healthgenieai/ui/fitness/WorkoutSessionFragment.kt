package com.example.healthgenieai.ui.fitness

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.healthgenieai.R

class WorkoutSessionFragment : Fragment() {

    private lateinit var tvExercise: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvSet: TextView
    private lateinit var imgExercise: ImageView

    private var exercises: ArrayList<String> = arrayListOf()

    private var exerciseIndex = 0
    private var seconds = 60

    private var set = 1
    private val maxSet = 3

    private var calories = 0

    private val handler = Handler(Looper.getMainLooper())

    private val exerciseGifMap: Map<String, Int> = mapOf(

        "Push Ups" to R.drawable.pushups,
        "Squats" to R.drawable.squats,
        "Plank" to R.drawable.plank,
        "Crunches" to R.drawable.crunches,
        "Burpees" to R.drawable.burpees,
        "Jumping Jacks" to R.drawable.jumping_jacks,
        "Mountain Climbers" to R.drawable.mountain_climbers,
        "High Knees" to R.drawable.high_knees,
        "Lunges" to R.drawable.lunges,
        "Russian Twist" to R.drawable.russian_twist,
        "Leg Raises" to R.drawable.leg_raises,
        "Bicycle Crunch" to R.drawable.bicycle_crunch,
        "Wall Sit" to R.drawable.wall_sit,
        "Side Plank" to R.drawable.side_plank,
        "Glute Bridge" to R.drawable.glute_bridge,
        "Donkey Kicks" to R.drawable.donkey_kicks,
        "Butt Kicks" to R.drawable.butt_kicks,
        "Arm Circles" to R.drawable.arm_circles,
        "Superman" to R.drawable.superman,
        "Reverse Crunch" to R.drawable.reverse_crunch
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_workout_session, container, false)

        tvExercise = view.findViewById(R.id.tvExercise)
        tvTimer = view.findViewById(R.id.tvTimer)
        tvSet = view.findViewById(R.id.tvSet)
        imgExercise = view.findViewById(R.id.imgExercise)

        exercises = arguments?.getStringArrayList("exercises") ?: arrayListOf()

        startWorkout()

        return view
    }

    private fun startWorkout() {

        if (exercises.isEmpty()) return

        exerciseIndex = 0
        set = 1
        seconds = 60

        updateExercise()

        handler.post(timerRunnable)
    }

    private val timerRunnable: Runnable = object : Runnable {

        override fun run() {

            if (seconds >= 0) {

                tvTimer.text = "00:${String.format("%02d", seconds)}"
                tvSet.text = "Set $set / $maxSet"

                seconds--
                calories++

                handler.postDelayed(this, 1000)

            } else {

                exerciseIndex++

                if (exerciseIndex < exercises.size) {

                    seconds = 60
                    updateExercise()

                    Toast.makeText(
                        requireContext(),
                        "Next Exercise",
                        Toast.LENGTH_SHORT
                    ).show()

                    handler.postDelayed(this, 2000)

                } else {

                    if (set < maxSet) {

                        set++
                        exerciseIndex = 0
                        seconds = 60

                        tvTimer.text = "Rest 15 sec"

                        Toast.makeText(
                            requireContext(),
                            "Take Rest",
                            Toast.LENGTH_SHORT
                        ).show()

                        handler.postDelayed({

                            updateExercise()
                            handler.post(timerRunnable)

                        }, 15000)

                    } else {

                        tvTimer.text = "Workout Completed 🎉"

                        Toast.makeText(
                            requireContext(),
                            "Calories Burned: $calories kcal",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateExercise() {

        val exerciseName = exercises[exerciseIndex]

        tvExercise.text = exerciseName

        val gifRes = exerciseGifMap[exerciseName] ?: R.drawable.pushups

        Glide.with(this)
            .asGif()
            .load(gifRes)
            .into(imgExercise)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}