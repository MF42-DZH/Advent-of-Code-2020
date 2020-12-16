package net.nergi.mainsource

import net.nergi.utils.getRawInputFile
import java.lang.StringBuilder

private enum class Cell {
    FLOOR, SEAT_FREE, SEAT_OCCUPIED;

    override fun toString(): String {
        return when (this) {
            FLOOR -> "."
            SEAT_FREE -> "L"
            SEAT_OCCUPIED -> "#"
        }
    }
}

private class Board(init: String) {
    private var content: List<List<Cell>>

    init {
        val c = mutableListOf<List<Cell>>()
        val acc = mutableListOf<Cell>()

        for (chr in init.filter { it != '\r' }) {
            if (chr == '\n') {
                c.add(acc.toList())
                acc.clear()
            } else {
                when (chr) {
                    '.' -> acc.add(Cell.FLOOR)
                    'L' -> acc.add(Cell.SEAT_FREE)
                    '#' -> acc.add(Cell.SEAT_OCCUPIED)
                }
            }
        }

        println(c.size)
        content = c.filter { it.isNotEmpty() }
    }

    /*
     * Iterates the board according to the ruleset:
     *
     *   '.' does not change
     *   'L' is the 'dead' state
     *   '#' is the 'alive' state
     *
     *   The ruleset is B0/S123, but it doesn't factor in '.'
     */
    @Throws(NoSuchMethodException::class)
    fun iterate(tolerance: Int = 4, occMethod: (Board, Int, Int) -> Int = Board::getOccupiedAdjacent) {
        val new = mutableListOf<List<Cell>>()
        val curr = mutableListOf<Cell>()

        for (y in content.indices) {
            curr.clear()

            for (x in content[y].indices) {
                val occ = occMethod(this, x, y)

                when {
                    content[y][x] == Cell.FLOOR -> curr.add(Cell.FLOOR)
                    occ == 0 && content[y][x] == Cell.SEAT_FREE -> curr.add(Cell.SEAT_OCCUPIED)
                    occ > 0 && content[y][x] == Cell.SEAT_FREE -> curr.add(Cell.SEAT_FREE)
                    occ < tolerance && content[y][x] == Cell.SEAT_OCCUPIED -> curr.add(Cell.SEAT_OCCUPIED)
                    occ >= tolerance && content[y][x] == Cell.SEAT_OCCUPIED -> curr.add(Cell.SEAT_FREE)
                    else -> throw NoSuchMethodException("Missing case in when!")
                }
            }

            new.add(curr.toList())
        }

        content = new.toList()
    }

    fun getOccupiedAdjacent(x: Int, y: Int): Int {
        val checks = listOf(
            Pair(x - 1, y - 1),
            Pair(x, y - 1),
            Pair(x + 1, y - 1),
            Pair(x - 1, y),
            Pair(x + 1, y),
            Pair(x - 1, y + 1),
            Pair(x, y + 1),
            Pair(x + 1, y + 1),
        ).filter { (x1, y1) -> x1 >= 0 && y1 >= 0 && x1 < content[0].size && y1 < content.size }

        var c = 0
        for ((x1, y1) in checks) {
            if (content[y1][x1] == Cell.SEAT_OCCUPIED) {
                c += 1
            }
        }

        return c
    }

    fun getOccupiedVisible(x: Int, y: Int): Int {
        val checks = listOf(
            Pair(-1, -1),
            Pair(0, -1),
            Pair(1, -1),
            Pair(-1, 0),
            Pair(1, 0),
            Pair(-1, 1),
            Pair(0, 1),
            Pair(1, 1),
        )

        var c = 0
        for ((dx, dy) in checks) {
            var nx = x + dx
            var ny = y + dy
            while (nx >= 0 && ny >= 0 && ny < content.size && nx < content[0].size) {
                when (content[ny][nx]) {
                    Cell.FLOOR -> {
                        nx += dx
                        ny += dy

                        continue
                    }
                    Cell.SEAT_FREE -> break
                    Cell.SEAT_OCCUPIED -> {
                        c += 1
                        break
                    }
                }
            }
        }

        return c
    }

    fun countAllOccupied(): Int = content.fold(0) { res, it -> res + it.count { r -> r == Cell.SEAT_OCCUPIED } }

    fun countAllFree(): Int = content.fold(0) { res, it -> res + it.count { r -> r == Cell.SEAT_FREE } }

    override fun toString(): String {
        val sb = StringBuilder()

        for (row in content) {
            sb.append(row.joinToString(prefix = "", postfix = "", separator = ""))
            sb.append('\n')
        }

        return sb.toString()
    }
}

fun main() {
    val currentBoard = Board(getRawInputFile("day11.txt"))
    val cbt = Board(getRawInputFile("day11.txt"))
    println(currentBoard)
    println(currentBoard.countAllFree())
    println(currentBoard.countAllOccupied())

    var oldCount: Int
    var newCount: Int
    var t = 0

    do {
        oldCount = currentBoard.countAllOccupied()
        currentBoard.iterate()
        newCount = currentBoard.countAllOccupied()

        if (oldCount == newCount) {
            t += 1
        } else {
            t = 0
        }
    } while (t < 5)

    println(currentBoard)
    println(currentBoard.countAllFree())
    println(currentBoard.countAllOccupied())

    t = 0

    do {
        oldCount = cbt.countAllOccupied()
        cbt.iterate(5, Board::getOccupiedVisible)
        newCount = cbt.countAllOccupied()

        if (oldCount == newCount) {
            t += 1
        } else {
            t = 0
        }
    } while (t < 5)

    println(cbt)
    println(cbt.countAllFree())
    println(cbt.countAllOccupied())
}
