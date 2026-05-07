package com.ksp.petcaretracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PetCareApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_VACCINATION,
                    "Vaccination Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for upcoming vaccinations"
                },
                NotificationChannel(
                    CHANNEL_VET_APPOINTMENT,
                    "Vet Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for vet appointments"
                },
                NotificationChannel(
                    CHANNEL_DIET,
                    "Diet Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders for pet feeding schedule"
                }
            )
            val manager = getSystemService(NotificationManager::class.java)
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_VACCINATION = "vaccination_reminders"
        const val CHANNEL_VET_APPOINTMENT = "vet_appointment_reminders"
        const val CHANNEL_DIET = "diet_reminders"
    }
}
