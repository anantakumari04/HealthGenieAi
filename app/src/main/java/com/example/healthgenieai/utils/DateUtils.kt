package com.example.healthgenieai.utils



import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun todayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun currentWeekKey(): String {
        val cal = Calendar.getInstance()
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        return "$year-week-$week"
    }

    fun weekDays(): List<String> =
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
}