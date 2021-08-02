package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import com.example.SpringBatchTutorial.repository.CustomerRepository
import com.example.SpringBatchTutorial.service.CustomerService
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.adapter.ItemReaderAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableBatchProcessing
class ServiceAdapterBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var customerService: CustomerService

    @Bean
    fun serviceAdapterJob(): Job {
        return this.jobBuilderFactory.get("ServiceAdapterBatch")
            .start(copyServiceAdapterStep())
            .build()
    }

    @Bean
    fun copyServiceAdapterStep(): Step {
        return this.stepBuilderFactory.get("copyServiceAdapterStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(1)
            .reader(serviceAdapterItemReader(customerService))
            .writer(serviceAdapterItemWriter())
            .build()
    }

    @Bean
    fun serviceAdapterItemReader(customerService: CustomerService): ItemReaderAdapter<CustomerForHibernate> {
        val adapter =  ItemReaderAdapter<CustomerForHibernate>()
        adapter.setTargetObject(customerService)
        adapter.setTargetMethod(CustomerService::getCustomer.name)
        return adapter
    }

    @Bean
    fun serviceAdapterItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}