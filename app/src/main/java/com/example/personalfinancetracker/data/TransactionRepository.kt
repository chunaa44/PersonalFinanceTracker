package com.example.personalfinancetracker.data

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactionsStream(): Flow<List<Transaction>>
    fun getTransactionsByTypeStream(type: TransactionType): Flow<List<Transaction>>
    fun getTransactionStream(id: Int): Flow<Transaction?>
    suspend fun insert(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
}