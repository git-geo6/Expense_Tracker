package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.Expense
import com.example.expensetracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                ExpenseScreen(viewModel)
            }
        }
    }
}

private val categoryColors = listOf(Purple, Coral, Mint, Amber, PurpleDark)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val total by viewModel.total.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Purple,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TotalHeader(total = total)

            if (expenses.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        val color = categoryColors[(expense.id % categoryColors.size).toInt()]
                        ExpenseRow(
                            expense = expense,
                            accentColor = color,
                            onDelete = { viewModel.deleteExpense(expense) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, amount ->
                viewModel.addExpense(title, amount)
                showDialog = false
            }
        )
    }
}

@Composable
fun TotalHeader(total: Double) {
    val gradient = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(Purple, PurpleDark)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .padding(20.dp)
    ) {
        Column {
            Text(
                "Total spent",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "₹${"%.2f".format(total)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💸", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text("No expenses yet", style = MaterialTheme.typography.titleMedium)
        Text("Tap + to add your first one", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ExpenseRow(expense: Expense, accentColor: Color, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                expense.title.take(1).uppercase(),
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(expense.title, style = MaterialTheme.typography.titleMedium)
            Text(dateFormat.format(Date(expense.timestamp)), style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            "-₹${"%.2f".format(expense.amount)}",
            color = Coral,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(Modifier.width(8.dp))

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Add expense", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What was it for?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = Coral, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amt = amount.toDoubleOrNull()
                if (title.isBlank()) {
                    error = "Enter a title"
                } else if (amt == null || amt <= 0) {
                    error = "Enter a valid amount"
                } else {
                    onConfirm(title.trim(), amt)
                }
            }) {
                Text("Add", color = Purple, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
