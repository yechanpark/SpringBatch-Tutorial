package com.example.SpringBatchTutorial.reader

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemStreamSupport
import java.lang.RuntimeException
import java.util.*

class CustomerItemReader: ItemStreamSupport(), ItemReader<CustomerForHibernate> {

    private val customers = arrayListOf<CustomerForHibernate>()
    private var curIndex = 0
    private val INDEX_KEY = "current.index.customers"

    private var ref: ExecutionContext? = null

    private val firstNames = arrayListOf("FirstName1", "FirstName2", "FirstName3", "FirstName4", "FirstName5")
    private val middleInitial = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lastNames = arrayOf("LastName1", "LastName2", "LastName3", "LastName4", "LastName5")
    private val streets = arrayOf("Street1", "Street2", "Street3", "Street4", "Street5")
    private val cities = arrayOf("City1", "City2", "City3", "City4", "City5")
    private val states = arrayOf("State1", "State2", "State3", "State4")

    private val generator = Random()

    init {
        for (i in 0 until 100) {
            customers.add(buildCustomer())
        }
    }

    private fun buildCustomer(): CustomerForHibernate {
        return CustomerForHibernate(
            id = generator.nextInt(Integer.MAX_VALUE).toLong(),
            firstName = firstNames[generator.nextInt(firstNames.size - 1)],
            middleInitial = middleInitial[generator.nextInt(middleInitial.length - 1)].toString(),
            lastName = lastNames[generator.nextInt(lastNames.size - 1)],
            address = "${generator.nextInt(9999)} ${streets[generator.nextInt(streets.size - 1)]}",
            city = cities[generator.nextInt(cities.size - 1)],
            state = states[generator.nextInt(states.size - 1)],
            zipCode = generator.nextInt(99999).toString()
        )
    }

    override fun open(executionContext: ExecutionContext) {
        ref = executionContext
        curIndex = if (executionContext.containsKey(getExecutionContextKey(INDEX_KEY))) {
            val index = executionContext.getInt(getExecutionContextKey(INDEX_KEY))
            if (index == 50) 51 else index
        } else 0

    }

    override fun update(executionContext: ExecutionContext) {
        executionContext.putInt(getExecutionContextKey(INDEX_KEY), curIndex)
    }

    override fun read(): CustomerForHibernate? {
        var customer: CustomerForHibernate? = null

        if (curIndex == 50) {
            ref?.putInt(getExecutionContextKey(INDEX_KEY), curIndex)
            throw RuntimeException("This will end your execution")
        }

        if (curIndex < customers.size) {
            customer = customers[curIndex]
            curIndex++
        }

        return customer
    }

    override fun close() {
        ref = null
    }

}