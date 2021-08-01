package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import org.hibernate.SessionFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.HibernateCursorItemReader
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.persistence.EntityManagerFactory


@Configuration
@EnableBatchProcessing
class HibernateCursorBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun hibernateCursorJob(): Job {
        return this.jobBuilderFactory.get("HibernateCursorJob")
            .start(copyHibernateCursorStep())
            .build()
    }

    @Bean
    fun copyHibernateCursorStep(): Step {
        return this.stepBuilderFactory.get("copyHibernateCursorStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(1)
            .reader(customerHibernateCursorItemReader(null, null))
            .writer(hibernateCursorItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerHibernateCursorItemReader(
        entityManagerFactory: EntityManagerFactory?,
        @Value("#{jobParameters['city']}") city: String?
    ): HibernateCursorItemReader<CustomerForHibernate> {
        return HibernateCursorItemReaderBuilder<CustomerForHibernate>()
            .name("customerHibernateCursorItemReader")
            .sessionFactory(entityManagerFactory!!.unwrap(SessionFactory::class.java))
            .queryString("from CustomerForHibernate where city = :city")
            .parameterValues(Collections.singletonMap("city", city) as Map<String, Any>)
            .build()
    }

    @Bean
    fun hibernateCursorItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}