package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test

private val bpStrings = getInputFile("day5.txt")

private data class Quint<V, W, X, Y, Z>(val first: V, val second: W, val third: X, val fourth: Y, val fifth: Z)

private fun parseID(sort: String): Quint<Int, Int, Int, Int, Long> {
    var front = 0
    var back = 127
    var left = 0
    var right = 7

    sort.forEach {
        when (it) {
            'F' -> back = (front + back) / 2
            'B' -> front = (front + back) / 2
            'L' -> right = (left + right) / 2
            'R' -> left = (left + right) / 2
            else -> println("WHAT THE HELL?!")
        }
    }

    return Quint(front, back, left, right, back.toLong() * 8L + right.toLong())
}

private fun parseIDNoQuint(sort: String): Int {
    var front = 0
    var back = 127
    var left = 0
    var right = 7

    sort.forEach {
        when (it) {
            'F' -> back = (front + back) / 2
            'B' -> front = (front + back) / 2
            'L' -> right = (left + right) / 2
            'R' -> left = (left + right) / 2
            else -> println("WHAT THE HELL?!")
        }
    }

    return back * 8 + right
}

fun main() {
    println(parseID("FBFBBFFRLRR"))
    println(parseID("BFFFBBFRRR"))
    println(parseID("FFFBBBFRRRR"))
    println(parseID("BBFFBBFRLL"))

    val mappedIDs = bpStrings.map(::parseIDNoQuint)
    val minbp = mappedIDs.minOrNull()!!
    val maxbp = mappedIDs.maxOrNull()!!

    println(maxbp)

    for (id in minbp..maxbp) {
        if (id !in mappedIDs && id - 1 in mappedIDs && id + 1 in mappedIDs) {
            println(id)
            break
        }
    }

    test({ bpStrings.map(::parseIDNoQuint).maxOrNull()!! }, 256)
    test(
        {
            val mappedIDs2 = bpStrings.map(::parseIDNoQuint)
            for (id in minbp..maxbp) {
                if (id !in mappedIDs2 && id - 1 in mappedIDs2 && id + 1 in mappedIDs2) {
                    break
                }
            }
        },
        256
    )
}
