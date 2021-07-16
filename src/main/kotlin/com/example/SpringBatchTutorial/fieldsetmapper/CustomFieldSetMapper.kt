package com.example.SpringBatchTutorial.fieldsetmapper

import com.example.SpringBatchTutorial.domain.Customer
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.FieldSet

class CustomerFieldSetMapper: FieldSetMapper<Customer> {
    override fun mapFieldSet(fieldSet: FieldSet): Customer {
        println("${this::class.simpleName} ${Thread.currentThread().stackTrace[1].methodName}()")
        return Customer(
            firstName = fieldSet.readString(Customer::firstName.name),
            middleInitial= fieldSet.readString(Customer::middleInitial.name),
            lastName= fieldSet.readString(Customer::lastName.name),
            address = "${fieldSet.readString("addressNumber")} ${fieldSet.readString("street")}",
            city = fieldSet.readString(Customer::city.name),
            state = fieldSet.readString(Customer::state.name),
            zipCode = fieldSet.readString(Customer::zipCode.name)
        )
    }
}