package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import com.example.SpringBatchTutorial.reader.CustomerItemReader
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableBatchProcessing
class CustomReaderBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun customReaderJob(): Job {
        return this.jobBuilderFactory.get("CustomReaderJob")
            .start(copyCustomReaderStep())
            .build()
    }

    @Bean
    fun copyCustomReaderStep(): Step {
        return this.stepBuilderFactory.get("copyCustomReaderStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(100)
            .reader(customReaderItemReader())
            .writer(customReaderItemWriter())
            .build()
    }

    @Bean
    fun customReaderItemReader(): CustomerItemReader {
        val customReaderItemReader = CustomerItemReader()
        customReaderItemReader.setName("customReaderItemReader")
        return customReaderItemReader
    }

    @Bean
    fun customReaderItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}