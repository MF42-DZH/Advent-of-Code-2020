package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val tileInput = getGroupedInputFile("day20.txt")

private val tiles = tileInput.map(Tile::fromGroupString)

data class Tile(val id: Int, private var content: List<String>) {
    companion object {
        private val idReg = Regex("Tile ([0-9]+):")

        fun fromGroupString(str: String): Tile {
            val splitted = str.split(lb)

            val idString = splitted[0]
            val contents = splitted.drop(1).filter { it.isNotEmpty() }
            val id = idReg.find(idString)!!.groupValues[1].toInt()

            return Tile(id, contents)
        }
    }

    val connections: MutableList<Pair<Tile, Orientation>> = mutableListOf()

    var tileOnTop: Tile? = null
    var tileOnBottom: Tile? = null
    var tileOnLeft: Tile? = null
    var tileOnRight: Tile? = null

    fun contentNoBorder(): List<String> {
        return content.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
    }

    fun getContent(): List<String> = content

    // Adapted from /u/VictiniX888's solution
    fun rotateInPlace() {
        val rot = content.flatMap { it.withIndex() }.groupBy({ (y, _) -> y }, { (_, x) -> x }).map { (_, x) -> x.reversed() }

        content = rot.map { String(it.toCharArray()) }
    }

    // Adapted from /u/VictiniX888's solution
    fun flipHorizontal() {
        content = content.map { it.reversed() }
    }

    // Adapted from /u/VictiniX888's solution
    fun flipVertical() {
        content = content.reversed()
    }

    fun getUpperEdge(): String = content[0]

    fun getLeftEdge(): String {
        val sb = StringBuilder()
        for (str in content) {
            sb.append(str.first())
        }

        return sb.toString()
    }

    fun getRightEdge(): String {
        val sb = StringBuilder()
        for (str in content) {
            sb.append(str.last())
        }

        return sb.toString()
    }

    fun getLowerEdge(): String = content[content.size - 1]

    fun getConnections(lst: List<Tile>) {
        val matches = lst.findMatchingTiles(this)
        for ((match, orient) in matches) {
            connections.add(match to orient)
        }
    }

    fun countConnections(): Int {
        return connections.size
    }

    operator fun get(y: Int, x: Int): Char {
        return content[y][x]
    }

    override fun toString(): String {
        return "Tile $id"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Tile) {
            this.id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return this.id
    }
}

enum class Orientation(val bitData: Int) {
    UNDEF(0), UP(2), UPFLIP(3), DOWN(4), DOWNFLIP(5), LEFT(8), LEFTFLIP(9), RIGHT(16), RIGHTFLIP(17)
}

private fun List<Tile>.findTileById(id: Int): Tile? {
    val flt = this.filter { it.id == id }
    return if (flt.isEmpty()) {
        null
    } else {
        flt[0]
    }
}

fun List<Tile>.findMatchingTiles(tile: Tile): List<Pair<Tile, Orientation>> {
    val tls: MutableList<Pair<Tile, Orientation>> = mutableListOf()
    val tileEdges = listOf(tile.getUpperEdge(), tile.getLowerEdge(), tile.getLeftEdge(), tile.getRightEdge()).flatMap { listOf(it, it.reversed()) }
    // println(tileEdges)

    for (t in this) {
        if (t != tile) {
            val tEdges = listOf(t.getUpperEdge(), t.getLowerEdge(), t.getLeftEdge(), t.getRightEdge()).flatMap { listOf(it, it.reversed()) }

            for (i in 0 until 8) {
                if (tEdges[i] in tileEdges) {
                    val p = t to when (i) {
                        0 -> Orientation.UP
                        1 -> Orientation.UPFLIP
                        2 -> Orientation.DOWN
                        3 -> Orientation.DOWNFLIP
                        4 -> Orientation.LEFT
                        5 -> Orientation.LEFTFLIP
                        6 -> Orientation.RIGHT
                        7 -> Orientation.RIGHTFLIP
                        else -> Orientation.UNDEF
                    }

                    tls.add(p)
                    break
                }
            }
        }
    }

    // return null to Orientation.UNDEF
    // println(tls)
    return tls
}

