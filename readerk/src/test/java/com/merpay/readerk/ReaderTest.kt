package com.merpay.readerk

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ReaderTest {

    @Test
    fun pure() {
        val just5Reader = Reader.pure<String, Int>(5)

        assertThat(just5Reader.runReader("1234"), equalTo(5))
    }

    @Test
    fun local() {
        val countLengthReader = Reader { s: String -> s.length }

        val intToCountLengthReader = countLengthReader.local { i: Int -> i.toString() }

        assertThat(intToCountLengthReader.runReader(489), equalTo(3))
    }

    @Test
    fun ask() {
        val identityReader = Reader.ask<Boolean>()

        assertThat(identityReader.runReader(true), equalTo(true))
    }

    @Test
    fun map1() {
        //find absolute
        val originalReader = Reader { i: Int -> if (i > 0) i - 0 else 0 - i }

        //count the digit of absoluted value
        val newReader = originalReader.map { it.toString().length }

        assertThat(newReader.runReader(34), equalTo(2))
        assertThat(newReader.runReader(-458), equalTo(3))
    }

    @Test
    fun map2() {
        //generate item of x, y items
        val originalReader = Reader<Pair<Int, Int>, IntArray> { (x, y) -> IntArray(y) { x } }

        //map them to be the sum of all number
        val newReader = originalReader.map { it.reduce { acc, item -> acc + item } }

        assertThat(newReader.runReader(3 to 5), equalTo(15))
    }

    @Test
    fun flatMap() {
        //identity list of string reader
        val originalReader = Reader.ask<List<String>>()

        //flatMap so it changes to join of all of items
        val newReader = originalReader.flatMap { Reader { l: List<String> -> l.joinToString("&") } }

        assertThat(newReader.runReader(listOf("1", "2", "3")), equalTo("1&2&3"))
    }
}
