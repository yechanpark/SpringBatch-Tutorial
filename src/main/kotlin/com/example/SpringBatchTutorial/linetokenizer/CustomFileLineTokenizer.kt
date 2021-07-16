package com.example.SpringBatchTutorial.linetokenizer

import com.example.SpringBatchTutorial.domain.Customer
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory
import org.springframework.batch.item.file.transform.FieldSet
import org.springframework.batch.item.file.transform.LineTokenizer

class CustomerFileLineTokenizer: LineTokenizer {
    private val delimiter = ","
    private val names = arrayOf(
        Customer::firstName.name,
        Customer::middleInitial.name,
        Customer::lastName.name,
        "address",
        Customer::city.name,
        Customer::state.name,
        Customer::zipCode.name
    )
    private val fieldSetFactory = DefaultFieldSetFactory()

    override fun tokenize(record: String?): FieldSet {
        println("${this::class.simpleName} ${Thread.currentThread().stackTrace[1].methodName}()")
        val fields = record?.split(delimiter)
        val parsedFields = arrayListOf<String>()

        for ( i in 0 until fields!!.size) {
            if ( i == 4 ) {
                parsedFields[i - 1] = "${parsedFields[i - 1]} ${fields[i]}"
            } else {

                parsedFields.add(fields[i])
            }
        }

        return fieldSetFactory.create(parsedFields.toArray(arrayOf()), names)

    }
}