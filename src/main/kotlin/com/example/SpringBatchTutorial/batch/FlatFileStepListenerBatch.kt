package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.Customer
import com.example.SpringBatchTutorial.domain.Transaction
import com.example.SpringBatchTutorial.fieldsetmapper.TransactionFieldSetMapper
import com.example.SpringBatchTutorial.listener.EmptyInputStepFailer
import com.example.SpringBatchTutorial.reader.CustomerFileReader
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.LineTokenizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
@EnableBatchProcessing
class FlatFileStepListenerBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun flatFileStepListenerBatchJob(): Job {
        return this.jobBuilderFactory.get("FlatFileStepListenerBatchJob")
            .start(copyFlatFileStepListenerStep())
            .build()
    }

    @Bean
    fun emptyFileFailer(): EmptyInputStepFailer {
        return EmptyInputStepFailer()
    }

    @Bean
    fun copyFlatFileStepListenerStep(): Step {
        return this.stepBuilderFactory.get("copyFlatFileStepListenerStep")
            .chunk<Any, Customer>(10)
            .reader(multiCustomerStepListenerReader(null))
            .writer(flatFileStepListenerItemWriter())
            .listener(emptyFileFailer())
            .build()
    }

    @Bean
    @StepScope
    fun multiCustomerStepListenerReader(@Value("#{jobParameters['customerFile']}") inputFiles: Array<Resource>?): MultiResourceItemReader<Any> {
        return MultiResourceItemReaderBuilder<Any>()
            .name("multiCustomerStepListenerReader")
            .resources(*inputFiles!!)
            .delegate(customerStepListenerFileReader())
            .build()
    }

    @Bean
    @StepScope
    fun customerStepListenerItemReader(): FlatFileItemReader<*> {
        return FlatFileItemReaderBuilder<Any>()
            .name("customerStepListenerItemReader")
            .lineMapper(stepListenerLineTokenizer())
            .build()
    }

    @Bean
    fun customerStepListenerFileReader() = CustomerFileReader(customerStepListenerItemReader())

    @Bean
    fun stepListenerLineTokenizer(): PatternMatchingCompositeLineMapper<Any> {
        val customerPrefixPattern = "CUST*"
        val transactionPrefixPattern = "TRANS*"


        // LineTokenizer ??????
        val lineTokenizers = HashMap<String, LineTokenizer>(2)
        lineTokenizers[customerPrefixPattern] = stepListenerCustomerLineTokenizer()       // Customer ??? DelimitedLineTokenizer
        lineTokenizers[transactionPrefixPattern] = stepListenerTransactionLineTokenizer() // Transaction ??? DelimitedLineTokenizer


        // FieldSetMapper ??????
        val fieldSetMappers = mutableMapOf<String, FieldSetMapper<Any>>()

        val customerFieldSetMapper = BeanWrapperFieldSetMapper<Any>()
        customerFieldSetMapper.setTargetType(Customer::class.java)

        fieldSetMappers[customerPrefixPattern] = customerFieldSetMapper         // Customer ??? BeanWrapperFieldSetMapper
        fieldSetMappers[transactionPrefixPattern] = TransactionFieldSetMapper() // Transaction ??? FieldSetMapper


        // LineMapper ??????
        val lineMappers = PatternMatchingCompositeLineMapper<Any>()
        lineMappers.setTokenizers(lineTokenizers)       // ?????? LineTokenizer??? ?????? ?????? Map<String, LineTokenizer>
        lineMappers.setFieldSetMappers(fieldSetMappers) // ?????? FieldSetMapper??? ?????? ?????? Map<String, FieldSetMapper<Any>>
        return lineMappers
    }

    @Bean
    fun stepListenerTransactionLineTokenizer(): DelimitedLineTokenizer {
        val lineTokenizer = DelimitedLineTokenizer()

        // setIncludedFields()??? ?????? ?????? ?????? ?????? ???????????? ????????? ???
        // ?????? ??? FieldSet??? prefix?????? ???????????? ????????????, ??????????????? ????????? ??????
        lineTokenizer.setNames(
            "prefix",
            Transaction::accountNumber.name,
            Transaction::transactionDate.name,
            Transaction::amount.name
        )

        return lineTokenizer
    }

    @Bean
    fun stepListenerCustomerLineTokenizer(): DelimitedLineTokenizer {
        val lineTokenizer = DelimitedLineTokenizer()

        lineTokenizer.setNames(
            Customer::firstName.name,
            Customer::middleInitial.name,
            Customer::lastName.name,
            Customer::address.name,
            Customer::city.name,
            Customer::state.name,
            Customer::zipCode.name
        )

        // 0??? ?????? (CUST)??? FieldSet??? ???????????? ????????? 0??? ???????????? ???????????? ??????
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7)
        return lineTokenizer
    }

    @Bean
    fun flatFileStepListenerItemWriter(): ItemWriter<Any> {
        return ItemWriter { items: List<Any> ->
            items.forEach { println(it) }
        }
    }

}