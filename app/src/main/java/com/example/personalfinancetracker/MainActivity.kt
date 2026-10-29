package com.example.personalfinancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinancetracker.ui.screens.MainScreen
import com.example.personalfinancetracker.ui.viewmodel.TransactionViewModel
import com.example.personalfinancetracker.ui.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as PersonalFinanceApplication

        val factory = TransactionViewModelFactory(
            application.container.transactionRepository
        )

        setContent {
            val transactionViewModel: TransactionViewModel = viewModel(
                factory = factory
            )

            val transactions by transactionViewModel.transactions.collectAsState()

            MainScreen(
                transactions = transactions,

                onAddIncomeClick = {
                    // Add screen will be connected later.
                },

                onAddExpenseClick = {
                    // Add screen will be connected later.
                },

                onEditClick = {
                    // Edit screen will be connected later.
                },

                onDeleteClick = { transaction ->
                    transactionViewModel.delete(transaction)
                },

                onSettingsClick = {
                    // Settings screen will be connected later.
                }
            )
        }
    }
}