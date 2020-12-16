package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test

private val numbers = getInputFile("day9.txt").map(String::toLong)

private fun isSumOfLast(numIndex: Int, fromLast: Int = 25, list: List<Long> = numbers): Boolean {
    for (i in numIndex - fromLast until numIndex) {
        for (j in i + 1 until numIndex) {
            if (list[i] + list[j] == list[numIndex]) {
                return true
            }
        }
    }

    return false
}

inline fun <reified T> scanLUntil(func: (T, T) -> T, init: T, list: List<T>, pred: (T) -> Boolean): List<T> {
    val result = mutableListOf(init)
    var acc = init
    for (item in list) {
        acc = func(acc, item)
        result.add(acc)

        println("$acc | $result")

        if (pred(acc)) break
    }

    return result
}

inline fun <reified T> scanL1Until(func: (T, T) -> T, list: List<T>, pred: (T) -> Boolean): List<T> = scanLUntil(func, list[0], list.drop(1), pred)

private fun sumOfRangeInLast(numIndex: Int, list: List<Long> = numbers): Long {
    for (groupSize in 2 until numIndex) {
        for (offset in 0..numIndex - groupSize) {
            val group = list.drop(offset).take(groupSize)
            if (group.sum() == list[numIndex]) {
                return group.minOrNull()!! + group.maxOrNull()!!
            }
        }
    }

    return 0L
}

fun main() {
    var indexOfWeakness = -1

    // Test data
    val testData = listOf(35L, 20L, 15L, 25L, 47L, 40L, 62L, 55L, 65L, 95L, 102L, 117L, 150L, 182L, 127L, 219L, 299L, 277L, 309L, 576L)
    for (i in 5 until testData.size) {
        if (isSumOfLast(i, 5, testData)) {
            println("${testData[i]} has a sum in of two in the last 5.")
        } else {
            println("${testData[i]} is not a sum of two in the last 5.")
            println("${testData[i]} has a range minmax sum of ${sumOfRangeInLast(i, testData)}.")
        }
    }

    // Part 1
    println("There are ${numbers.size} numbers.")
    for (i in 25 until numbers.size) {
        if (!isSumOfLast(i)) {
            println(numbers[i])
            indexOfWeakness = i
            break
        }
    }

    // Part 2
    println(sumOfRangeInLast(indexOfWeakness))

    // Testing
    test({ for (i in 25 until numbers.size) if (!isSumOfLast(i)) break }, 256)
    test({ sumOfRangeInLast(indexOfWeakness) }, 256)
}
