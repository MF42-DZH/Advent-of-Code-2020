package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test
import kotlin.math.abs

private val directions = getInputFile("day12.txt")

private enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun next(): Direction {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    fun prev(): Direction {
        return when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }
}

private fun getManhDist(): Long {
    var x: Long = 0
    var y: Long = 0
    var cDir = Direction.EAST

    // Parse dir
    for (move in directions) {
        val dir = move[0]
        val num = move.drop(1).toLong()

        when (dir) {
            'N' -> y += num
            'S' -> y -= num
            'E' -> x += num
            'W' -> x -= num
            'L' -> for (i in 0 until num / 90L) cDir = cDir.prev()
            'R' -> for (i in 0 until num / 90L) cDir = cDir.next()
            'F' -> {
                when (cDir) {
                    Direction.NORTH -> y += num
                    Direction.SOUTH -> y -= num
                    Direction.EAST -> x += num
                    Direction.WEST -> x -= num
                }
            }
        }
    }

    return abs(x) + abs(y)
}

fun getNewManhDist(): Long {
    var wx: Long = 10
    var wy: Long = 1
    var sx: Long = 0
    var sy: Long = 0

    for (move in directions) {
        val dir = move[0]
        val num = move.drop(1).toLong()

        when (dir) {
            'N' -> wy += num
            'S' -> wy -= num
            'E' -> wx += num
            'W' -> wx -= num
            'L' -> {
                // print("L-ROT: $wx | $wy")
                for (i in 0 until num / 90L) {
                    val tx = wx
                    wx = -wy
                    wy = tx
                }
                // println(" -> $wx | $wy")
            }
            'R' -> {
                // print("R-ROT: $wx | $wy")
                for (i in 0 until num / 90L) {
                    val tx = wx
                    wx = wy
                    wy = -tx
                }
                // println(" -> $wx | $wy")
            }
            'F' -> {
                sx += num * wx
                sy += num * wy
            }
        }
    }

    return abs(sx) + abs(sy)
}

fun main() {
    println(getManhDist())
    println(getNewManhDist())
    test({ getManhDist() }, 256)
    test({ getNewManhDist() }, 256)
}
