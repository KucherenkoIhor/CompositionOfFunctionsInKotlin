package com.ik

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

class Student(
        val firstName: String,
        val secondName: String,
        val age: Int)

val students = listOf(
        Student("Jacob", "Smith", 20),
        Student("Isabella", "Williams", 24),
        Student("Ava", "Johnson", 19),
        Student("Anthony", "Taylor", 18),
        Student("Elijah", "Davis", 17),
        Student("Daniel", "Moore", 24),
        Student("Ethan", "Thomas", 30),
        Student("Elijah", "Jackson", 25),
        Student("Jacob", "Anderson", 18),
        Student("Joshua", "Miller", 29),
        Student("Liam", "Davis", 22)
)


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class MyBenchmark {

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun firstCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map { it - it * 0.9 }
                .map { it + 0.3 }
                .map { it - 0.1 }
                .sum()
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun secondCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(::discount)
                .map(::tax)
                .map(::aid)
                .sum()
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun thirdCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(::aid + ::tax + ::discount)
                .sum()
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun fourthCase(): Double {

        val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

        return prices
                .map(compose(::aid, ::tax, ::discount))
                .sum()
    }

    fun ageMoreThan20(student: Student): Boolean = student.age > 20

    fun firstNameStartsWithE(student: Student): Boolean = student.firstName.startsWith("E")

    fun theLengthOfSecondNameMoreThan5(student: Student): Boolean = student.secondName.length > 5


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun filteringFirstCase(): List<Student> {

        return students
                .filter(::ageMoreThan20)
                .filter(::firstNameStartsWithE)
                .filter(::theLengthOfSecondNameMoreThan5)
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    fun filteringSecondCase(): List<Student> {
        return students
                .filter(::ageMoreThan20 and ::firstNameStartsWithE and ::theLengthOfSecondNameMoreThan5)
    }


    inline infix fun <P> ((P) -> Boolean).and(crossinline predicate: (P) -> Boolean): (P) -> Boolean {
        return { p: P -> this(p) && predicate(p) }
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
