package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class WaterState(val st: Boolean) {
    SMOOTH(false), ROUGH(true);

    companion object {
        @Throws(IllegalArgumentException::class)
        fun fromChar(chr: Char): WaterState {
            return when (chr) {
                '.' -> SMOOTH
                '#' -> ROUGH
                else -> throw IllegalArgumentException("Invalid character! [$chr]")
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            SMOOTH -> "."
            ROUGH -> "#"
        }
    }
}

fun Int.halfTurn() = (this + 2) % 4

data class PTile(val id: Int, private var content: List<List<WaterState>>) {
    companion object {
        private val idReg = Regex("Tile ([0-9]+):")

        const val UBRD = 0
        const val RBRD = 1
        const val DBRD = 2
        const val LBRD = 3

        fun fromGroupString(gStr: String): PTile {
            val splitted = gStr.split(lb)

            val id = idReg.find(splitted[0])!!.groupValues[1].toInt()
            val contents = splitted.drop(1).filter { it.isNotEmpty() }.map { it.map(WaterState::fromChar) }
            return PTile(id, contents)
        }
    }

    var tAbove: Int? = null
    var tRight: Int? = null
    var tBelow: Int? = null
    var tLeft: Int? = null

    operator fun get(x: Int, y: Int) = content[y][x]

    fun rotateRight(amt: Int = 1) {
        for (i in 0 until amt) {
            val nList: MutableList<List<WaterState>> = mutableListOf()

            for (i in content[0].indices) {
                nList.add(content.map { it[i] }.reversed())
            }

            content = nList
        }
    }

    fun flipH() {
        content = content.map { it.reversed() }
    }

    fun flipV() {
        content = content.reversed()
    }

    fun getContentNoBorder(): List<List<WaterState>> {
        return content.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
    }

    fun getContent(): List<List<WaterState>> {
        return content
    }

    // Order: up-right-down-left
    fun getBorders(): List<List<WaterState>> {
        return listOf(
            content.first(),
            content.map { it.last() },
            content.last().reversed(),
            content.map { it.first() }.reversed(),
        )
    }

    fun getC(cNum: Int): Int? {
        return when (cNum) {
            UBRD -> tAbove
            RBRD -> tRight
            DBRD -> tBelow
            LBRD -> tLeft
            else -> throw IllegalArgumentException("Invalid collection number. [$cNum]")
        }
    }

    fun setC(cNum: Int, t: Int) {
        when (cNum) {
            UBRD -> tAbove = t
            RBRD -> tRight = t
            DBRD -> tBelow = t
            LBRD -> tLeft = t
            else -> throw IllegalArgumentException("Invalid collection number. [$cNum]")
        }
    }

    fun getNumberOfConnections(): Int {
        return listOf(tAbove, tRight, tLeft, tBelow).count { it != null }
    }
}

private val pMainInput = getGroupedInputFile("day20.txt")

private val pTestInput = getGroupedInputFile("d20t.txt")

private val mainTiles = pMainInput.map(PTile::fromGroupString)

private val testTiles = pTestInput.map(PTile::fromGroupString)

// Adapted from /u/VictiniX888's solution
private fun constructConnections(tiles: List<PTile>) {
    val tMap = tiles.associateBy { it.id }
    val remn = tiles.toMutableList()
    val stk = mutableListOf(tiles[0])

    while (stk.isNotEmpty()) {
        val current = stk.removeAt(0)
        remn.remove(current)

        val currentBrds = current.getBorders()

        remn.forEach { tile ->
            tile.getBorders().forEachIndexed { oin, brd ->
                if (brd in currentBrds) {
                    val cin = currentBrds.indexOf(brd)
                    val rot = ((cin + 4) - oin.halfTurn()) % 4

                    if (current.getC(cin) == null) {
                        tMap[tile.id]!!.rotateRight(rot)

                        if (cin == 0 || cin == 2) {
                            tMap[tile.id]!!.flipH()
                        } else {
                            tMap[tile.id]!!.flipV()
                        }

                        current.setC(cin, tile.id)
                        tile.setC(cin.halfTurn(), current.id)
                        stk.add(0, tile)
                    }
                } else if (brd.reversed() in currentBrds) {
                    val rev = brd.reversed()
                    val cin = currentBrds.indexOf(rev)
                    val rot = ((cin + 4) - oin.halfTurn()) % 4

                    if (current.getC(cin) == null) {
                        tMap[tile.id]!!.rotateRight(rot)

                        current.setC(cin, tile.id)
                        tile.setC(cin.halfTurn(), current.id)
                        stk.add(0, tile)
                    }
                }
            }
        }
    }
}

private typealias Puzzle = Array<Array<PTile>>

// Adapted from /u/VictiniX888's solution
private fun constructPuzzleFromConnections(tiles: List<PTile>): Puzzle {
    val tMap = tiles.associateBy { it.id }
    val sz = sqrt(tiles.size.toDouble()).roundToInt()
    val arr = Array(sz) {
        Array<PTile>(sz) { PTile(-1, listOf()) }
    }

    var left: PTile? = tMap.values.find { it.tAbove == null && it.tLeft == null }

    for (y in arr.indices) {
        var right: PTile? = left!!
        for (x in arr[y].indices) {
            arr[y][x] = right!!
            right = tMap[right.tRight]
        }

        left = tMap[left.tBelow]
    }

    return arr
}

// Adapted from /u/VictiniX888's solution
private fun constructLargeTile(puzzle: Puzzle): PTile {
    return PTile(
        621,
        puzzle.map { row ->
            row.map { tile ->
                tile.getContentNoBorder()
            }.flatMap { it.mapIndexed { i, l -> IndexedValue(i, l) } }
                .groupBy({ (i, _) -> i }, { (_, v) -> v })
                .map { (_, v) -> v.reduce { acc, list -> acc + list } }
        }.reduce { acc, list -> acc + list }
    )
}

private fun isSeaMonster(pz: PTile, x: Int, y: Int): Boolean {
    return pz[x + 18, y].st &&
        pz[x, y + 1].st &&
        pz[x + 5, y + 1].st &&
        pz[x + 6, y + 1].st &&
        pz[x + 11, y + 1].st &&
        pz[x + 12, y + 1].st &&
        pz[x + 17, y + 1].st &&
        pz[x + 18, y + 1].st &&
        pz[x + 19, y + 1].st &&
        pz[x + 1, y + 2].st &&
        pz[x + 4, y + 2].st &&
        pz[x + 7, y + 2].st &&
        pz[x + 10, y + 2].st &&
        pz[x + 13, y + 2].st &&
        pz[x + 16, y + 2].st
}

private fun countSeaMonsters(ts: PTile): Int {
    val ys = ts.getContent().size - 2
    val xs = ts.getContent()[0].size - 19

    var c = 0
    for (y in 0 until ys) {
        for (x in 0 until xs) {
            if (isSeaMonster(ts, x, y)) {
                c += 1
            }
        }
    }

    return c
}

// Adapted from /u/VictiniX888's solution
private fun getTrueSeaMonsterCount(ts: PTile): Int {
    println("${ts.getContent().size} | ${ts.getContent()[0].size}")

    for (i in 0 until 4) {
        countSeaMonsters(ts).let { if (it != 0) return it }

        ts.flipV()
        countSeaMonsters(ts).let { if (it != 0) return it }

        ts.flipH()
        countSeaMonsters(ts).let { if (it != 0) return it }

        ts.flipV()
        countSeaMonsters(ts).let { if (it != 0) return it }

        ts.flipH()
        ts.rotateRight()
    }

    return 0
}

private fun getRoughness(ts: PTile): Int {
    val mns = getTrueSeaMonsterCount(ts)
    return (ts.getContent().map { it.count { w -> w == WaterState.ROUGH } }.sum()) - (mns * 15)
}

fun main() {
    constructConnections(mainTiles)
    constructConnections(testTiles)

    // Verify that this works
    println("${mainTiles[0].tAbove}, ${mainTiles[0].tRight}, ${mainTiles[0].tBelow}, ${mainTiles[0].tLeft}")

    println(
        testTiles.filter { it.getNumberOfConnections() == 2 }.fold(1L) { r, i -> r * i.id.toLong() }
    )

    val testPuzzle = constructPuzzleFromConnections(testTiles)
    val testAssembled = constructLargeTile(testPuzzle)
    println(getRoughness(testAssembled))

    // Final
    println(getRoughness(constructLargeTile(constructPuzzleFromConnections(mainTiles))))
}
