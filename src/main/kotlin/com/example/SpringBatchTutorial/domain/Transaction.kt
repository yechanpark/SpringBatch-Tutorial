package com.example.SpringBatchTutorial.domain

import java.util.*

data class Transaction(
    val accountNumber: String = "",
    val transactionDate: Date? = null,
    val amount: Double = 0.0
)