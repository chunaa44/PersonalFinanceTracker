package com.example.personalfinancetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    existingTransaction: Transaction? = null,
    initialType: String = "Income",
    onSave: (Transaction) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember {
        mutableStateOf(existingTransaction?.type ?: initialType)
    }

    var name by remember {
        mutableStateOf(existingTransaction?.name ?: "")
    }

    var amount by remember {
        mutableStateOf(existingTransaction?.amount?.toString() ?: "")
    }

    var date by remember {
        mutableStateOf(
            existingTransaction?.date
                ?: SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (existingTransaction == null) "Add Transaction" else "Edit Transaction")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Transaction type
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = type == "Income",
                    onClick = { type = "Income" },
                    label = { Text("Income") }
                )

                FilterChip(
                    selected = type == "Expense",
                    onClick = { type = "Expense" },
                    label = { Text("Expense") }
                )
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₮)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (yyyy.MM.dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    if (
                        name.isNotBlank() &&
                        parsedAmount != null &&
                        parsedAmount > 0 &&
                        date.matches(Regex("\\d{4}\\.\\d{2}\\.\\d{2}"))
                    ) {
                        onSave(
                            Transaction(
                                id = existingTransaction?.id ?: 0,
                                type = type,
                                name = name,
                                amount = parsedAmount,
                                date = date
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}