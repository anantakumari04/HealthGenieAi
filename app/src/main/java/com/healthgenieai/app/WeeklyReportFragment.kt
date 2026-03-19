package com.healthgenieai.app.ui.report

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.healthgenieai.app.R
import com.healthgenieai.app.utils.*
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class WeeklyReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_weekly_report, container, false)

        val data = HealthStorage.getWeeklyData(requireContext())

        val tvTotalSteps = view.findViewById<TextView>(R.id.tvTotalSteps)
        val tvTotalCalories = view.findViewById<TextView>(R.id.tvTotalCalories)
        val tvTotalWater = view.findViewById<TextView>(R.id.tvTotalWater)

        val totalSteps = data.sumOf { it.steps }
        val totalCalories = data.sumOf { it.calories.toDouble() }
        val totalWater = data.sumOf { it.water }

        tvTotalSteps.text = totalSteps.toString()
        tvTotalCalories.text = "${totalCalories.toInt()} kcal"
        tvTotalWater.text = "$totalWater glasses"

        setupSteps(view.findViewById(R.id.stepsChart), data)
        setupCalories(view.findViewById(R.id.caloriesChart), data)
        setupWater(view.findViewById(R.id.waterChart), data)
        setupPie(view.findViewById(R.id.caloriePieChart), data)

        return view
    }

    private fun setupSteps(chart: BarChart, data: List<com.healthgenieai.app.ui.model.DailyHealthData>) {

        val entries = data.mapIndexed { i, d ->
            BarEntry(i.toFloat(), d.steps.toFloat())
        }

        val set = BarDataSet(entries, "Steps 👣")
        set.color = Color.parseColor("#6366F1")

        chart.data = BarData(set)

        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false

        styleXAxis(chart)
    }

    private fun setupCalories(chart: LineChart, data: List<com.healthgenieai.app.ui.model.DailyHealthData>) {

        val entries = data.mapIndexed { i, d ->
            Entry(i.toFloat(), d.calories)
        }

        val set = LineDataSet(entries, "Calories 🔥")
        set.color = Color.parseColor("#F97316")
        set.circleRadius = 5f
        set.setCircleColor(Color.parseColor("#F97316"))

        chart.data = LineData(set)

        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false

        styleXAxis(chart)
    }

    private fun setupWater(chart: BarChart, data: List<com.healthgenieai.app.ui.model.DailyHealthData>) {

        val entries = data.mapIndexed { i, d ->
            BarEntry(i.toFloat(), d.water.toFloat())
        }

        val set = BarDataSet(entries, "Water 💧")
        set.color = Color.parseColor("#06B6D4")

        chart.data = BarData(set)

        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false

        styleXAxis(chart)
    }

    private fun setupPie(chart: PieChart, data: List<com.healthgenieai.app.ui.model.DailyHealthData>) {

        val totalCalories = data.sumOf { it.calories.toDouble() }.toFloat()
        val totalSteps = data.sumOf { it.steps }.toFloat()
        val totalWater = data.sumOf { it.water }.toFloat()

        val entries = listOf(
            PieEntry(totalCalories, "Calories"),
            PieEntry(totalSteps, "Steps"),
            PieEntry(totalWater, "Water")
        )

        val set = PieDataSet(entries, "")
        set.colors = listOf(
            Color.parseColor("#F97316"),
            Color.parseColor("#6366F1"),
            Color.parseColor("#06B6D4")
        )

        chart.data = PieData(set)

        chart.description.isEnabled = false
        chart.animateY(900)

        chart.invalidate()
    }

    private fun styleXAxis(chart: BarLineChartBase<*>) {

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(DateUtils.weekDays())
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f

        chart.axisRight.isEnabled = false

        chart.animateY(800)
        chart.invalidate()
    }
}