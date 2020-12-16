package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.pass
import java.text.ParseException

private val inputSet = getInputFile("day14.txt")

private class BitMask(repr: String) {
    private val strRep = repr
    private val mask: Long
    private val bitSet: Long
    private val xLocs: List<Int>

    init {
        val rev = repr.reversed()
        var m: Long = 0.inv()
        var s: Long = 0
        val xl = mutableListOf<Int>()

        for (i in 0 until 36) {
            when (rev[i]) {
                '1' -> {
                    s = s or (1L shl i)
                    m = m and ((1L shl i).inv())
                }
                '0' -> m = m and ((1L shl i).inv())
                'X' -> xl.add(i)
                else -> pass
            }
        }

        mask = m
        bitSet = s
        xLocs = xl
    }

    fun applyMask(n: Long): Long = (n and mask) or bitSet

    fun applyPermuteMask(n: Long): List<Long> {
        val results: MutableList<Long> = mutableListOf()
        val rev = strRep.reversed()

        for (i in 0L until (1L shl xLocs.size)) {
            var cn = n
            for (j in strRep.indices) {
                when (rev[j]) {
                    '1' -> {
                        cn = cn or (1L shl j)
                    }
                    else -> pass
                }
            }

            var ccn = cn
            for (k in xLocs.indices) {
                val bit = (i shr k) and 1L
                ccn = (ccn and (1L shl xLocs[k]).inv()) or (bit shl xLocs[k])
            }

            results.add(ccn)
        }

        return results
    }
}

private enum class Operation {
    MASK, MEM
}

private data class Instruction(val op: Operation, val addr: Long, val data: String)

@Throws(ParseException::class)
private fun parseLine(line: String): Instruction {
    val spl = line.split("[", " = ").map { it.filter { ch -> ch != ']' } }
    return when (spl.size) {
        2 -> {
            // MASK
            Instruction(Operation.MASK, 0L, spl[1])
        }
        3 -> {
            // MEM
            Instruction(Operation.MEM, spl[1].toLong(), spl[2])
        }
        else -> {
            throw ParseException("I'm not sure how you did this. [$line]", 0)
        }
    }
}

private val instructions = inputSet.map(::parseLine)

private fun part1Parse() {
    var currentMask: BitMask? = null
    val memory: MutableMap<Long, Long> = mutableMapOf()

    for ((op, addr, data) in instructions) {
        when (op) {
            Operation.MASK -> currentMask = BitMask(data)
            Operation.MEM -> memory[addr] = currentMask!!.applyMask(data.toLong())
        }
    }

    println(memory.values.sum())
}

private fun part2Parse() {
    var currentMask: BitMask? = null
    val memory: MutableMap<Long, Long> = mutableMapOf()

    for ((op, addr, data) in instructions) {
        when (op) {
            Operation.MASK -> currentMask = BitMask(data)
            Operation.MEM -> {
                for (a in currentMask!!.applyPermuteMask(addr)) {
                    memory[a] = data.toLong()
                }
            }
        }
    }

    println(memory.values.sum())
}

fun main() {
    println(inputSet[0].split("[", " = ").map { it.filter { ch -> ch != ']' } })
    println(inputSet[1].split("[", " = ").map { it.filter { ch -> ch != ']' } })

    val testMask = BitMask("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X")
    val tm2 = BitMask("000000000000000000000000000000X1001X")
    val tm3 = BitMask("00000000000000000000000000000000X0XX")
    println(testMask.applyMask(11))
    println(tm2.applyPermuteMask(42))
    println(tm3.applyPermuteMask(26))

    part1Parse()
    part2Parse()
}
