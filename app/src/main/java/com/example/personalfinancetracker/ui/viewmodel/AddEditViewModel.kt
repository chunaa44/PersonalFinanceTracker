
package com.example.personalfinancetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.model.Transaction
import com.example.personalfinancetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddEditUiState(
    val type: String = "Income",
    val name: String = "",
    val amount: String = "",
    val date: String = SimpleDateFormat(
        "yyyy.MM.dd",
        Locale.getDefault()
    ).format(Date()),
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddEditViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var editingTransactionId: Int? = null

    fun loadTransaction(transaction: Transaction?) {
        editingTransactionId = transaction?.id

        _uiState.value = if (transaction == null) {
            AddEditUiState()
        } else {
            AddEditUiState(
                type = transaction.type,
                name = transaction.name,
                amount = transaction.amount.toString(),
                date = transaction.date
            )
        }
    }

    fun updateType(type: String) {
        _uiState.update { it.copy(type = type) }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun updateDate(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val parsedAmount = state.amount.toDoubleOrNull()

        val validDate = try {
            val formatter = SimpleDateFormat(
                "yyyy.MM.dd",
                Locale.getDefault()
            )
            formatter.isLenient = false
            formatter.parse(state.date) != null
        } catch (e: Exception) {
            false
        }

        if (
            state.name.isBlank() ||
            parsedAmount == null ||
            parsedAmount <= 0 ||
            !validDate
        ) {
            _uiState.update {
                it.copy(errorMessage = "Please enter valid information.")
            }
            return
        }

        val transaction = Transaction(
            id = editingTransactionId ?: 0,
            type = state.type,
            name = state.name,
            amount = parsedAmount,
            date = state.date
        )

        viewModelScope.launch {
            try {
                if (editingTransactionId == null) {
                    repository.insert(transaction)
                } else {
                    repository.update(transaction)
                }

                _uiState.update {
                    it.copy(isSaved = true, errorMessage = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to save transaction.")
                }
            }
        }
    }
}
