package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForHibernate
import com.example.SpringBatchTutorial.repository.CustomerRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import java.util.*


@Configuration
@EnableBatchProcessing
class RepositoryBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun repositoryJob(): Job {
        return this.jobBuilderFactory.get("RepositoryJob")
            .start(copyRepositoryStep())
            .build()
    }

    @Bean
    fun copyRepositoryStep(): Step {
        return this.stepBuilderFactory.get("copyRepositoryStep")
            .chunk<CustomerForHibernate, CustomerForHibernate>(1)
            .reader(repositoryItemReader(null, null))
            .writer(repositoryItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun repositoryItemReader(
        repository: CustomerRepository?,
        @Value("#{jobParameters['city']}") city: String?
    ): RepositoryItemReader<CustomerForHibernate> {
        return RepositoryItemReaderBuilder<CustomerForHibernate>()
            .name("repositoryItemReader")
            .arguments(Collections.singletonList(city))
            .methodName(CustomerRepository::findByCity.name)
            .repository(repository!!)
            .pageSize(1)
            .sorts(Collections.singletonMap(CustomerForHibernate::lastName.name, Sort.Direction.ASC))
            .build()
    }

    @Bean
    fun repositoryItemWriter(): ItemWriter<CustomerForHibernate> {
        return ItemWriter { items: List<CustomerForHibernate> ->
            items.forEach { println(it) }
        }
    }

}