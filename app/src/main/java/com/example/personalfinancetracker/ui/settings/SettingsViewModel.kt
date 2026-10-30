package com.example.personalfinancetracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.TransactionFilter
import com.example.personalfinancetracker.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val selectedFilter: StateFlow<TransactionFilter> =
        userPreferencesRepository.transactionFilter.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TransactionFilter.ALL
        )

    fun selectFilter(filter: TransactionFilter) {
        viewModelScope.launch { userPreferencesRepository.saveTransactionFilter(filter) }
    }
}