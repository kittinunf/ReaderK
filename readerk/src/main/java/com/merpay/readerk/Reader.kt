package com.merpay.readerk

class Reader<T : Any, U : Any>(val runReader: (T) -> U) {

    companion object {
        //This is a wrapper over identity function
        fun <T : Any> ask(): Reader<T, T> = Reader { t: T -> t }
    }

    fun <R : Any> local(f: (R) -> T): Reader<R, U> = Reader { r: R -> runReader(f(r)) }
}

//monad
fun <T : Any, Value : Any> Reader.Companion.pure(v: Value): Reader<T, Value> =
        Reader { v }

fun <T : Any, U : Any, R : Any> Reader<T, U>.flatMap(transform: (U) -> Reader<T, R>): Reader<T, R> =
        Reader { t: T -> transform(runReader(t)).runReader(t) }

//functor
fun <T : Any, U : Any, R : Any> Reader<T, U>.map(transform: (U) -> R): Reader<T, R> = Reader { t: T -> transform(runReader(t)) }

