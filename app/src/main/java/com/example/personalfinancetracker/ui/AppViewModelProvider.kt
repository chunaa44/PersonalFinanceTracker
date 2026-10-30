package com.example.personalfinancetracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.personalfinancetracker.PersonalFinanceApplication
import com.example.personalfinancetracker.ui.entry.EntryViewModel
import com.example.personalfinancetracker.ui.home.HomeViewModel
import com.example.personalfinancetracker.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                financeApplication().container.transactionRepository,
                financeApplication().container.userPreferencesRepository
            )
        }
        initializer {
            EntryViewModel(
                this.createSavedStateHandle(),
                financeApplication().container.transactionRepository
            )
        }
        initializer {
            SettingsViewModel(financeApplication().container.userPreferencesRepository)
        }
    }
}

fun CreationExtras.financeApplication(): PersonalFinanceApplication =
    this[AndroidViewModelFactory.APPLICATION_KEY] as PersonalFinanceApplication