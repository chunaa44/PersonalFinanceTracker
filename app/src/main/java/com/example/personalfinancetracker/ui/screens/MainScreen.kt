package com.example.personalfinancetracker.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.model.Transaction
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    transactions: List<Transaction>,
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onEditClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit,
    onSettingsClick: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }

    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }

    var showMenu by remember { mutableStateOf(false) }
    var showTotals by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    val monthText = String.format("%04d-%02d", selectedYear, selectedMonth)
    val monthPrefix = String.format("%04d.%02d", selectedYear, selectedMonth)

    val monthlyTransactions = transactions
        .filter { it.date.startsWith(monthPrefix) }
        .sortedByDescending { it.date }

    val incomeTotal = monthlyTransactions
        .filter { it.type.equals("Income", ignoreCase = true) }
        .sumOf { it.amount }

    val expenseTotal = monthlyTransactions
        .filter { it.type.equals("Expense", ignoreCase = true) }
        .sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance") },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Text("⋮")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    onSettingsClick()
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddIncomeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF159957)
                    )
                ) {
                    Text("+ Add Income")
                }

                Button(
                    onClick = onAddExpenseClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFE53935)
                    )
                ) {
                    Text("+ Add Expense")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    if (selectedMonth == 1) {
                        selectedMonth = 12
                        selectedYear--
                    } else {
                        selectedMonth--
                    }
                }) {
                    Text("<")
                }

                Text(
                    text = monthText,
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(onClick = {
                    if (selectedMonth == 12) {
                        selectedMonth = 1
                        selectedYear++
                    } else {
                        selectedMonth++
                    }
                }) {
                    Text(">")
                }
            }

            // Income and expense summaries
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    title = "Income",
                    amount = incomeTotal,
                    color = androidx.compose.ui.graphics.Color(0xFFE0F5E8),
                    amountColor = androidx.compose.ui.graphics.Color(0xFF159957),
                    modifier = Modifier.weight(1f),
                    visible = showTotals,
                    onClick = { showTotals = !showTotals }
                )

                SummaryCard(
                    title = "Expense",
                    amount = expenseTotal,
                    color = androidx.compose.ui.graphics.Color(0xFFFFE5E5),
                    amountColor = androidx.compose.ui.graphics.Color(0xFFE53935),
                    modifier = Modifier.weight(1f),
                    visible = showTotals,
                    onClick = { showTotals = !showTotals }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (monthlyTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions this month")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    items(monthlyTransactions, key = { it.id }) { transaction ->
                        var showAmount by remember { mutableStateOf(false) }

                        val isIncome = transaction.type.equals(
                            "Income",
                            ignoreCase = true
                        )

                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isIncome) "↑" else "↓",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = if (isIncome) {
                                        androidx.compose.ui.graphics.Color(0xFF159957)
                                    } else {
                                        androidx.compose.ui.graphics.Color(0xFFE53935)
                                    },
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = {
                                                onEditClick(transaction)
                                            }
                                        )
                                ) {
                                    Text(
                                        transaction.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        transaction.date,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = if (showAmount) {
                                        val sign = if (isIncome) "+" else "-"
                                        "$sign%,.0f ₮".format(transaction.amount)
                                    } else {
                                        "**** ₮"
                                    },
                                    color = if (isIncome) {
                                        androidx.compose.ui.graphics.Color(0xFF159957)
                                    } else {
                                        androidx.compose.ui.graphics.Color(0xFFE53935)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.combinedClickable(
                                        onClick = { showAmount = !showAmount },
                                        onLongClick = {
                                            onEditClick(transaction)
                                        }
                                    )
                                )

                                TextButton(onClick = {
                                    transactionToDelete = transaction
                                }) {
                                    Text("⋮")
                                }
                            }

                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete transaction?") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick(transaction)
                    transactionToDelete = null
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    transactionToDelete = null
                }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: androidx.compose.ui.graphics.Color,
    amountColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    visible: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {}
                )
        ) {
            Text(title)
            Text(
                text = if (visible) {
                    "%,.0f ₮".format(amount)
                } else {
                    "**** ₮"
                },
                color = amountColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}