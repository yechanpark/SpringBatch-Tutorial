package com.example.SpringBatchTutorial.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
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

data class CustomerForJDBC(
    var id: Long,
    var firstName: String,
    var middleInitial: String,
    var lastName: String,
    var address: String,
    var city: String,
    var state: String,
    var zipCode: String
)

@Entity
@Table(name = "customer_for_db")
data class CustomerForHibernate(
    @Id
    var id: Long,

    @Column(name = "firstName")
    var firstName: String,

    @Column(name = "middleInitial")
    var middleInitial: String,

    @Column(name = "lastName")
    var lastName: String,
    var address: String,
    var city: String,
    var state: String,
    var zipCode: String
)