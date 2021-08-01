package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import com.example.SpringBatchTutorial.jpa.queryprovider.CustomerByCityQueryProvider
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.persistence.EntityManagerFactory


@Configuration
@EnableBatchProcessing
class JpaPagingBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun jpaPagingJob(): Job {
        return this.jobBuilderFactory.get("JpaPagingJob")
            .start(copyJpaPagingStep())
            .build()
    }

    @Bean
    fun copyJpaPagingStep(): Step {
        return this.stepBuilderFactory.get("copyJpaPagingStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(1)
            .reader(customerJpaPagingItemReader(null, null))
            .writer(jpaPagingItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerJpaPagingItemReader(
        entityManagerFactory: EntityManagerFactory?,
        @Value("#{jobParameters['city']}") city: String?
    ): JpaPagingItemReader<CustomerForHibernate> {
        return JpaPagingItemReaderBuilder<CustomerForHibernate>()
            .name("customerJpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory!!)
            .queryProvider(CustomerByCityQueryProvider())
            .parameterValues(Collections.singletonMap("city", city) as Map<String, Any>)
            .pageSize(1)
            .build()
    }

    @Bean
    fun jpaPagingItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}