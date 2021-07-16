package com.example.SpringBatchTutorial.batch

import com.example.SpringBatchTutorial.domain.Customer
import com.example.SpringBatchTutorial.domain.Transaction
import com.example.SpringBatchTutorial.fieldsetmapper.TransactionFieldSetMapper
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
class FlatFileBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun job(): Job {
        return this.jobBuilderFactory.get("FlatFileBatchJob")
            .start(copyFileStep())
            .build()
    }

    @Bean
    fun copyFileStep(): Step {
        return this.stepBuilderFactory.get("copyFileStep")
            .chunk<Any, Customer>(10)
            .reader(multiCustomerReader(null))
            .writer(itemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun multiCustomerReader(@Value("#{jobParameters['customerFile']}") inputFiles: Array<Resource>?): MultiResourceItemReader<Any> {
        return MultiResourceItemReaderBuilder<Any>()
            .name("multiCustomerReader")
            .resources(*inputFiles!!)
            .delegate(customerFileReader())
            .build()
    }

    /**
     * fixed length File
     */
    /*
    @Bean
    @StepScope
    fun customerItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): FlatFileItemReader<Customer> {
        println("inputFile: $inputFile")
        return FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .resource(inputFile!!)
            .fixedLength()
                .columns(Range(1, 11), Range(12, 12), Range(13, 22), Range(23, 26), Range(27, 46), Range(47, 62), Range(63, 64), Range(65, 69))
                .names(
                    Customer::firstName.name,
                    Customer::middleInitial.name,
                    Customer::lastName.name,
                    Customer::address.name,
                    Customer::street.name,
                    Customer::city.name,
                    Customer::state.name,
                    Customer::zipCode.name
                )
            .targetType(Customer::class.java)
            .build()
    }*/

    /**
     * delimited file
     */
    /*
    @Bean
    @StepScope
    fun customerItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): FlatFileItemReader<Customer> {
        println("inputFile: $inputFile")
        return FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .delimited()
            .names(
                Customer::firstName.name,
                Customer::middleInitial.name,
                Customer::lastName.name,
                "addressNumber",
                "street",
                Customer::city.name,
                Customer::state.name,
                Customer::zipCode.name
            )
            .fieldSetMapper(CustomerFieldSetMapper())
            .resource(inputFile!!)
            .build()
    }
     */


    /**
     * CustomerFileLoneTokenizer 사용
     * */
    /*
    @Bean
    @StepScope
    fun customerItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): FlatFileItemReader<Customer> {
        println("inputFile: $inputFile")
        return FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .lineTokenizer(CustomerFileLineTokenizer())
            .targetType(Customer::class.java)
            .resource(inputFile!!)
            .build()
    }
    */

    /**
     * PatternMatchingCompositeLineMapper 사용
     * */
    /*
    @Bean
    @StepScope
    fun customerItemReader(@Value("#{jobParameters['customerFile']}") inputFile: Resource?): FlatFileItemReader<*> {
        println("inputFile: $inputFile")
        return FlatFileItemReaderBuilder<Any>()
            .name("customerItemReader")
            .lineMapper(lineTokenizer())
            .resource(inputFile!!)
            .build()
    }
    */

    @Bean
    @StepScope
    fun customerItemReader(): FlatFileItemReader<*> {
        return FlatFileItemReaderBuilder<Any>()
            .name("customerItemReader")
            .lineMapper(lineTokenizer())
            .build()
    }

    @Bean
    fun customerFileReader() = CustomerFileReader(customerItemReader())

    @Bean
    fun lineTokenizer(): PatternMatchingCompositeLineMapper<Any> {
        val customerPrefixPattern = "CUST*"
        val transactionPrefixPattern = "TRANS*"


        // LineTokenizer 설정
        val lineTokenizers = HashMap<String, LineTokenizer>(2)
        lineTokenizers[customerPrefixPattern] = customerLineTokenizer()       // Customer 용 DelimitedLineTokenizer
        lineTokenizers[transactionPrefixPattern] = transactionLineTokenizer() // Transaction 용 DelimitedLineTokenizer


        // FieldSetMapper 설정
        val fieldSetMappers = mutableMapOf<String, FieldSetMapper<Any>>()

        val customerFieldSetMapper = BeanWrapperFieldSetMapper<Any>()
        customerFieldSetMapper.setTargetType(Customer::class.java)

        fieldSetMappers[customerPrefixPattern] = customerFieldSetMapper         // Customer 용 BeanWrapperFieldSetMapper
        fieldSetMappers[transactionPrefixPattern] = TransactionFieldSetMapper() // Transaction 용 FieldSetMapper


        // LineMapper 설정
        val lineMappers = PatternMatchingCompositeLineMapper<Any>()
        lineMappers.setTokenizers(lineTokenizers)       // 여러 LineTokenizer를 담고 있는 Map<String, LineTokenizer>
        lineMappers.setFieldSetMappers(fieldSetMappers) // 여러 FieldSetMapper를 담고 있는 Map<String, FieldSetMapper<Any>>
        return lineMappers
    }

    @Bean
    fun transactionLineTokenizer(): DelimitedLineTokenizer {
        val lineTokenizer = DelimitedLineTokenizer()

        // setIncludedFields()를 쓰지 않는 경우 컬럼 자릿수를 맞춰야 함
        // 파싱 시 FieldSet에 prefix라는 이름으로 저장하되, 매핑에서는 쓰이지 않음
        lineTokenizer.setNames(
            "prefix",
            Transaction::accountNumber.name,
            Transaction::transactionDate.name,
            Transaction::amount.name
        )

        return lineTokenizer
    }

    @Bean
    fun customerLineTokenizer(): DelimitedLineTokenizer {
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

        // 0번 필드 (CUST)는 FieldSet에 저장하지 않도록 0번 인덱스만 제외하고 기술
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7)
        return lineTokenizer
    }

    @Bean
    fun itemWriter(): ItemWriter<Any> {
        return ItemWriter { items: List<Any> ->
            items.forEach { println(it) }
        }
    }

}