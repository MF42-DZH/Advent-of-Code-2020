package net.nergi.mainsource

import net.nergi.utils.getRawInputFile

var contents = getAllPairs(getRawInputFile("myd24p1tiles.txt"))

fun getAllPairs(str: String): List<Pair<Int, Int>> {
    val mlist: MutableList<Pair<Int, Int>> = mutableListOf()
    val reg = Regex("\\((-?\\d+), (-?\\d+)\\)")
    val matches = reg.findAll(str)

    for (match in matches) {
        val (_, q, r) = match.groupValues
        mlist.add(q.toInt() to r.toInt())
    }

    return mlist
}

private fun axialToCube(axial: Pair<Int, Int>): Triple<Int, Int, Int> {
    return Triple(axial.first, -axial.first - axial.second, axial.second)
}

private fun checkAdj(p: Pair<Int, Int>): Int {
    val (q, r) = p
    val toCheck = listOf(
        q + 1 to r,
        q to r + 1,
        q - 1 to r + 1,
        q - 1 to r,
        q to r - 1,
        q + 1 to r - 1,
    )

    return contents.count { it in toCheck }
}

fun main() {
    println(contents.size)

    for (i in 0 until 100) {
        println("${i + 1} / 100")
        val mlist: MutableList<Pair<Int, Int>> = mutableListOf()

        val qmin = contents.minByOrNull { it.first }!!.first - 1
        val qmax = contents.maxByOrNull { it.first }!!.first + 1
        val rmin = contents.minByOrNull { it.second }!!.second - 1
        val rmax = contents.maxByOrNull { it.second }!!.second + 1

        for (r in rmin..rmax) {
            for (q in qmin..qmax) {
                val (x, y, z) = axialToCube(q to r)
                if (x + y + z != 0) {
                    continue
                }

                val cp = q to r
                val adj = checkAdj(cp)
                when {
                    cp in contents && adj == 1 -> mlist.add(cp)
                    cp in contents && adj == 2 -> mlist.add(cp)
                    cp !in contents && adj == 2 -> mlist.add(cp)
                }
            }
        }

        contents = mlist
    }

    println(contents.size)
}
