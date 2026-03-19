package com.healthgenieai.app.ui.fitness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.healthgenieai.app.R

class FitnessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_fitness, container, false)

        val cardFullBody = view.findViewById<LinearLayout>(R.id.cardFullBody)
        val cardAbs = view.findViewById<LinearLayout>(R.id.cardAbs)
        val cardFatBurn = view.findViewById<LinearLayout>(R.id.cardFatBurn)

        // FULL BODY WORKOUT
        cardFullBody.setOnClickListener {

            openWorkout(
                arrayListOf(
                    "Jumping Jacks",
                    "Push Ups",
                    "Squats",
                    "Mountain Climbers",
                    "Burpees"
                )
            )
        }

        // ABS WORKOUT
        cardAbs.setOnClickListener {

            openWorkout(
                arrayListOf(
                    "Crunches",
                    "Leg Raises",
                    "Russian Twist",
                    "Plank",
                    "Bicycle Crunch"
                )
            )
        }

        // FAT BURN WORKOUT
        cardFatBurn.setOnClickListener {

            openWorkout(
                arrayListOf(
                    "High Knees",
                    "Burpees",
                    "Jumping Jacks",
                    "Mountain Climbers",
                    "Butt Kicks"
                )
            )
        }

        return view
    }

    private fun openWorkout(exercises: ArrayList<String>) {

        val fragment = WorkoutSessionFragment()

        val bundle = Bundle()
        bundle.putStringArrayList("exercises", exercises)

        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}