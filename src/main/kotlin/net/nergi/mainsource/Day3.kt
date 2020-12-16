package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test

private val lines = getInputFile("day3.txt")

const val TREE = '#'

fun getCellAt(x: Int, y: Int): Char {
    val line = lines[y]
    return line[x % line.length]
}

operator fun Pair<Int, Int>.plus(a: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + a.first, this.second + a.second)
}

private val slopes = listOf(
    Pair(1, 1),
    Pair(3, 1),
    Pair(5, 1),
    Pair(7, 1),
    Pair(1, 2),
)

private val treeCounts = mutableListOf(
    0,
    0,
    0,
    0,
    0,
)

private fun resetTreeCounts() {
    treeCounts[0] = 0
    treeCounts[1] = 0
    treeCounts[2] = 0
    treeCounts[3] = 0
    treeCounts[4] = 0
}

private val bottom = lines.size

fun evaluate() {
    resetTreeCounts()
    for (i in slopes.indices) {
        var coords: Pair<Int, Int> = Pair(0, 0)
        while (coords.second < bottom) {
            if (getCellAt(coords.first, coords.second) == TREE) {
                ++treeCounts[i]
            }

            coords += slopes[i]
        }

        println("Trees for (${slopes[i].first}, ${slopes[i].second}): ${treeCounts[i]}")
    }

    println("Product: ${treeCounts.fold(1L) { res, it -> res * it.toLong() }}")
}

fun evaluateNoPrint(): Long {
    resetTreeCounts()
    for (i in slopes.indices) {
        var coords: Pair<Int, Int> = Pair(0, 0)
        while (coords.second < bottom) {
            if (getCellAt(coords.first, coords.second) == TREE) {
                ++treeCounts[i]
            }

            coords += slopes[i]
        }
    }

    return treeCounts.fold(1L) { res, it -> res * it.toLong() }
}

fun main() {
    evaluate()
    test(::evaluateNoPrint, 256)
}
