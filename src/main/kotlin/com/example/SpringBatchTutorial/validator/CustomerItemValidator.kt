package com.example.SpringBatchTutorial

import org.springframework.batch.item.validator.ValidationException
import org.springframework.batch.item.validator.Validator
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class CustomerItemValidator(
    private val jdbcTemplate: NamedParameterJdbcTemplate
): Validator<CustomerUpdate> {

    val FIND_CUSTOMER: String = "SELECT COUNT(*) FROM CUSTOMER WHERE customer_id = :id"

    override fun validate(customer: CustomerUpdate) {
        val parameterMap = mutableMapOf<String, Long>()
        parameterMap["id"] = customer.customerId
        val count = jdbcTemplate.queryForObject(FIND_CUSTOMER, parameterMap, Long::class.java)
        if (count == 0L) {
            throw ValidationException(
                String.format("Customer id %s was not able to be found", customer.customerId)
            )
        }
    }

}

class CustomerUpdate(
    val customerId: Long
)