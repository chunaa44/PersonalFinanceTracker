package com.example.personalfinancetracker.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionRepository
import com.example.personalfinancetracker.data.TransactionType
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EntryUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val name: String = "",
    val amount: String = "",
    val date: String = SimpleDateFormat("yyyy.MM.dd", Locale.US).format(Date())
) {
    val isValid: Boolean
        get() = name.isNotBlank() && amount.isNotBlank() && date.isNotBlank()
}

class EntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    companion object {
        const val TRANSACTION_ID_ARG = "transactionId"
    }

    // 0 means "add new" (matches Room's autoGenerate default).
    private val transactionId: Int = savedStateHandle[TRANSACTION_ID_ARG] ?: 0
    val isEditing: Boolean = transactionId != 0

    var uiState by mutableStateOf(EntryUiState())
        private set

    init {
        if (isEditing) {
            viewModelScope.launch {
                val t = transactionRepository.getTransactionStream(transactionId)
                    .filterNotNull().first()
                uiState = EntryUiState(t.type, t.name, t.amount.toLong().toString(), t.date)
            }
        }
    }

    fun onTypeChange(type: TransactionType) { uiState = uiState.copy(type = type) }
    fun onNameChange(name: String) { uiState = uiState.copy(name = name) }
    fun onAmountChange(amount: String) { uiState = uiState.copy(amount = amount.filter { it.isDigit() }) }
    fun onDateChange(date: String) { uiState = uiState.copy(date = date) }

    suspend fun save() {
        if (!uiState.isValid) return
        val transaction = Transaction(
            id = transactionId,
            type = uiState.type,
            name = uiState.name.trim(),
            amount = uiState.amount.toDouble(),
            date = uiState.date
        )
        if (isEditing) transactionRepository.update(transaction)
        else transactionRepository.insert(transaction)
    }
}