package com.group.libraryapp

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JunitTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            println("beforeAll")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            println("afterAll")
        }
    }

    @Test
    fun test1() {
        println("테스트1")
    }

    @Test
    fun test2() {
        println("테스트2")
    }

}