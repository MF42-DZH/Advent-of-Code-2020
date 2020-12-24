package net.nergi.mainsource

import net.nergi.utils.getInputFile

private val mainTiles = getInputFile("day24.txt").map(::instsFromLine)
private val testTiles = getInputFile("d24t.txt").map(::instsFromLine)

private enum class TileInst {
    E, SE, SW, W, NW, NE
}

private typealias Instructions = List<TileInst>

@Throws(IllegalArgumentException::class)
private fun instsFromLine(line: String): Instructions {
    val insts: MutableList<TileInst> = mutableListOf()
    var prev: Char = ' '

    for (chr in line) {
        if (chr in "ns") {
            prev = chr
            continue
        }

        insts.add(
            when (chr) {
                'e' -> {
                    when (prev) {
                        's' -> {
                            prev = ' '
                            TileInst.SE
                        }
                        'n' -> {
                            prev = ' '
                            TileInst.NE
                        }
                        else -> {
                            prev = ' '
                            TileInst.E
                        }
                    }
                }
                'w' -> {
                    when (prev) {
                        's' -> {
                            prev = ' '
                            TileInst.SW
                        }
                        'n' -> {
                            prev = ' '
                            TileInst.NW
                        }
                        else -> {
                            prev = ' '
                            TileInst.W
                        }
                    }
                }
                else -> throw IllegalArgumentException("Invalid direction!")
            }
        )
    }

    return insts
}

// Note: false = white, true, black
class HexTile(var coords: Pair<Int, Int>? = null) {
    var e: HexTile? = null
    var se: HexTile? = null
    var sw: HexTile? = null
    var w: HexTile? = null
    var nw: HexTile? = null
    var ne: HexTile? = null
}

// Mutes the HexTile given
// Ref: https://www.redblobgames.com/grids/hexagons/
private fun parseInstLine(refTile: HexTile, insts: Instructions) {
    var curTile: HexTile = refTile
    var cq = 0
    var cr = 0
    for (inst in insts) {
        when (inst) {
            TileInst.E -> {
                cq += 1
                if (curTile.e == null) {
                    curTile.e = HexTile()
                }
                curTile = curTile.e!!
            }
            TileInst.SE -> {
                cr += 1
                if (curTile.se == null) {
                    curTile.se = HexTile()
                }
                curTile = curTile.se!!
            }
            TileInst.SW -> {
                cq -= 1
                cr += 1
                if (curTile.sw == null) {
                    curTile.sw = HexTile()
                }
                curTile = curTile.sw!!
            }
            TileInst.W -> {
                cq -= 1
                if (curTile.w == null) {
                    curTile.w = HexTile()
                }
                curTile = curTile.w!!
            }
            TileInst.NW -> {
                cr -= 1
                if (curTile.nw == null) {
                    curTile.nw = HexTile()
                }
                curTile = curTile.nw!!
            }
            TileInst.NE -> {
                cq += 1
                cr -= 1
                if (curTile.ne == null) {
                    curTile.ne = HexTile()
                }
                curTile = curTile.ne!!
            }
        }
    }

    curTile.coords = cq to cr
}

// Modifed DFS
private fun countBlackTiles(refTile: HexTile): Pair<Int, List<Pair<Int, Int>>> {
    val stack: MutableList<HexTile> = mutableListOf(refTile)
    val identified: MutableList<Pair<Int, Int>> = mutableListOf()

    while (stack.isNotEmpty()) {
        // Check tile
        val cur = stack.removeAt(0)
        if (cur.coords != null) {
            // println(cur.coords)
            if (cur.coords!! in identified) {
                identified.remove(cur.coords!!)
            } else {
                identified.add(cur.coords!!)
            }
        }

        // Add non-null tiles to stack
        cur.e?.let { stack.add(it) }
        cur.se?.let { stack.add(it) }
        cur.sw?.let { stack.add(it) }
        cur.w?.let { stack.add(it) }
        cur.nw?.let { stack.add(it) }
        cur.ne?.let { stack.add(it) }
    }

    // println(identified.sortedWith(compareBy({ it.first }, { it.second })))
    return identified.size to identified
}

private const val HWID = 401
private const val HHALF = HWID / 2

private var grid: MutableList<MutableList<MutableList<Boolean>>> = MutableList(HWID) {
    MutableList(HWID) {
        MutableList(HWID) { false }
    }
}

private fun getAdj(q: Int, r: Int): Int {
    val toCheck = listOf(
        q to r - 1,
        q - 1 to r,
        q - 1 to r + 1,
        q to r + 1,
        q + 1 to r,
        q + 1 to r - 1,
    )

    var cnt = 0
    for ((nq, nr) in toCheck) {
        val cx = HHALF + nq
        val cz = HHALF + nr
        val cy = HHALF + (-nq - nr)

        if (cx >= 0 && cy >= 0 && cz >= 0 && cx < HWID && cy < HWID && cz < HWID) {
            if (grid[cz][cy][cx]) {
                cnt += 1
            }
        }
    }

    return cnt
}

fun main() {
    println(instsFromLine("nwwswee"))
    println(instsFromLine("esenee"))

    // Run with short test input, should be zero
    val zrt = HexTile()
    listOf(listOf(TileInst.E), listOf(TileInst.NE, TileInst.E, TileInst.SW)).forEach {
        parseInstLine(zrt, it)
    }

    println(countBlackTiles(zrt).first)

    // Run with test input
    val trt = HexTile()
    testTiles.forEach {
        parseInstLine(trt, it)
    }

    println(countBlackTiles(trt).first)

    // Run with main input
    val mrt = HexTile()
    mainTiles.forEach {
        parseInstLine(mrt, it)
    }

    val (blk, ided) = countBlackTiles(mrt)
    println(blk)
    println(ided)

    // -----------------------------------------------------------------------
    System.exit(0)
    // -----------------------------------------------------------------------

    for ((q, r) in ided) {
        grid[HHALF + r][HHALF + (-q - r)][HHALF + q] = true
    }

    // RULE: B2/S12
    for (i in 0 until 100) {
        val newG: MutableList<MutableList<MutableList<Boolean>>> = MutableList(HWID) {
            MutableList(HWID) {
                MutableList(HWID) { false }
            }
        }

        for (z in -HHALF..HHALF) {
            for (y in -HHALF..HHALF) {
                for (x in -HHALF..HHALF) {
                    if (x + y + z != 0) {
                        continue
                    }

                    val q = x
                    val r = z

                    val current = getAdj(q, r)
                    newG[HHALF + q][HHALF + (-q - r)][HHALF + r] = when {
                        grid[HHALF + q][HHALF + (-q - r)][HHALF + r] && current == 1 -> true
                        grid[HHALF + q][HHALF + (-q - r)][HHALF + r] && current == 2 -> true
                        !grid[HHALF + q][HHALF + (-q - r)][HHALF + r] && current == 2 -> true
                        else -> false
                    }
                }
            }
        }

        grid = newG
    }

    println(grid.map { it.map { l -> l.count { v -> v } }.sum() }.sum())
}
