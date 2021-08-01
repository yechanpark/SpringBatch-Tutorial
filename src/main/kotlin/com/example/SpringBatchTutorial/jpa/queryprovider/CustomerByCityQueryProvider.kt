package com.example.SpringBatchTutorial.jpa.queryprovider

import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider
import org.springframework.util.Assert
import javax.persistence.Query

class CustomerByCityQueryProvider(
    private var cityName: String = ""
): AbstractJpaQueryProvider() {

    override fun createQuery(): Query {
        val query = entityManager.createQuery("select c from CustomerForHibernate c where c.city = :city")
        query.setParameter("city", cityName)
        return query
    }

    override fun afterPropertiesSet() {
        Assert.notNull(cityName, "City name is required")
    }
}