package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import org.hibernate.SessionFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.HibernatePagingItemReader
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.persistence.EntityManagerFactory


@Configuration
@EnableBatchProcessing
class HibernatePagingBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun hibernatePagingJob(): Job {
        return this.jobBuilderFactory.get("HibernatePagingJob")
            .start(copyHibernatePagingStep())
            .build()
    }

    @Bean
    fun copyHibernatePagingStep(): Step {
        return this.stepBuilderFactory.get("copyHibernatePagingStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(1)
            .reader(customerHibernatePagingItemReader(null, null))
            .writer(hibernatePagingItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerHibernatePagingItemReader(
        entityManagerFactory: EntityManagerFactory?,
        @Value("#{jobParameters['city']}") city: String?
    ): HibernatePagingItemReader<CustomerForHibernate> {
        return HibernatePagingItemReaderBuilder<CustomerForHibernate>()
            .name("customerHibernatePagingItemReader")
            .sessionFactory(entityManagerFactory!!.unwrap(SessionFactory::class.java))
            .queryString("from CustomerForHibernate where city = :city")
            .parameterValues(Collections.singletonMap("city", city) as Map<String, Any>)
            .pageSize(1)
            .build()
    }

    @Bean
    fun hibernatePagingItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}