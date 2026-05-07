package com.ksp.petcaretracker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    fun formatDate(millis: Long): String = displayFormat.format(Date(millis))

    fun formatShortDate(millis: Long): String = shortFormat.format(Date(millis))

    fun calculateAge(dateOfBirth: Long): String {
        val now = Calendar.getInstance()
        val dob = Calendar.getInstance().apply { timeInMillis = dateOfBirth }

        var years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        var months = now.get(Calendar.MONTH) - dob.get(Calendar.MONTH)

        if (months < 0) {
            years--
            months += 12
        }

        return when {
            years > 0 && months > 0 -> "$years yr $months mo"
            years > 0 -> "$years yr"
            months > 0 -> "$months mo"
            else -> "< 1 mo"
        }
    }

    fun daysUntil(millis: Long): Long {
        val now = System.currentTimeMillis()
        val diff = millis - now
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun isOverdue(millis: Long): Boolean = millis < System.currentTimeMillis()

    fun todayStartMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
