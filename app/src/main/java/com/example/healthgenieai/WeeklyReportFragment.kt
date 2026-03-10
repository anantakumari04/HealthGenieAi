package com.example.healthgenieai.ui.report

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.healthgenieai.R
import com.example.healthgenieai.utils.*
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlin.collections.mapIndexed

class WeeklyReportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_weekly_report, container, false)
        val data = HealthStorage.getWeeklyData(requireContext())

        setupSteps(view.findViewById(R.id.stepsChart), data)
        setupCalories(view.findViewById(R.id.caloriesChart), data)
        setupWater(view.findViewById(R.id.waterChart), data)

        return view
    }

    private fun setupSteps(chart: BarChart, data: List<com.example.healthgenieai.ui.model.DailyHealthData>) {
        val entries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.steps.toFloat()) }
        val set = BarDataSet(entries, "Steps 👣")
        set.color = Color.parseColor("#4F46E5")
        chart.data = BarData(set)
        styleXAxis(chart)
    }

    private fun setupCalories(chart: LineChart, data: List<com.example.healthgenieai.ui.model.DailyHealthData>) {
        val entries = data.mapIndexed { i, d -> Entry(i.toFloat(), d.calories) }
        val set = LineDataSet(entries, "Calories 🔥")
        set.color = Color.parseColor("#F97316")
        set.circleRadius = 5f
        set.setCircleColor(Color.parseColor("#F97316"))
        chart.data = LineData(set)
        styleXAxis(chart)
    }

    private fun setupWater(chart: BarChart, data: List<com.example.healthgenieai.ui.model.DailyHealthData>) {
        val entries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.water.toFloat()) }
        val set = BarDataSet(entries, "Water 💧")
        set.color = Color.parseColor("#06B6D4")
        chart.data = BarData(set)
        styleXAxis(chart)
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