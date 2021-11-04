package com.example.SpringBatchTutorial

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class Foo {
    fun bar() = "success"
    fun close() = println("close")
}

class FooTest {

    private var fooInstance: Foo? = null

    @BeforeEach
    fun setUp() {
        fooInstance = Foo()
    }

    @Test
    fun testBar() {
        val results = fooInstance?.bar()

        assertNotNull(results, "Results were null")
        assertEquals("success", results, "The test was not a success")
    }

    @AfterEach
    fun tearDown() {
        fooInstance?.close()
    }
}