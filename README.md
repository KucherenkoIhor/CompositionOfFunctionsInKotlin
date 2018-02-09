# Composition Of Functions In Kotlin
This repository contains two implementation of function composition in Kotlin and benchmark tests.

The implementation:

```Kotlin
inline operator fun <P1, R1, R2> ((R1) -> R2).plus(crossinline f: (P1) -> R1): (P1) -> R2 {
    return { p1: P1 -> this(f(p1)) }
}
```

If you need combine several operations in one sequence you can implement it as follows:
```Kotlin
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
```
To increase readability of code you can extract code from maps to separate functions:

```Kotlin
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

fun discount(price: Double) = price - price * 0.9

fun tax(price: Double) = price + 0.3

fun aid(price: Double) = price - 0.1    
```
It still contains several invoking of map(). Now we can use the composition of functions approach:
```Kotlin
@Benchmark
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
fun thirdCase(): Double {

    val prices = listOf(3.4, 5.6, 5.6, 3.4, 3.4, 3.4, 5.6, 5.6, 3.4, 3.4)

    return prices
            .map(::aid + ::tax + ::discount)
            .sum()
}
``` 
It looks much better!

And what about performance!??

#### MyBenchmark.firstCase    415.542 ± 14.232  ns/op
#### MyBenchmark.secondCase   367.386 ±  8.428  ns/op
#### MyBenchmark.thirdCase    265.401 ±  5.976  ns/op

To run benchmarks:
1) Clone the repository
2) Build a .jar file using this command at the root of the repository: ```mvn clean install```
3) To run the .jar with fast benchmarking: ```java -jar target/benchmarks.jar -wi 0 -i 1 -f 1 -tu ns -bm avgt```
   To run the .jar with default benchmarking: ```java -jar target/benchmarks.jar```


## Changelog

I added inline and crossinline based on this [suggestion] (https://github.com/KucherenkoIhor/CompositionOfFunctionsInKotlin/issues/1).

#### MyBenchmark.firstCase     399.855 ± 20.951  ns/op
#### MyBenchmark.secondCase    200  366.027 ± 12.553  ns/op
#### MyBenchmark.thirdCase     200  190.994 ±  8.581  ns/op
    
    
    
    
