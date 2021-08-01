package com.example.SpringBatchTutorial.jdbc

import com.example.SpringBatchTutorial.domain.CustomerForJDBC
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class CustomerRowMapper: RowMapper<CustomerForJDBC> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = CustomerForJDBC(
        id = rs.getLong(CustomerForJDBC::id.name),
        address = rs.getString(CustomerForJDBC::address.name),
        city = rs.getString(CustomerForJDBC::city.name),
        firstName = rs.getString(CustomerForJDBC::firstName.name),
        lastName = rs.getString(CustomerForJDBC::lastName.name),
        middleInitial = rs.getString(CustomerForJDBC::middleInitial.name),
        state = rs.getString(CustomerForJDBC::state.name),
        zipCode = rs.getString(CustomerForJDBC::zipCode.name)
    )
}