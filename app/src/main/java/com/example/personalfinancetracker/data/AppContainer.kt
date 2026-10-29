package com.example.personalfinancetracker.data

import android.content.Context
import androidx.room.Room
import com.example.personalfinancetracker.data.database.TransactionDatabase
import com.example.personalfinancetracker.data.repository.TransactionRepository

interface AppContainer {
    val transactionRepository: TransactionRepository
}

class AppDataContainer(
    private val context: Context
) : AppContainer {

    override val transactionRepository: TransactionRepository by lazy {
        val database = Room.databaseBuilder(
            context,
            TransactionDatabase::class.java,
            "finance_database"
        ).build()

        TransactionRepository(database.transactionDao())
    }
}
