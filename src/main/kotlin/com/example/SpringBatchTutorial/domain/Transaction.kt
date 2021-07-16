package com.example.SpringBatchTutorial.domain

import java.util.*

data class Transaction(
    val accountNumber: String,
    val transactionDate: Date,
    val amount: Double
)