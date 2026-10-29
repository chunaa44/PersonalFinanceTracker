package com.example.personalfinancetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.personalfinancetracker.data.dao.TransactionDao
import com.example.personalfinancetracker.data.model.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}
