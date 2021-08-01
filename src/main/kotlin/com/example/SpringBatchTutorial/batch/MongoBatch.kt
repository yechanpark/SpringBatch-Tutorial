package com.example.SpringBatchTutorial.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.*
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.MongoItemReader
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import java.util.*
import kotlin.collections.HashMap


@Configuration
@EnableBatchProcessing
class MongoBatch {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    fun mongoJob(): Job {
        return this.jobBuilderFactory.get("MongoJob")
            .start(copyMongoStep())
            .build()
    }

    @Bean
    fun copyMongoStep(): Step {
        return this.stepBuilderFactory.get("copyMongoStep")
            .chunk<Map<Any, Any>, Map<Any, Any>>(1)
            .reader(tweetsItemReader(null, null))
            .writer(mongoItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun tweetsItemReader(
        mongoTemplate: MongoTemplate?,
        @Value("#{jobParameters['hashTag']}") hashTag: String?
    ): MongoItemReader<HashMap<Any, Any>> {
        return MongoItemReaderBuilder<HashMap<Any, Any>>()
            .name("tweetsItemReader")
                .targetType(hashMapOf<Any, Any>()::class.java)
                .jsonQuery("{ \"entities.hashtags.text\": {\$eq: ?0}}")
                .collection("tweets_collection")
                .parameterValues(Collections.singletonList(hashTag) as List<Any>)
                .pageSize(1)
                .sorts(Collections.singletonMap("created_at", Sort.Direction.ASC))
                .template(mongoTemplate!!)
                .build()
    }

    @Bean
    fun mongoItemWriter(): ItemWriter<Map<Any, Any>> {
        return ItemWriter { items: List<Map<Any, Any>> ->
            items.forEach { println(it) }
        }
    }

}