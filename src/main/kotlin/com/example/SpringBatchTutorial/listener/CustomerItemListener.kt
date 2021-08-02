package com.example.SpringBatchTutorial.listener

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.annotation.OnReadError
import org.springframework.batch.item.file.FlatFileParseException

class CustomerItemListener {
    private val logger: Log = LogFactory.getLog(CustomerItemListener::class.java)

    @OnReadError
    fun onReadError(e: Exception) {
        if (e is FlatFileParseException) {
            val ffpe: FlatFileParseException = e
            val errorMessage = """
                An error occured while processing the ${ffpe.lineNumber} line of the file. Below was the faulty input.
                ${ffpe.input}
            """.trimIndent()
            logger.error(errorMessage, ffpe)
        } else {
            logger.error("An error has occurred", e)
        }
    }
}