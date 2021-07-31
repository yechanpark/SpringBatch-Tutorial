package com.example.SpringBatchTutorial.domain

import javax.xml.bind.annotation.*

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class Customer(
    var firstName: String = "",
    var middleInitial: String = "",
    var lastName: String = "",
    var address: String = "", // addressNumber + street
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    @XmlElementWrapper(name = "transactions")
    @XmlElement(name = "transaction")
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