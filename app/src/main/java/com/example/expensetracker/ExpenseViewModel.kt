package com.example.expensetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ExpenseDatabase.getInstance(application).expenseDao()

    val expenses = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val total = dao.getTotal().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    fun addExpense(title: String, amount: Double) {
        viewModelScope.launch {
            dao.insert(Expense(title = title, amount = amount))
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            dao.delete(expense)
        }
    }
}
