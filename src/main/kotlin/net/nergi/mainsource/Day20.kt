package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb
import net.nergi.utils.pass

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

    fun contentNoBorder(orient: Orientation): List<String> {
        return content.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
    }

    // Adapted from /u/VictiniX888's solution
    fun rotateInPlace(right: Boolean = false) {
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
            sb.append(str[0])
        }

        return sb.toString()
    }

    fun getRightEdge(): String {
        val sb = StringBuilder()
        for (str in content) {
            sb.append(str[str.length - 1])
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

// Holds a set of connected tiles together
private class TileSet {
    fun contains(tile: Tile): Boolean {
        TODO()
    }
}

fun main() {
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
