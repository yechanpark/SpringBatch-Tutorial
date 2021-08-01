package com.example.SpringBatchTutorial.config

import org.hibernate.SessionFactory
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Component
class HibernateBatchConfigurer(
    dataSource: DataSource,
    entityManagerFactory: EntityManagerFactory
) : DefaultBatchConfigurer(dataSource) {

    private val sessionFactory: SessionFactory
    private val transactionManager: PlatformTransactionManager

    override fun getTransactionManager(): PlatformTransactionManager {
        return this.transactionManager
    }

    init {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory::class.java)
        this.transactionManager = HibernateTransactionManager(sessionFactory)
    }
}