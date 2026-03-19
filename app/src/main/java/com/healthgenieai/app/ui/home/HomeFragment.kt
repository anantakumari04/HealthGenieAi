package com.healthgenieai.app.ui.home

import android.app.AlertDialog
import android.content.Context
import android.hardware.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.healthgenieai.app.MainActivity
import com.healthgenieai.app.R
import com.healthgenieai.app.ui.maps.HospitalMapFragment
import com.healthgenieai.app.ui.reminder.ReminderFragment
import com.healthgenieai.app.ui.report.WeeklyReportFragment
import com.healthgenieai.app.utils.DateUtils
import com.healthgenieai.app.utils.HealthStorage

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private lateinit var tvSteps: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvWater: TextView
    private lateinit var tvStepGoal: TextView

    private lateinit var tvBMI: TextView
    private lateinit var tvBMICategory: TextView

    private var initialSteps = -1
    private var resetOffset = 0

    private var waterCount = 0
    private var waterGoal = 8
    private var stepGoal = 10000

    private var userWeight = 60f

    private lateinit var stepProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvSteps = view.findViewById(R.id.tvSteps)
        tvCalories = view.findViewById(R.id.tvCalories)
        tvWater = view.findViewById(R.id.tvWater)
        tvStepGoal = view.findViewById(R.id.tvStepGoal)

        tvBMI = view.findViewById(R.id.tvBMI)
        tvBMICategory = view.findViewById(R.id.tvBMICategory)

        stepProgress = view.findViewById(R.id.stepProgress)

        val btnAddWater = view.findViewById<Button>(R.id.btnAddWater)

        setupStepGoal(view)
        setupWaterTracker(btnAddWater)
        setupNavigation(view)
        setupSensors()

        calculateBMI()

        return view
    }

    // ---------------- STEP GOAL ----------------

    private fun setupStepGoal(view: View) {

        val prefs = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

        stepGoal = prefs.getInt("goal", 10000)
        resetOffset = prefs.getInt("reset", 0)

        tvStepGoal.text = "Goal: $stepGoal"

        val stepCard = tvSteps.parent as View

        stepCard.setOnLongClickListener {

            val options = arrayOf("Set Goal", "Reset Steps")

            AlertDialog.Builder(requireContext())
                .setTitle("Steps Options")
                .setItems(options) { _, which ->

                    when (which) {
                        0 -> showGoalDialog()
                        1 -> resetSteps()
                    }
                }
                .show()

            true
        }
    }

    private fun showGoalDialog() {

        val input = EditText(requireContext())
        input.hint = "Enter step goal"

        AlertDialog.Builder(requireContext())
            .setTitle("Set Step Goal")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->

                val goal = input.text.toString().toIntOrNull() ?: return@setPositiveButton

                stepGoal = goal

                requireContext()
                    .getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("goal", goal)
                    .apply()

                tvStepGoal.text = "Goal: $goal"
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetSteps() {

        val prefs = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

        resetOffset = initialSteps

        prefs.edit().putInt("reset", resetOffset).apply()

        tvSteps.text = "0"
    }

    // ---------------- BMI ----------------

    private fun calculateBMI() {

        val prefs = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        val heightCm = prefs.getFloat("height", 170f)
        val weightKg = prefs.getFloat("weight", 60f)

        val heightM = heightCm / 100
        val bmi = weightKg / (heightM * heightM)

        tvBMI.text = String.format("%.1f", bmi)

        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Healthy"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }

        tvBMICategory.text = category
    }

    // ---------------- WATER TRACKER ----------------

    private fun setupWaterTracker(btnAddWater: Button) {

        val prefs = requireContext().getSharedPreferences("water_prefs", Context.MODE_PRIVATE)

        waterGoal = prefs.getInt("goal", 8)

        val today = DateUtils.todayKey()

        if (prefs.getString("day", "") != today) {

            waterCount = 0

            prefs.edit()
                .putString("day", today)
                .putInt("count", 0)
                .apply()

        } else {

            waterCount = prefs.getInt("count", 0)
        }

        updateWaterUI()

        btnAddWater.setOnClickListener {

            waterCount++

            if (waterCount >= waterGoal) {

                Toast.makeText(requireContext(),
                    "🎉 Water goal completed!", Toast.LENGTH_SHORT).show()

                waterCount = 0
            }

            prefs.edit().putInt("count", waterCount).apply()

            updateWaterUI()
        }

        tvWater.setOnClickListener {
            showWaterGoalDialog()
        }

        tvWater.setOnLongClickListener {
            showWaterGoalInputDialog()
            true
        }
    }

    private fun showWaterGoalInputDialog() {

        val input = EditText(requireContext())
        input.hint = "Enter water goal (glasses)"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(requireContext())
            .setTitle("Custom Water Goal")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->

                val goal = input.text.toString().toIntOrNull() ?: return@setPositiveButton

                waterGoal = goal
                waterCount = 0

                requireContext()
                    .getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("goal", goal)
                    .putInt("count", 0)
                    .apply()

                updateWaterUI()
            }
            .setNegativeButton("Cancel", null)
            .show()
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

                    waterCount = 0

                    requireContext()
                        .getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("goal", waterGoal)
                        .putInt("count", 0)
                        .apply()

                    updateWaterUI()

                    dismiss()
                }

            }.show()
    }

    private fun updateWaterUI() {

        tvWater.text = "$waterCount / $waterGoal"
    }

    // ---------------- NAVIGATION ----------------

    private fun setupNavigation(view: View) {

        view.findViewById<View>(R.id.btnShowWeeklyReport).setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, WeeklyReportFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.cardReminder).setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, ReminderFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnFindHospital).setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, HospitalMapFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.cardAiChat).setOnClickListener {

            (activity as? MainActivity)?.navigateFromHome(R.id.nav_chat)
        }

        view.findViewById<LinearLayout>(R.id.cardDietPlan).setOnClickListener {

            (activity as? MainActivity)?.navigateFromHome(R.id.nav_diet)
        }

        view.findViewById<LinearLayout>(R.id.cardWorkout).setOnClickListener {

            (activity as? MainActivity)?.navigateFromHome(R.id.nav_fitness)
        }
    }

    // ---------------- SENSOR ----------------

    private fun setupSensors() {

        sensorManager =
            requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onResume() {
        super.onResume()

        calculateBMI()

        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

        val totalSteps = event.values[0].toInt()

        if (initialSteps == -1) initialSteps = totalSteps

        val currentSteps = totalSteps - resetOffset

        tvSteps.text = currentSteps.toString()

        val calories = currentSteps * userWeight * 0.0005f
        tvCalories.text = String.format("%.1f kcal", calories)

        val progressPercent = (currentSteps * 100) / stepGoal

        stepProgress.progress = progressPercent.coerceAtMost(100)

        HealthStorage.saveToday(
            requireContext(),
            currentSteps,
            calories,
            waterCount
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}