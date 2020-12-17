package net.nergi.mainsource

import net.nergi.utils.getInputFile

private val inputSet = getInputFile("day17.txt")

private enum class PDCell {
    INACTIVE, ACTIVE;

    companion object {
        fun fromChar(ch: Char): PDCell {
            return when (ch) {
                '.' -> INACTIVE
                '#' -> ACTIVE
                else -> throw IllegalArgumentException("Invalid character! [$ch]")
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            INACTIVE -> "."
            ACTIVE -> "#"
        }
    }
}

private class PDBoard(init: List<String>, enable4d: Boolean = false) {
    companion object {
        const val W_LIMIT: Int = 15
        const val FIELD_SIZE_LIMIT: Int = 31
        const val FIELD_MIDDLE: Int = FIELD_SIZE_LIMIT / 2

        private fun createCellList(): MutableList<MutableList<MutableList<PDCell>>> {
            return MutableList(FIELD_SIZE_LIMIT) {
                MutableList(FIELD_SIZE_LIMIT) {
                    MutableList(FIELD_SIZE_LIMIT) { PDCell.INACTIVE }
                }
            }
        }

        private fun create4dCellList(): MutableList<MutableList<MutableList<MutableList<PDCell>>>> {
            return MutableList(W_LIMIT) {
                MutableList(FIELD_SIZE_LIMIT) {
                    MutableList(FIELD_SIZE_LIMIT) {
                        MutableList(FIELD_SIZE_LIMIT) { PDCell.INACTIVE }
                    }
                }
            }
        }

        private fun conv(i: Int): Int = i + FIELD_MIDDLE
    }

    private val four: Boolean
    private var content: MutableList<MutableList<MutableList<MutableList<PDCell>>>>

    init {
        four = enable4d

        // Create backing list
        if (enable4d) {
            content = create4dCellList()
        } else {
            content = mutableListOf(createCellList())
        }

        val w = if (enable4d) {
            W_LIMIT / 2
        } else {
            0
        }

        // Initialise cells
        for (i in init.indices) {
            val cstr = init[i]

            for (j in cstr.indices) {
                content[w][FIELD_MIDDLE][FIELD_MIDDLE + i][FIELD_MIDDLE + j] = PDCell.fromChar(cstr[j])
            }
        }
    }

    fun getCell(x: Int, y: Int, z: Int, w: Int = 0): PDCell? {
        val cx = conv(x)
        val cy = conv(y)
        val cz = conv(z)
        val cw = if (four) {
            w + (W_LIMIT / 2)
        } else {
            0
        }

        return if (cw < 0 || cw >= W_LIMIT || listOf(cx, cy, cz).any { it < 0 || it >= FIELD_SIZE_LIMIT }) {
            null
        } else {
            content[cw][cz][cy][cx]
        }
    }

    fun getAdjacent(x: Int, y: Int, z: Int, w: Int = 0): Int {
        var total = 0

        if (four) {
            for (dw in w - 1..w + 1) {
                for (dz in z - 1..z + 1) {
                    for (dy in y - 1..y + 1) {
                        for (dx in x - 1..x + 1) {
                            if (dx == x && dy == y && dz == z && dw == w) {
                                continue
                            } else {
                                total += if (getCell(dx, dy, dz, dw) == PDCell.ACTIVE) {
                                    1
                                } else {
                                    0
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (dz in z - 1..z + 1) {
                for (dy in y - 1..y + 1) {
                    for (dx in x - 1..x + 1) {
                        if (dx == x && dy == y && dz == z) {
                            continue
                        } else {
                            total += if (getCell(dx, dy, dz, 0) == PDCell.ACTIVE) {
                                1
                            } else {
                                0
                            }
                        }
                    }
                }
            }
        }

        return total
    }

    fun iterate(verbose: Boolean = false) {
        val newList = if (four) {
            create4dCellList()
        } else {
            mutableListOf(createCellList())
        }

        if (four) {
            for (w in 0 until W_LIMIT) {
                for (z in 0 until FIELD_SIZE_LIMIT) {
                    for (y in 0 until FIELD_SIZE_LIMIT) {
                        for (x in 0 until FIELD_SIZE_LIMIT) {
                            val adj = getAdjacent(x - FIELD_MIDDLE, y - FIELD_MIDDLE, z - FIELD_MIDDLE, w - (W_LIMIT / 2))
                            val cur = getCell(x - FIELD_MIDDLE, y - FIELD_MIDDLE, z - FIELD_MIDDLE, w - (W_LIMIT / 2))

                            if (verbose && adj > 0) {
                                println("$w $x $y $z: $adj")
                            }

                            newList[w][z][y][x] = when {
                                cur == PDCell.ACTIVE && (adj == 2 || adj == 3) -> PDCell.ACTIVE
                                cur == PDCell.INACTIVE && adj == 3 -> PDCell.ACTIVE
                                else -> PDCell.INACTIVE
                            }
                        }
                    }
                }
            }
        } else {
            for (z in 0 until FIELD_SIZE_LIMIT) {
                for (y in 0 until FIELD_SIZE_LIMIT) {
                    for (x in 0 until FIELD_SIZE_LIMIT) {
                        val adj = getAdjacent(x - FIELD_MIDDLE, y - FIELD_MIDDLE, z - FIELD_MIDDLE)
                        val cur = getCell(x - FIELD_MIDDLE, y - FIELD_MIDDLE, z - FIELD_MIDDLE)

                        if (verbose && adj > 0) {
                            println("$x $y $z: $adj")
                        }

                        newList[0][z][y][x] = when {
                            cur == PDCell.ACTIVE && (adj == 2 || adj == 3) -> PDCell.ACTIVE
                            cur == PDCell.INACTIVE && adj == 3 -> PDCell.ACTIVE
                            else -> PDCell.INACTIVE
                        }
                    }
                }
            }
        }

        content = newList.toMutableList()
    }

    override fun toString(): String {
        val s = FIELD_SIZE_LIMIT * FIELD_SIZE_LIMIT * FIELD_SIZE_LIMIT * (if (four) W_LIMIT else 1)
        var act = content.map { it.map { l -> l.map { l1 -> l1.count { c -> c == PDCell.ACTIVE } }.sum() }.sum() }.sum()

        return "Board: $act ACTIVE | ${s - act.toLong()} INACTIVE"
    }
}

private fun part1and2(init: List<String>, part2: Boolean = false) {
    val test = PDBoard(init, part2)
    for (i in 1..6) {
        println("ITERATION: $i / 6 | $test")
        test.iterate()
    }

    println(test)
}

fun main() {
    part1and2(listOf(".#.", "..#", "###"))
    part1and2(inputSet)
    part1and2(listOf(".#.", "..#", "###"), true)
    part1and2(inputSet, true)
}
