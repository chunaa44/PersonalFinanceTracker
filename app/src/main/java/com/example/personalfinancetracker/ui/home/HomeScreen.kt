package com.example.personalfinancetracker.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinancetracker.data.Transaction
import com.example.personalfinancetracker.data.TransactionType
import com.example.personalfinancetracker.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val current = transactions.getOrNull(viewModel.currentIndex)
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Tracker") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { menuExpanded = false; onOpenSettings() }
                        )
                    }
                }
            )
        }
    ) { padding ->
        val card: @Composable (Modifier) -> Unit = { modifier ->
            TransactionCard(
                transaction = current,
                position = "${viewModel.currentIndex + 1} / ${transactions.size}",
                isAmountRevealed = viewModel.isAmountRevealed,
                onToggleAmount = viewModel::toggleAmount,
                onLongPress = { current?.let { onEdit(it.id) } },
                modifier = modifier
            )
        }
        val buttons: @Composable (Modifier) -> Unit = { modifier ->
            ActionButtons(
                hasRecord = current != null,
                hasPrevious = viewModel.currentIndex > 0,
                hasNext = viewModel.currentIndex < transactions.lastIndex,
                onPrevious = viewModel::showPrevious,
                onNext = viewModel::showNext,
                onEdit = { current?.let { onEdit(it.id) } },
                onDelete = { showDeleteDialog = true },
                onAdd = onAdd,
                modifier = modifier
            )
        }

        if (isLandscape) {
            Row(
                modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                card(Modifier.weight(1f).fillMaxHeight())
                buttons(Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                card(Modifier.weight(1f).fillMaxWidth())
                buttons(Modifier.fillMaxWidth())
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete record?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; viewModel.deleteCurrent() }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionCard(
    transaction: Transaction?,
    position: String,
    isAmountRevealed: Boolean,
    onToggleAmount: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.combinedClickable(onClick = {}, onLongClick = onLongPress)
    ) {
        if (transaction == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No records yet", style = MaterialTheme.typography.titleMedium)
                Text("Tap Add to create one.")
            }
        } else {
            val isIncome = transaction.type == TransactionType.INCOME
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(position, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = if (isIncome) "Income" else "Expense",
                    color = if (isIncome) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Text(transaction.name, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = if (isAmountRevealed) "%,.0f ₮".format(transaction.amount) else "**** ₮",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.combinedClickable(
                        onClick = onToggleAmount,
                        onLongClick = onLongPress
                    )
                )
                Text(transaction.date, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ActionButtons(
    hasRecord: Boolean,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onPrevious, Modifier.weight(1f), enabled = hasPrevious) { Text("Previous") }
            OutlinedButton(onNext, Modifier.weight(1f), enabled = hasNext) { Text("Next") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onEdit, Modifier.weight(1f), enabled = hasRecord) { Text("Edit") }
            OutlinedButton(onDelete, Modifier.weight(1f), enabled = hasRecord) { Text("Delete") }
        }
        Button(onAdd, Modifier.fillMaxWidth()) { Text("Add") }
    }
}