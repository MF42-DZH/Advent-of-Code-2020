@file:Suppress(
    "NamedArgsPositionMismatch", "NamedArgsPositionMismatch", "NamedArgsPositionMismatch",
    "NamedArgsPositionMismatch"
)

package net.nergi.mainsource

import net.nergi.utils.getInputFile
import java.math.BigInteger
import kotlin.math.abs

private val lines = getInputFile("day13.txt")

private val timestamp = lines[0].toLong()

private val busesPart1 = lines[1].split(',').filter { it != "x" }.map(String::toLong)

private val busesPart2 = lines[1].split(',')

private fun multipleUntil(start: Long, end: Long): Long {
    var s = start

    while (s < end) {
        s += start
    }

    return s
}

private fun findBusNearStamp() {
    val mmap: MutableMap<Long, Long> = mutableMapOf()
    for (bus in busesPart1) {
        mmap[bus] = multipleUntil(bus, timestamp)
    }

    val min = mmap.minByOrNull { it.value }!!

    println(mmap)
    println(min)
    println(min.key * (min.value - timestamp))
}

private fun findConsecutive() {
    // The first bus id pops up first, then the 2nd, one minute later, then the 3rd, two minutes later, etc.
    // Find differentials first
    // 2, 5 have a difference of 3
    // 4, 10 have a difference of 6
    val mmap: MutableMap<Pair<Long, Long>, Int> = mutableMapOf()
    for (p in busesPart1.zipWithNext()) {
        mmap[p] = 0
    }

    for ((b1, b2) in mmap.keys) {
        val start = busesPart2.indexOf(b1.toString())
        val end = busesPart2.indexOf(b2.toString())
        mmap[b1 to b2] = end - start
    }

    val newMap: MutableMap<Long, Long> = mutableMapOf()
    val bs = busesPart1.reversed()
    val ks = busesPart1.zipWithNext().reversed()

    for (i in ks.indices) {
        newMap[ks[i].second] = 0
        for (j in 0 until i) {
            newMap[ks[i].second] = newMap[ks[i].second]!! + mmap[mmap.keys.reversed()[j]]!!
        }
    }

    println(mmap)
    println(newMap)

    // var checkRange = LongRange(0, Long.MAX_VALUE / 1000).filter { it % bs[0] == 0L }
    // for (bus in bs.drop(1)) {
    //     checkRange = checkRange.filter { (it - newMap[bus]!!) % bus == 0L }
    // }
    //
    // println("SIZEOF: ${checkRange.size}")
    // println(checkRange[0])

    // var cn = findMultipleWithDifference(17, 41, 7)
    // for (key in mmap.keys.reversed().drop(1)) {
    //     cn = findMultipleWithDifference(cn, key.first, mmap[key]!!.toLong())
    //     println("$key: $cn")
    // }

    // println(cn)
    val ik = mmap.map { findMultipleWithDifference(it.key.second, it.key.first, it.value.toLong()) }
    println(ik)
    println(ik.fold(1.toBigInteger()) { res, it -> lcm(res, it.toBigInteger()) })
    println(ik.fold(1.toBigInteger()) { res, it -> res * it.toBigInteger() })

    // so what I need is:
    // t mod 17 = 0
    // (t - 7) mod 41 = 0
    // (t - 17) mod 383 = 0
    // (t - 25) mod 23 = 0
    // (t - 29) mod 19 = 0
    // (t - 30) mod 13 = 0
    // (t - 48) mod 631 = 0
    // (t - 54) mod 37 = 0
}

private fun String.isNumber(): Boolean = this.all(Char::isDigit)

private fun findCons() {
    val mlist: MutableList<Pair<BigInteger, BigInteger>> = mutableListOf()

    for (bus in busesPart2.indices) {
        if (busesPart2[bus].isNumber()) mlist.add((-bus).toBigInteger() to busesPart2[bus].toBigInteger())
    }

    println(mlist)

    val l = mlist.drop(1).fold(mlist[0]) { res, it -> soln(res, it) }
    println(l)
    println(l.first + l.second)
}

private fun soln(a1: Pair<BigInteger, BigInteger>, a2: Pair<BigInteger, BigInteger>): Pair<BigInteger, BigInteger> {
    val (d1, n1) = a1
    val (d2, n2) = a2
    val (m1, m2) = bezout(n1, n2)
    val coeff = m2 * n2
    val product = n1 * n2
    return ((d1 * coeff) + d2 * (BigInteger.ONE - coeff)) % product to product
}

private fun bezout(a: BigInteger, b: BigInteger): Pair<BigInteger, BigInteger> {
    return if (b == BigInteger.ZERO) {
        BigInteger.ONE to BigInteger.ZERO
    } else {
        val q = a / b
        val r = a % b
        val (u, v) = bezout(b, r)
        v to (u - (q * v))
    }
}

// DIFFERENCE IS n(l1) - n(l2)
private fun findMultipleWithDifference(l1: Long, l2: Long, target: Long): Long {
    var difference = l1 - l2
    var nl1 = l1
    var nl2 = l2
    while (difference != target) {
        val d = difference - target
        if (d > 0) {
            nl2 += if (abs(d) > l2) findExpLTE(l2, abs(d)) else l2
            difference = nl1 - nl2
        } else if (d < 0) {
            nl1 += if (abs(d) > l1) findExpLTE(l1, abs(d)) else l1
            difference = nl1 - nl2
        } else {
            break
        }
    }

    return nl1
}

private fun findExpLTE(l: Long, target: Long): Long {
    var nl = 1L

    while (nl < target) {
        nl *= l
    }

    if (nl > target) {
        nl /= l
    }

    return nl
}

private tailrec fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) {
        a
    } else {
        gcd(b, a % b)
    }
}

private fun lcm(a: Long, b: Long): Long {
    return (a * b) / gcd(a, b)
}

private tailrec fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    return if (b == BigInteger.ZERO) {
        a
    } else {
        gcd(b, a % b)
    }
}

private fun lcm(a: BigInteger, b: BigInteger): BigInteger {
    return (a * b) / gcd(a, b)
}

private fun lcm(vararg longs: Long): Long {
    return longs.drop(1).foldRight(longs[0]) { res, it -> lcm(it, res) }
}

fun main() {
    println(findExpLTE(10L, 2000L))

    println(busesPart1)
    findBusNearStamp()
    // println(findMultipleWithDifference(4, 5, 1))
    // println(findMultipleWithDifference(17, 41, 7))
    // findConsecutive()
    findCons()
}
