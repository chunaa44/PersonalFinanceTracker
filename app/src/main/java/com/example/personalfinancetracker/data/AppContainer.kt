package com.example.personalfinancetracker.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

interface AppContainer {
    val transactionRepository: TransactionRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val transactionRepository: TransactionRepository by lazy {
        OfflineTransactionRepository(
            TransactionDatabase.getDatabase(context).transactionDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}