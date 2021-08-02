package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForJDBC
import com.example.SpringBatchTutorial.jdbc.CustomerRowMapper
import com.example.SpringBatchTutorial.skippolicy.FileVerificationSkipper
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.ParseException
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class JdbcCursorSkipBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun jdbcCursorSkipJob(): Job {
        return this.jobBuilderFactory.get("JdbcCursorSkipJob")
            .start(copyJdbcCursorSkipStep())
            .build()
    }

    @Bean
    fun copyJdbcCursorSkipStep(): Step {
        return this.stepBuilderFactory.get("copyJdbcCursorSkipStep")
            .chunk<CustomerForJDBC, CustomerForJDBC>(1)
            .reader(customerJdbcCursorSkipItemReader(dataSource))
            .writer(jdbcCursorSkipItemWriter())
            .faultTolerant()
            .skipPolicy(FileVerificationSkipper())
            .build()
    }

    @Bean
    fun customerJdbcCursorSkipItemReader(dataSource: DataSource): JdbcCursorItemReader<CustomerForJDBC> {
        return JdbcCursorItemReaderBuilder<CustomerForJDBC>()
            .name("customerJdbcCursorSkipItemReader")
            .dataSource(dataSource)
            .sql("select * from customer_for_db where city = ?")
            .rowMapper(CustomerRowMapper())
            .preparedStatementSetter(citySetter(null))
            .build()
    }


    @Bean
    @StepScope
    fun citySetter(@Value("#{jobParameters['city']}") city: String?): ArgumentPreparedStatementSetter {
        return ArgumentPreparedStatementSetter(arrayOf(city))
    }

    @Bean
    fun jdbcCursorSkipItemWriter(): ItemWriter<CustomerForJDBC> {
        return ItemWriter { items: List<CustomerForJDBC> ->
            items.forEach { println(it) }
        }
    }

}