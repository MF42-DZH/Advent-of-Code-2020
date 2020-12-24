package net.nergi.mainsource

private val startCupStates = listOf(2, 8, 4, 5, 7, 3, 9, 6, 1)

private val testStates = listOf(3, 8, 9, 1, 2, 5, 4, 6, 7)

// E.g.
//
// 3 8 9 1 2 5 4 6 7 |
// 3 n n n 2 5 4 6 7 | 8 9 1
// 3 2 8 9 1 5 4 6 7
//
// 3 2 n n n 5 4 6 7 | 8 9 1
// 3 2 5 4 6 7 8 9 1
// ...

// _ _ c D e f g C _
// _ c D _ e f g C _
// c D _ _ e f g C _
// D _ _ _ e f g C c

// # _ _ _ # D # C #
// # _ _ # D _ # C #
// # _ # D _ _ # C #
// # # D _ _ _ # C #

private fun part1Moves(start: List<Int>, amount: Int = 100, max: Int = 9): List<Int?> {
    val curList: MutableList<Int?> = start.toMutableList()
    var curCupInd = 0

    for (i in 0 until amount) {
        val curCup = curList[curCupInd]

        // Step 1 - take
        val taken1 = curList[(curCupInd + 1) % curList.size]
        val taken2 = curList[(curCupInd + 2) % curList.size]
        val taken3 = curList[(curCupInd + 3) % curList.size]

        curList[(curCupInd + 1) % curList.size] = null
        curList[(curCupInd + 2) % curList.size] = null
        curList[(curCupInd + 3) % curList.size] = null

        // Step 2 - dest
        var dst = curCup!!
        do {
            dst = if (dst == 1) {
                max
            } else {
                dst - 1
            }
        } while (dst in listOf(taken1, taken2, taken3))

        // println("$curList | Dst: $dst")

        // Step 3 - move values through nulls
        for (j in 0 until 3) {
            // Rotate through nulls here
            for (k in 1 until curList.size - 1) {
                val ts1 = (curCupInd + k) % curList.size
                val ts2 = (curCupInd + k + 1) % curList.size

                if (curList[ts2] == dst) {
                    val t = curList[ts1]
                    curList[ts1] = curList[ts2]
                    curList[ts2] = t
                    // println(curList)
                    break
                }

                val t = curList[ts1]
                curList[ts1] = curList[ts2]
                curList[ts2] = t
                // println(curList)
            }
        }

        // println(curList)

        // Step 4 - fill nulls
        for (v in listOf(taken1, taken2, taken3)) {
            for (j in 1 until curList.size) {
                val tc = (curCupInd + j) % curList.size
                if (curList[tc] == null) {
                    curList[tc] = v
                    break
                }
            }
        }

        // println("${i + 1}: $curList")
        // if ((i + 1) % 10 == 0) {
        //     println("${i + 1} / $amount")
        // }

        curCupInd = (curCupInd + 1) % curList.size
    }

    return curList
}

// Adapted from /u/9xD4aPHdEeb's solution
private fun part2Moves(start: List<Int>, amount: Int, max: Int): Map<Int, Int> {
    val circle: MutableMap<Int, Int> = mutableMapOf()
    for (i in start.indices) {
        circle[start[i]] = start[(i + 1) % start.size]
    }

    var current = circle.keys.last()
    for (rnd in 0 until amount) {
        current = circle[current]!!

        val pickedUp = mutableListOf<Int>()
        var tmp = current

        for (i in 0 until 3) {
            tmp = circle[tmp]!!
            pickedUp.add(tmp)
        }
        circle[current] = circle[tmp]!!

        var dst = current - 1
        while (dst in pickedUp || dst < 1) {
            dst -= 1
            if (dst < 1) {
                dst = max
            }
        }

        var tmpv = circle[dst]!!
        circle[dst] = pickedUp[0]
        circle[pickedUp.last()] = tmpv

        // if ((rnd + 1) % 1000 == 0) {
        //     println("${rnd + 1} / $amount")
        // }
    }

    return circle
}

fun main() {
    println(part1Moves(testStates, 10))
    println(part1Moves(startCupStates, 100))

    val test = part2Moves(startCupStates, 100, 9)
    var c = 1
    for (i in 0 until 9) {
        print(test[c])
        c = test[c]!!
    }
    print('\n')

    val currentList = startCupStates.toMutableList()
    for (i in 10..1000000) {
        currentList.add(i)
    }

    val res = part2Moves(currentList, 10000000, 1000000)
    val nextToOne = res[1]!!
    val nextToNext = res[nextToOne]!!

    println(nextToOne.toLong() * nextToNext.toLong())
}
