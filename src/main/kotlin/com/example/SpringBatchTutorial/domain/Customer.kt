package com.example.SpringBatchTutorial.domain

data class Customer(
    var firstName: String = "",
    var middleInitial: String = "",
    var lastName: String = "",
    var address: String = "", // addressNumber + street
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var transactions: MutableList<Transaction> = mutableListOf()
) {
    override fun toString(): String {
        val output = StringBuilder().append("$firstName $middleInitial. $lastName has ")

        if (transactions.isNotEmpty()) {
            output.append(transactions.size).append(" transactions.")
        } else {
            output.append("no transactions.")
        }

        return output.toString()
    }
}