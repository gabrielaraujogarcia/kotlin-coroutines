package br.com.example.coroutines

import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

/**
 * Exemplos extraiddos deste link: https://kotlinlang.org/docs/tutorials/coroutines/coroutines-basic-jvm.html
 */
fun main() {

    var executor = CoroutinesApplication()
    executor.first()
    executor.second()
    executor.third()
    executor.fourth()
    executor.fifth()
    executor.sixth()
}

class CoroutinesApplication {

    /**
     * The main thread (that runs the main() function) must wait until our coroutine completes, otherwise the program
     * ends before Hello is printed. Exercise: try removing the sleep() from the program above and see the result.
     */
    fun first() {

        println("Start 1")

        GlobalScope.launch {
            delay(1000)
            println("Hello")
        }

        println("After hello")

        Thread.sleep(2000)
        println("Stop 1")

    }

    /**
     * runBlocking bloqueia a thread principal até que esta co-rotina termine
     */
    fun second() {

        println("\n\nStart 2")

        GlobalScope.launch {
            delay(5000)
            println("Hello")
        }

        runBlocking {
            delay(1000)
            println("After hello need to waiting for me because I am blocking")
        }

        println("After hello")

        Thread.sleep(6000)
        println("Stop 2")


    }

    /**
     * Benchmark de thread vs coroutines.
     */
    fun third() {

        println("\n\nStart 3")

        val c = AtomicLong()
        var startAt = LocalTime.now()

        for (i in 1..1_000_000L)
            thread(start = true) {
                c.addAndGet(i)
            }

        println(c.get())
        println(Duration.between(startAt, LocalTime.now()).seconds)

        val d = AtomicLong()
        startAt = LocalTime.now()

        for (i in 1..1_000_000L)
            GlobalScope.launch {
                d.addAndGet(i)
            }

        println(d.get())
        println(Duration.between(startAt, LocalTime.now()).seconds)

        println("End 3")
    }

    /**
     * Async: returning a value from a coroutine. It is like launch {}, but returns an instance of Deferred<T>,
     * which has an await() function that returns the result of the coroutine.
     */
    fun fourth() {

        println("\n\nStart 4")

        val deferred = (1..1_000_000).map { n ->
            GlobalScope.async {
                n
            }
        }

        runBlocking {
            var sum = deferred.map {
                it.await().toLong()
            }.sum()

            println("Sum: $sum")
        }

        println("End 4")
    }


    /**
     * Check if corountines run in parallel. If we add a 1-second delay() to each of the async's, the resulting program
     * won't run for 1'000'000 seconds (over 11,5 days). But this takes about 10 seconds on my machine, so yes,
     * coroutines do run in parallel.
     */
    fun fifth() {

        println("\n\nStart 5")
        val startAt = LocalTime.now()

        val deferred = (1..1_000_000).map { n ->
            GlobalScope.async {
                delay(1000)
                n
            }
        }

        println(Duration.between(startAt, LocalTime.now()).seconds)
        println("End 5")

    }

    /**
     * Suspend functions
     */
    fun sixth() {

        println("\n\nStart 6")
        val startAt = LocalTime.now()

        for (n in 1..1_000_000) {
            GlobalScope.async {
                workload(n)
            }
        }

        println(Duration.between(startAt, LocalTime.now()).seconds)
        println("End 6")

    }

    private suspend fun workload(n: Int): Int {
        delay(1000)
        return n
    }

}

