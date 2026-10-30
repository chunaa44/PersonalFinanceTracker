package com.example.personalfinancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.personalfinancetracker.ui.navigation.FinanceNavHost
import com.example.personalfinancetracker.ui.theme.PersonalFinanceTrackerTheme

class MainActivity : ComponentActivity() {

    private val reminderScheduler
        get() = (application as PersonalFinanceApplication).container.reminderScheduler

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            PersonalFinanceTrackerTheme {
                FinanceNavHost()
            }
        }
    }

    // App is visible again, so any pending reminder is no longer needed.
    override fun onStart() {
        super.onStart()
        reminderScheduler.cancel()
    }

    // App left the screen, so start the 24h countdown.
    // Rotation also triggers onStop, so it is skipped to avoid pointless reschedules.
    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) reminderScheduler.schedule()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}