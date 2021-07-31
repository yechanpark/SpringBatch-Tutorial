package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.Customer
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.json.JacksonJsonObjectReader
import org.springframework.batch.item.json.JsonItemReader
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.text.SimpleDateFormat

@Configuration
@EnableBatchProcessing
class JsonBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun jsonBatchJob(): Job {
        return this.jobBuilderFactory.get("JsonBatchJob")
            .start(copyJsonFileStep())
            .build()
    }

    @Bean
    fun copyJsonFileStep(): Step {
        return this.stepBuilderFactory.get("copyJsonFileStep")
            .chunk<Customer, Customer>(10)
            .reader(customerJsonFileItemReader(null))
            .writer(jsonItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerJsonFileItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): JsonItemReader<Customer> {

        val objectMapper = ObjectMapper()
        objectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        val jsonObjectReader = JacksonJsonObjectReader(Customer::class.java)
        jsonObjectReader.setMapper(objectMapper)

        return JsonItemReaderBuilder<Customer>()
            .name("customerJsonFileItemReader")
            .jsonObjectReader(jsonObjectReader)
            .resource(inputFile!!)
            .build()
    }

    @Bean
    fun jsonItemWriter(): ItemWriter<Any> {
        return ItemWriter { items: List<Any> ->
            items.forEach { println(it) }
        }
    }

}