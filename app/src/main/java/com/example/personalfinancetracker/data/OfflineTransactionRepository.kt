package com.example.personalfinancetracker.data

import kotlinx.coroutines.flow.Flow

class OfflineTransactionRepository(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactionsStream(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    override fun getTransactionsByTypeStream(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    override fun getTransactionStream(id: Int): Flow<Transaction?> =
        transactionDao.getTransaction(id)

    override suspend fun insert(transaction: Transaction) = transactionDao.insert(transaction)
    override suspend fun update(transaction: Transaction) = transactionDao.update(transaction)
    override suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)
}