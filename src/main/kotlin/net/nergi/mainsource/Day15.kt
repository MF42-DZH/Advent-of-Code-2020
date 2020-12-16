package net.nergi.mainsource

import java.util.HashMap

private val startInput: List<Int> = listOf(0, 8, 15, 2, 12, 1, 4)

// Adapted from /u/p88h's solution
private fun determine(amount: Int = 2020, verbose: Boolean = false) {
    val hmap: HashMap<Int, Int> = HashMap()
    var ago = 0
    var currentNumber = -1

    for (i in 1..amount) {
        currentNumber = if (i <= startInput.size) {
            startInput[i - 1]
        } else {
            ago
        }

        ago = if (hmap.containsKey(currentNumber)) {
            i - hmap[currentNumber]!!
        } else {
            0
        }

        hmap[currentNumber] = i
    }

    if (verbose) {
        println(hmap)
    }

    println(currentNumber)
}

fun main() {
    determine(2020, true)
    determine(30000000)
}
