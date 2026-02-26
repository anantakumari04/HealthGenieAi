package com.example.healthgenieai.ui.home

import android.app.AlertDialog
import android.content.Context
import android.hardware.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.healthgenieai.R
import com.example.healthgenieai.ui.reminder.ReminderFragment
import com.example.healthgenieai.ui.report.WeeklyReportFragment
import com.example.healthgenieai.utils.*

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private lateinit var tvSteps: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvWater: TextView

    private var initialSteps = -1
    private var waterCount = 0
    private var waterGoal = 8
    private var userWeight = 60f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvSteps = view.findViewById(R.id.tvSteps)
        tvCalories = view.findViewById(R.id.tvCalories)
        tvWater = view.findViewById(R.id.tvWater)
        val btnAddWater = view.findViewById<Button>(R.id.btnAddWater)

        val prefs = requireContext().getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
        waterGoal = prefs.getInt("goal", 8)

        val today = DateUtils.todayKey()
        if (prefs.getString("day", "") != today) {
            waterCount = 0
            prefs.edit().putString("day", today).putInt("count", 0).apply()
        } else {
            waterCount = prefs.getInt("count", 0)
        }

        updateWaterUI()

        btnAddWater.setOnClickListener {
            if (waterCount < waterGoal) {
                waterCount++
                prefs.edit().putInt("count", waterCount).apply()
                updateWaterUI()
            }
        }

        tvWater.setOnClickListener { showWaterGoalDialog() }

        view.findViewById<View>(R.id.btnShowWeeklyReport).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, WeeklyReportFragment())
                .addToBackStack(null)
                .commit()
        }

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        view.findViewById<View>(R.id.cardReminder).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, ReminderFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showWaterGoalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_water_goal, null)
        val rg = dialogView.findViewById<RadioGroup>(R.id.rgWaterGoal)

        when (waterGoal) {
            6 -> rg.check(R.id.rb6)
            8 -> rg.check(R.id.rb8)
            10 -> rg.check(R.id.rb10)
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
            .apply {
                dialogView.findViewById<Button>(R.id.btnSaveGoal).setOnClickListener {
                    waterGoal = when (rg.checkedRadioButtonId) {
                        R.id.rb6 -> 6
                        R.id.rb10 -> 10
                        else -> 8
                    }
                    requireContext().getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
                        .edit().putInt("goal", waterGoal).apply()
                    updateWaterUI()
                    dismiss()
                }
            }.show()
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val totalSteps = event.values[0].toInt()
        if (initialSteps == -1) initialSteps = totalSteps
        val currentSteps = totalSteps - initialSteps

        tvSteps.text = currentSteps.toString()
        val calories = currentSteps * userWeight * 0.0005f
        tvCalories.text = String.format("%.1f kcal", calories)

        HealthStorage.saveToday(requireContext(), currentSteps, calories, waterCount)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateWaterUI() {
        tvWater.text = "$waterCount / $waterGoal glasses"
    }
}