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
            .chunk<Map, Map>(1)
            .reader(tweetsItemReader(null, null))
            .writer(mongoItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun tweetsItemReader(
        mongoTemplate: MongoTemplate?,
        @Value("#{jobParameters['hashTag']}") hashTag: String?
    ): MongoItemReader<Map<Any, Any>> {
        return MongoItemReaderBuilder<Map<Any, Any>>()
            .name("tweetsItemReader")
                .targetType(Map::class.java)
                .jsonQuery("{ \"entities.hashtags.text\": {\$eq: ?0}}")
                .collection("tweets_collection")
                .parameterValues(Collections.singletonList(hashTag) as List<Any>)
                .pageSize(1)
                .sorts(Collections.singletonMap("created_at", Sort.Direction.ASC))
                .template(mongoTemplate!!)
                .build()
    }

    @Bean
    fun mongoItemWriter(): ItemWriter<Map> {
        return ItemWriter { items: List<Map> ->
            items.forEach { println(it) }
        }
    }

}