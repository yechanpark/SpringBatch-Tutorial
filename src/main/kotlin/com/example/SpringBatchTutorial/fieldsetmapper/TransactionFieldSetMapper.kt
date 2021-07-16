package com.example.SpringBatchTutorial.fieldsetmapper

import com.example.SpringBatchTutorial.domain.Transaction
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.FieldSet

class TransactionFieldSetMapper: FieldSetMapper<Any> {
    override fun mapFieldSet(fieldSet: FieldSet): Transaction {
        return Transaction(
            accountNumber = fieldSet.readString(Transaction::accountNumber.name),
            amount = fieldSet.readDouble(Transaction::amount.name),
            transactionDate = fieldSet.readDate(Transaction::transactionDate.name, "yyyy-MM-dd HH:mm:ss")
        )
    }
}