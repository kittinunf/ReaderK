package com.merpay.readerk

import org.hamcrest.CoreMatchers.equalTo

import org.junit.Assert.assertThat
import org.junit.Test

class ReaderTest {

    @Test
    fun test_pure() {
        val reader = Reader.pure<String, Int>(5)

        val value = reader.read("1234")

        assertThat(value, equalTo(5))
    }
}
