package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.CustomerForJDBC
import com.example.SpringBatchTutorial.jdbc.CustomerRowMapper
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class JdbcPagingBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun jdbcPagingJob(): Job {
        return this.jobBuilderFactory.get("JdbcPagingBatch")
            .start(copyJdbcPagingStep())
            .build()
    }

    @Bean
    fun copyJdbcPagingStep(): Step {
        return this.stepBuilderFactory.get("copyJdbcPagingStep")
            .chunk<CustomerForJDBC, CustomerForJDBC>(1)
            .reader(customerJdbcPagingItemReader(dataSource, null, null))
            .writer(jdbcPagingItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerJdbcPagingItemReader(
        dataSource: DataSource,
        queryProvider: PagingQueryProvider?,
        @Value("#{jobParameters['city']}") city: String?
    ): JdbcPagingItemReader<CustomerForJDBC> {
        val parameterValues = hashMapOf<String, Any>()
        parameterValues["city"] = city!!

        return JdbcPagingItemReaderBuilder<CustomerForJDBC>()
            .name("customerJdbcPagingItemReader")
            .dataSource(dataSource)
            .queryProvider(queryProvider!!)
            .parameterValues(parameterValues)
            .pageSize(1)
            .rowMapper(CustomerRowMapper())
            .build()
    }


    @Bean
    fun pagingQueryProvider(dataSource: DataSource): SqlPagingQueryProviderFactoryBean {
        val factoryBean = SqlPagingQueryProviderFactoryBean()
        factoryBean.setSelectClause("select *")
        factoryBean.setFromClause("from customer_for_db")
        factoryBean.setWhereClause("where city = :city")
        factoryBean.setSortKey("lastName")
        factoryBean.setDataSource(dataSource)
        return factoryBean
    }

    @Bean
    fun jdbcPagingItemWriter(): ItemWriter<CustomerForJDBC> {
        return ItemWriter { items: List<CustomerForJDBC> ->
            items.forEach { println(it) }
        }
    }

}