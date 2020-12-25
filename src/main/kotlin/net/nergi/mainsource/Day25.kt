package net.nergi.mainsource

import java.lang.Thread

/*
 * WARNING, SOLUTION USES 2 THREADS
 */

private val TRANSFORM = 20201227L
private val SUBJECT = 7L

private val pubKey1 = 9717666L
private val pubKey2 = 20089533L

// Brute forces the loop size
private class BruteForcer(val thrID: Int, val goalID: Int, val goal: Long) : Runnable {
    override fun run() {
        // Forcing the loop size
        var loopSize = 0L
        var cur = 1L

        while (cur != goal) {
            cur *= SUBJECT
            cur %= TRANSFORM

            loopSize += 1L
        }

        println("[$thrID] FOUND LS$goalID AS $loopSize")
        
        // Forcing the key
        val newSubj = if (goalID == 1) {
            pubKey2
        } else {
            pubKey1
        }

        cur = 1L
        for (i in 0L until loopSize) {
            cur *= newSubj
            cur %= TRANSFORM
        }

        println("[$thrID] FOUND KEY$goalID AS $cur")
    }
}

fun main() {
    // Part 1
    val loopForcerThreads = listOf(
        Thread(BruteForcer(0, 1, pubKey1)),
        Thread(BruteForcer(1, 2, pubKey2)),
    )

    for (thread in loopForcerThreads) {
        thread.start()
    }
}
