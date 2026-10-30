package com.example.personalfinancetracker.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val TRANSACTION_FILTER = stringPreferencesKey("transaction_filter")
    }

    val transactionFilter: Flow<TransactionFilter> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            TransactionFilter.valueOf(prefs[TRANSACTION_FILTER] ?: TransactionFilter.ALL.name)
        }

    suspend fun saveTransactionFilter(filter: TransactionFilter) {
        dataStore.edit { it[TRANSACTION_FILTER] = filter.name }
    }
}