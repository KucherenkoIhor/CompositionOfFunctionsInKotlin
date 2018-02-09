package com.ik

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class MyBenchmark {

    @Benchmark

    fun firstCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map { it - it * 0.9 }
                .map { it + 0.3 }
                .map { it - 0.1 }
                .sum()
    }

    @Benchmark
    fun secondCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(::discount)
                .map(::tax)
                .map(::aid)
                .sum()
    }

    @Benchmark
    fun thirdCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(::aid + ::tax + ::discount)
                .sum()
    }

    @Benchmark
    fun fourthCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(compose(::aid, ::tax, ::discount))
                .sum()
    }

    fun <R> compose(vararg funs: (R) -> R): (R) -> R = { x: R ->
        funs.reduceRight { acc, function -> { acc(function(it)) } }(x)
    }

    inline operator fun <P1, R1, R2> ((R1) -> R2).plus(crossinline f: (P1) -> R1): (P1) -> R2 {
        return { p1: P1 -> this(f(p1)) }
    }

    fun discount(price: Double) = price - price * 0.9

    fun tax(price: Double) = price + 0.3

    fun aid(price: Double) = price - 0.1

}
