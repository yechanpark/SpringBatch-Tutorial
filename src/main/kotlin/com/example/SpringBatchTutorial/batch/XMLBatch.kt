package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.Customer
import com.example.SpringBatchTutorial.domain.Transaction
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.xml.StaxEventItemReader
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.oxm.jaxb.Jaxb2Marshaller

@Configuration
@EnableBatchProcessing
class XMLBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun xmlJob(): Job {
        return this.jobBuilderFactory.get("XMLBatchJob")
            .start(copyXMLFileStep())
            .build()
    }

    @Bean
    fun copyXMLFileStep(): Step {
        return this.stepBuilderFactory.get("copyXMLFileStep")
            .chunk<Customer, Customer>(10)
            .reader(customerXMLFileItemReader(null))
            .writer(xmlItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customerXMLFileItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): StaxEventItemReader<Customer> {
        return StaxEventItemReaderBuilder<Customer>()
            .name("customerXMLFileItemReader")
            .resource(inputFile!!)
            .addFragmentRootElements("customer")
            .unmarshaller(customerMarshaller())
            .build()
    }

    @Bean
    fun customerMarshaller(): Jaxb2Marshaller {
        val jaxb2Marshaller = Jaxb2Marshaller()
        jaxb2Marshaller.setClassesToBeBound(Customer::class.java, Transaction::class.java)
        return jaxb2Marshaller
    }

    @Bean
    fun xmlItemWriter(): ItemWriter<Any> {
        return ItemWriter { items: List<Any> ->
            items.forEach { println(it) }
        }
    }

}