// Adapted from /u/VictiniX888's solution
private fun createTileSet(tiles: List<Tile>): Tile {
    val allTiles = tiles.associateBy { it.id }
    val checked = mutableSetOf<Tile>()
    val queue = tiles.toMutableList()

    val stack = mutableListOf<Tile>()
    stack.add(0, tiles.first())

    fun Tile.borders(): List<String> {
        return listOf(this.getUpperEdge(), this.getRightEdge(), this.getLowerEdge().reversed(), this.getLeftEdge().reversed())
    }

    fun Tile.getC(i: Int): Tile? {
        return when (i) {
            0 -> this.tileOnTop
            1 -> this.tileOnRight
            2 -> this.tileOnBottom
            3 -> this.tileOnLeft
            else -> TODO("oops")
        }
    }

    fun Tile.setC(i: Int, t: Tile) {
        return when (i) {
            0 -> this.tileOnTop = t
            1 -> this.tileOnRight = t
            2 -> this.tileOnBottom = t
            3 -> this.tileOnLeft = t
            else -> TODO("oops")
        }
    }

    fun Int.wrap(): Int {
        return when (this) {
            0 -> 2
            1 -> 3
            2 -> 0
            3 -> 1
            else -> Int.MAX_VALUE
        }
    }

    while (stack.isNotEmpty()) {
        val cur = stack.removeAt(0)
        queue.remove(cur)

        val borders = cur.borders()

        queue.forEach { tile ->
            tile.borders().forEachIndexed { i, border ->
                if (border in borders) {
                    val ic = borders.indexOf(border)
                    val rt = ((ic + 4) - i.wrap()) % 4

                    if (cur.getC(ic) == null) {
                        for (k in 0 until rt) {
                            allTiles[tile.id]!!.rotateInPlace()
                        }

                        if (ic == 0 || ic == 2) {
                            allTiles[tile.id]!!.flipHorizontal()
                        } else {
                            allTiles[tile.id]!!.flipVertical()
                        }

                        cur.setC(ic, tile)
                        tile.setC(ic.wrap(), cur)

                        println("$cur has been set $tile on $ic\n$tile has been set $cur on ${ic.wrap()}")
                        stack.add(0, tile)
                    }
                } else if (border.reversed() in borders) {
                    val ic = borders.indexOf(border.reversed())
                    val rt = ((ic + 4) - i.wrap()) % 4

                    if (cur.getC(ic) == null) {
                        for (k in 0 until rt) {
                            allTiles[tile.id]!!.rotateInPlace()
                        }

                        cur.setC(ic, tile)
                        tile.setC(ic.wrap(), cur)
                        println("$cur has been set $tile on $ic\n$tile has been set $cur on ${ic.wrap()}")
                        stack.add(0, tile)
                    }
                }
            }
        }

        checked.add(cur)
    }

    val sz = sqrt(tiles.size.toDouble()).roundToInt()
    val img = Array(sz) {
        Array<Tile>(sz) {
            Tile(-1, listOf())
        }
    }

    var lf: Tile? = allTiles.values.find { it.tileOnLeft == null && it.tileOnRight == null }
    println(lf)
    for (y in img.indices) {
        var rg: Tile? = lf!!
        for (x in img[y].indices) {
            img[y][x] = rg!!
            println(rg.tileOnRight)
            rg = allTiles[rg.tileOnRight!!.id]
        }

        lf = allTiles[lf.tileOnBottom!!.id]
    }

    return Tile(
        0,
        img.map { rw ->
            rw.map { tile ->
                tile.contentNoBorder()
            }.flatMap { it.mapIndexed { i, l -> IndexedValue(i, l) } }
                .groupBy({ (y, _) -> y }, { (_, x) -> x })
                .map { (_, x) -> x.reduce { acc, l -> acc + l } }
        }.reduce { acc, l -> acc + l }
    )
}

fun main() {
    fun isSeaMonster(ts: Tile, y: Int, x: Int): Boolean {
        fun Char.sm(): Boolean = this == '#'

        return ts[y, x + 18].sm() &&
            ts[y + 1, x].sm() &&
            ts[y + 1, x + 5].sm() &&
            ts[y + 1, x + 6].sm() &&
            ts[y + 1, x + 11].sm() &&
            ts[y + 1, x + 12].sm() &&
            ts[y + 1, x + 17].sm() &&
            ts[y + 1, x + 18].sm() &&
            ts[y + 1, x + 19].sm() &&
            ts[y + 2, x + 1].sm() &&
            ts[y + 2, x + 4].sm() &&
            ts[y + 2, x + 7].sm() &&
            ts[y + 2, x + 10].sm() &&
            ts[y + 2, x + 13].sm() &&
            ts[y + 2, x + 16].sm()
    }

    fun countSeaMonsters(ts: Tile): Int {
        val ys = ts.getContent().size - 2
        val xs = ts.getContent()[0].length - 19

        var c = 0
        for (y in 0 until ys) {
            for (x in 0 until xs) {
                if (isSeaMonster(ts, y, x)) {
                    c += 1
                }
            }
        }

        return c
    }

    fun getTrueSeaMonsterCount(ts: Tile): Int {
        for (i in 0 until 4) {
            countSeaMonsters(ts).let { if (it != 0) return it }

            ts.flipVertical()
            countSeaMonsters(ts).let { if (it != 0) return it }

            ts.flipHorizontal()
            countSeaMonsters(ts).let { if (it != 0) return it }

            ts.flipVertical()
            countSeaMonsters(ts).let { if (it != 0) return it }

            ts.flipHorizontal()
            ts.rotateInPlace()
        }

        return 0
    }

    fun roughness(ts: Tile): Int {
        return (ts.getContent().fold(0) { r, i -> r + i.count { it == '#' } }) - (getTrueSeaMonsterCount(ts) * 15)
    }

    // Test data
    val tt = getGroupedInputFile("d20t.txt").map(Tile::fromGroupString)
    tt.forEach {
        // println("$it:")
        it.getConnections(tt)
    }

    // println(tt.map { it.countConnections() })

    val tc = tt.filter { it.countConnections() == 2 }
    println(4 == tc.size)
    println(tc.fold(1L) { r, it -> r * it.id.toLong() })

    System.exit(0)

    // val ts = createTileSet(tt)

    // Part 1
    tiles.forEach {
        // print("$it: ")
        it.getConnections(tiles)
        // println(it.countConnections())
    }

    val corners = tiles.filter { it.countConnections() == 2 }
    println(4 == corners.size)
    println(corners.fold(1L) { r, it -> r * it.id.toLong() })

    println("assemble the tiles plox")

    // Part 2
    TODO("what the f//k? let's be honest I'm probably going to bruteforce the answer via manual binary search")
}
