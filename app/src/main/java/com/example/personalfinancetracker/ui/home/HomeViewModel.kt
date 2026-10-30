package com.example.personalfinancetracker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionFilter
import com.example.personalfinancetracker.data.TransactionRepository
import com.example.personalfinancetracker.data.TransactionType
import com.example.personalfinancetracker.data.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var currentIndex by mutableStateOf(0)
        private set
    var isAmountRevealed by mutableStateOf(false)
        private set

    // Re-queries Room whenever the saved filter changes.
    val transactions: StateFlow<List<Transaction>> = userPreferencesRepository.transactionFilter
        .flatMapLatest { filter ->
            when (filter) {
                TransactionFilter.ALL -> transactionRepository.getAllTransactionsStream()
                TransactionFilter.INCOME ->
                    transactionRepository.getTransactionsByTypeStream(TransactionType.INCOME)
                TransactionFilter.EXPENSE ->
                    transactionRepository.getTransactionsByTypeStream(TransactionType.EXPENSE)
            }
        }
        // Keeps the index valid after a delete or a filter change.
        .onEach { list -> currentIndex = currentIndex.coerceIn(0, maxOf(list.lastIndex, 0)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    fun showNext() {
        if (currentIndex < transactions.value.lastIndex) {
            currentIndex++
            isAmountRevealed = false
        }
    }

    fun showPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            isAmountRevealed = false
        }
    }

    fun toggleAmount() {
        isAmountRevealed = !isAmountRevealed
    }

    fun deleteCurrent() {
        val transaction = transactions.value.getOrNull(currentIndex) ?: return
        isAmountRevealed = false
        viewModelScope.launch { transactionRepository.delete(transaction) }
    }
}