package com.example.blogginapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.blogginapp.databinding.FragmentDescriptionBinding
import com.example.blogginapp.databinding.FragmentReminderBinding


class ReminderFragment : Fragment() {

    private lateinit var binding: FragmentReminderBinding
    private lateinit var alarmManager: AlarmManager
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 234, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReminderBinding.inflate(inflater, container, false)
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        binding.button.setOnClickListener {
            val timeInSec = binding.time.text.toString().toInt()
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (timeInSec * 1000),
                pendingIntent
            )
            Toast.makeText(requireContext(), "Alarm set in $timeInSec seconds", Toast.LENGTH_LONG).show()
        }

        binding.RStart.setOnClickListener {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                5000,
                pendingIntent
            )
            Toast.makeText(requireContext(), "Repeating Alarm 5 Seconds", Toast.LENGTH_LONG).show()
        }

        binding.cancelButton.setOnClickListener {
            alarmManager.cancel(pendingIntent)
            Toast.makeText(requireContext(), "Alarm Cancelled", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let { requireContext() } // Safety check for context
    }
}