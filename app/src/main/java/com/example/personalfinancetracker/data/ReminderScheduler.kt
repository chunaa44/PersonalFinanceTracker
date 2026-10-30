package com.example.personalfinancetracker.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.personalfinancetracker.notification.ReminderWorker
import java.util.concurrent.TimeUnit

class ReminderScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    // The initial delay makes the first reminder fire 24h after leaving, then it repeats every 24h.
    // Re-enqueuing with the same unique name restarts the countdown instead of duplicating work.
    fun schedule() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(INTERVAL_HOURS, TimeUnit.HOURS)
            .setInitialDelay(3, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(WORK_NAME)
    }

    private companion object {
        const val WORK_NAME = "daily_reminder"
        const val INTERVAL_HOURS = 24L
    }
}