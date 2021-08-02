package com.example.SpringBatchTutorial.repository

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<CustomerForHibernate, Long> {
    fun findByCity(city: String, pageRequest: Pageable): Page<CustomerForHibernate>
}