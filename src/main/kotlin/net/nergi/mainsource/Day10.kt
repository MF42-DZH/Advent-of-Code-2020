package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test
import java.math.BigInteger

private val joltList = getInputFile("day10.txt").map(String::toInt)

private val noEndList = joltList.sorted()

private val sortedJoltList = listOf(0) + joltList.sorted() + listOf(joltList.maxOrNull()!! + 3)

private fun verifyList(list: List<Int>): Boolean = list.zipWithNext().map { it.second - it.first }.all { it <= 3 }

private fun verifyListWithEnds(list: List<Int>): Boolean {
    val l = listOf(0) + list + listOf(joltList.maxOrNull()!! + 3)
    return l.zipWithNext().map { it.second - it.first }.all { it <= 3 }
}

private fun differenceMult(list: List<Int>): Int {
    val diffed = list.zipWithNext().map { it.second - it.first }
    return diffed.count { it == 1 } * diffed.count { it == 3 }
}

class BigIntegerRange(override val start: BigInteger, override val endInclusive: BigInteger) : ClosedRange<BigInteger> {
    companion object {
        class BigIntegerIterator(private val start: BigInteger, private val endInclusive: BigInteger) : Iterator<BigInteger> {
            private var current: BigInteger = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): BigInteger {
                return current++
            }
        }
    }

    operator fun iterator(): Iterator<BigInteger> {
        return BigIntegerIterator(start, endInclusive)
    }
}

infix fun BigInteger.until(other: BigInteger): BigIntegerRange {
    return BigIntegerRange(this, other)
}

private fun allValidSublists(list: List<Int>): Long {
    val max = BigInteger.ONE shl list.size
    println(max.toString())

    var result = 0L

    for (num in BigInteger.ZERO until max) {
        if (num % BigInteger.valueOf(1000000000L) == BigInteger.ZERO) print("Progress: $num / ${max - BigInteger.ONE} | Size: ${result}\r")
        val current: MutableList<Int> = mutableListOf()

        for (i in list.indices) {
            if (num and (BigInteger.ONE shl i) > BigInteger.ZERO) {
                if (current.size > 0 && list[i] - current.maxOrNull()!! > 3) break
                current.add(list[i])
            }
        }

        if (verifyListWithEnds(current)) {
            result += 1
        }
    }

    print("\n")

    return result
}

val pred: (Int, Int) -> Boolean = { a, b -> b > a && b - a <= 3 }

private fun avsl(list: List<Int>, cv: Int = 0): Long {
    val nList = list.dropWhile { it < cv }
    val max = nList.maxOrNull()!!

    // println("$cv | $nList")

    return when {
        cv < max -> {
            var sum = 0L
            for (value in nList.filter { it > cv && it - cv <= 3 }) {
                sum += avsl(nList, value)
            }

            sum
        }
        cv == max -> 1L
        else -> 0L
    }
}

// private typealias NumberMap = List<Segment>

// private data class Segment(val from: Int, val to: Int)

// private fun generateNumberMap(list: List<Int>) { }

// private val nmap = generateNumberMap(listOf(0) + noEndList)

private val grouped = sortedJoltList.fold(mutableListOf<MutableList<Int>>(mutableListOf())) { res, it ->
    if (res.last().isEmpty()) {
        res.last().add(it)
    } else {
        if (it - res.last().last() >= 3) {
            res.add(mutableListOf(it))
        } else {
            res.last().add(it)
        }
    }

    res
}

private var cnt = 0L

private fun getValidCount(list: List<Int>): Long {
    cnt = 0
    getValidCount(listOf(), list)
    return cnt
}

private fun getValidCount(fixed: List<Int>, toFix: List<Int>) {
    if (toFix.isEmpty()) return
    getValidCount(fixed, toFix.drop(1))
    if (verifyList(fixed + toFix[0])) {
        ++cnt
        getValidCount(fixed + toFix[0], toFix.drop(1))
    }
}

fun main() {
    println(joltList.take(20))
    println(sortedJoltList)
    println(verifyList(sortedJoltList))
    println(differenceMult(sortedJoltList))
    // println(allValidSublists(noEndList))
    // println(avsl(noEndList))
    // println(grouped)
    // println(getValidCount(sortedJoltList))

    println(grouped.map { avsl(it, it[0]) }.fold(1L) { res, it -> res * it })

    test({ differenceMult(sortedJoltList) }, 256)
    test({ grouped.map { avsl(it, it[0]) }.fold(1L) { res, it -> res * it } }, 256)
}
