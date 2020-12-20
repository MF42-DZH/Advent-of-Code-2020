package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb
import net.nergi.utils.pass

private val tileInput = getGroupedInputFile("day20.txt")

private val tiles = tileInput.map(Tile::fromGroupString)

data class Tile(val id: Int, val content: List<String>) {
    companion object {
        private val idReg = Regex("Tile ([0-9]+):")

        fun fromGroupString(str: String): Tile {
            val splitted = str.split(lb)

            val idString = splitted[0]
            val contents = splitted.drop(1)
            val id = idReg.find(idString)!!.groupValues[1].toInt()

            return Tile(id, contents)
        }
    }

    var tileAtTop: Tile? = null
    var tileAtLeft: Tile? = null
    var tileAtRight: Tile? = null
    var tileAtBottom: Tile? = null

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
            when (orient) {
                Orientation.UNDEF -> pass
                Orientation.UP -> tileAtTop = match
                Orientation.DOWN -> tileAtBottom = match
                Orientation.LEFT -> tileAtLeft = match
                Orientation.RIGHT -> tileAtTop = match
            }
        }
    }

    override fun toString(): String {
        return "Tile $id"
    }
}

enum class Orientation {
    UNDEF, UP, LEFT, RIGHT, DOWN
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
    for (t in this) {
        if (t != tile) {
            val tEdges = listOf(t.getUpperEdge(), t.getLowerEdge(), t.getLeftEdge(), t.getRightEdge()).flatMap { listOf(it, it.reversed()) }
            val tileEdges = listOf(tile.getUpperEdge(), tile.getLowerEdge(), tile.getLeftEdge(), tile.getRightEdge()).flatMap { listOf(it, it.reversed()) }

            for (i in 0 until 8) {
                if (tEdges[i] in tileEdges) {
                    val p = t to when (i) {
                        in 0..1 -> Orientation.UP
                        in 2..3 -> Orientation.DOWN
                        in 4..5 -> Orientation.LEFT
                        in 6..7 -> Orientation.RIGHT
                        else -> Orientation.UNDEF
                    }

                    tls.add(p)
                }
            }
        }
    }

    // return null to Orientation.UNDEF
    return tls
}

fun main() {
    tiles.forEach {
        println(it)
        it.getConnections(tiles)
    }

    println("assemble the tiles plox")
}
