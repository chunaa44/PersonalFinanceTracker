package com.example.personalfinancetracker

import android.app.Application
import com.example.personalfinancetracker.data.AppContainer
import com.example.personalfinancetracker.data.AppDataContainer
import com.example.personalfinancetracker.notification.ReminderWorker

class PersonalFinanceApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
        ReminderWorker.createChannel(this)
    }
}