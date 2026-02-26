package com.example.healthgenieai.utils

import android.content.Context
import com.example.healthgenieai.ui.model.DailyHealthData
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object HealthStorage {

    private const val PREF = "weekly_health"

    fun saveToday(context: Context, steps: Int, calories: Float, water: Int) {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val key = DateUtils.todayKey()

        val json = JSONObject()
        json.put("steps", steps)
        json.put("calories", calories)
        json.put("water", water)

        prefs.edit().putString(key, json.toString()).apply()
    }

    fun getWeeklyData(context: Context): List<DailyHealthData> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val result = mutableListOf<DailyHealthData>()

        for (i in 0..6) {
            val key = sdf.format(cal.time)
            val jsonStr = prefs.getString(key, null)

            if (jsonStr != null) {
                val obj = JSONObject(jsonStr)
                result.add(
                    DailyHealthData(
                        DateUtils.weekDays()[i],
                        obj.getInt("steps"),
                        obj.getDouble("calories").toFloat(),
                        obj.getInt("water")
                    )
                )
            } else {
                result.add(DailyHealthData(DateUtils.weekDays()[i], 0, 0f, 0))
            }
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun resetWeekly(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}