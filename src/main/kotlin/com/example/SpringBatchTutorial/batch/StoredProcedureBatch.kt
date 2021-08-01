package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForJDBC
import com.example.SpringBatchTutorial.jdbc.CustomerRowMapper
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.StoredProcedureItemReader
import org.springframework.batch.item.database.builder.StoredProcedureItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter
import org.springframework.jdbc.core.SqlParameter
import java.sql.Types
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class StoredProcedureBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun storedProcedureJob(): Job {
        return this.jobBuilderFactory.get("StoredProcedureJob")
            .start(copyStoredProcedureStep())
            .build()
    }

    @Bean
    fun copyStoredProcedureStep(): Step {
        return this.stepBuilderFactory.get("copyStoredProcedureStep")
            .chunk<CustomerForJDBC, CustomerForJDBC>(1)
            .reader(customerStoredProcedureItemReader(dataSource, null))
            .writer(storedProcedureItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerStoredProcedureItemReader(
        dataSource: DataSource,
        @Value("#{jobParameters['city']}") city: String?
    ): StoredProcedureItemReader<CustomerForJDBC> {
        return StoredProcedureItemReaderBuilder<CustomerForJDBC>()
            .name("customerStoredProcedureItemReader")
            .dataSource(dataSource)
            .procedureName("customer_list")
            .parameters(
                SqlParameter("cityOption", Types.VARCHAR)
            )
            .preparedStatementSetter(
                ArgumentPreparedStatementSetter(arrayOf(
                    city
                ))
            )
            .rowMapper(CustomerRowMapper())
            .build()
    }

    @Bean
    fun storedProcedureItemWriter(): ItemWriter<CustomerForJDBC> {
        return ItemWriter { items: List<CustomerForJDBC> ->
            items.forEach { println(it) }
        }
    }

}