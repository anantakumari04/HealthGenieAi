package com.example.healthgenieai.ui.reminder

import android.app.*
import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.healthgenieai.R
import java.util.*

class ReminderFragment : Fragment() {

    private var hour = 0
    private var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_reminder, container, false)

        val etMedicine = view.findViewById<EditText>(R.id.etMedicineName)
        val tvTime = view.findViewById<TextView>(R.id.tvSelectedTime)

        view.findViewById<Button>(R.id.btnPickTime).setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                hour = h
                minute = m
                tvTime.text = "Time: %02d:%02d".format(h, m)
            }, 10, 0, false).show()
        }

        view.findViewById<Button>(R.id.btnSaveReminder).setOnClickListener {
            val medicine = etMedicine.text.toString()

            if (medicine.isEmpty()) {
                Toast.makeText(requireContext(), "Enter medicine name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            scheduleAlarm(medicine)
            Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun scheduleAlarm(medicine: String) {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        intent.putExtra("medicine", medicine)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}