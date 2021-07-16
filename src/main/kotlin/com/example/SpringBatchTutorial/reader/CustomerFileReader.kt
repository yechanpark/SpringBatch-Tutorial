package com.example.SpringBatchTutorial.reader

import com.example.SpringBatchTutorial.domain.Customer
import com.example.SpringBatchTutorial.domain.Transaction
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream
import org.springframework.core.io.Resource

class CustomerFileReader(
    private var delegate: ResourceAwareItemReaderItemStream<*>
): ResourceAwareItemReaderItemStream<Any> {

    private var curItem: Any? = null

    override fun read(): Customer? {
        curItem = curItem ?: delegate.read()

        val item = curItem as Customer?
        curItem = null

        return item?.let {
            while (peek() is Transaction) {
                item.transactions.add(curItem as Transaction)
                curItem = null
            }
            it
        }
    }

    private fun peek(): Any? {
        curItem = curItem ?: delegate.read()
        return curItem
    }

    override fun open(executionContext: ExecutionContext) = delegate.open(executionContext)
    override fun update(executionContext: ExecutionContext) = delegate.update(executionContext)
    override fun close() = delegate.close()

    override fun setResource(resource: Resource) {
        println(resource.filename)
        this.delegate.setResource(resource)
    }

}