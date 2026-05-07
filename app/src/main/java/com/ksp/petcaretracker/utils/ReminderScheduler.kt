package com.ksp.petcaretracker.utils

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ksp.petcaretracker.worker.ReminderWorker
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        uniqueWorkName: String,
        title: String,
        message: String,
        channelId: String,
        triggerAtMillis: Long,
        notificationId: Int
    ) {
        val delay = triggerAtMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = Data.Builder()
            .putString(ReminderWorker.KEY_TITLE, title)
            .putString(ReminderWorker.KEY_MESSAGE, message)
            .putString(ReminderWorker.KEY_CHANNEL_ID, channelId)
            .putInt(ReminderWorker.KEY_NOTIFICATION_ID, notificationId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun cancelReminder(context: Context, uniqueWorkName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}
