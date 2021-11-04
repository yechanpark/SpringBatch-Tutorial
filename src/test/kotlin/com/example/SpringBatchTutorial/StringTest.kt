package com.example.SpringBatchTutorial

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

class StringTest {

    @Test
    fun testStringEquals() {
        val michael = "Michael"
        val michael2 = michael

        assertTrue(michael == michael2)
    }
}