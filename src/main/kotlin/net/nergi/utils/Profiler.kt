package net.nergi.utils

import kotlin.system.measureNanoTime

fun test(func: () -> Unit, amount: Int) {
    val taken = measureNanoTime {
        for (i in 0 until amount) {
            func()
        }
    }
    println("Time taken for $amount runs: $taken ns\nAverage time per run: ${taken.toDouble() / amount.toDouble()} ns")
}

private const val testString = "A- n0n -A"

fun isPalindromic(str: String): Boolean {
    for (i in 0 until str.length + 1 / 2) {
        if (str[i] != str[str.length - i - 1]) {
            return false
        }
    }

    return true
}

// Test the profiler
fun main() {
    test({ isPalindromic(testString) }, 256)
}
