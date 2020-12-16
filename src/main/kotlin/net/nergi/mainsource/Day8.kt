package net.nergi.mainsource

import net.nergi.utils.getInputFile

private val lines = getInputFile("day8.txt")
private val linesTest = getInputFile("d8t.txt")

private val instRegex = Regex("([a-z]{3}) ([+-])([0-9]+)")

private fun parseAsTriple(inst: String): Triple<String, String, String> {
    val mtch = instRegex.find(inst)!!
    val (opcode, sign, num) = mtch.groupValues.drop(1)
    return Triple(opcode, sign, num)
}

private data class MutablePair<X, Y>(var first: X, var second: Y)

private typealias InstructionSet = List<MutablePair<Triple<String, String, String>, Int>>

private val instructions: InstructionSet = lines.map {
    MutablePair(parseAsTriple(it), 0)
}
private val instructionsTest: InstructionSet = linesTest.map {
    MutablePair(parseAsTriple(it), 0)
}

private fun InstructionSet.reset() = this.forEach { it.second = 0 }

private fun InstructionSet.deepCopy(): InstructionSet {
    this.reset()

    val m: MutableList<MutablePair<Triple<String, String, String>, Int>> = mutableListOf()
    for ((inst, _) in this) {
        m.add(MutablePair(Triple(inst.first, inst.second, inst.third), 0))
    }

    return m
}

private fun eval(insts: InstructionSet, verbose: Boolean = false): Int {
    insts.reset()
    var pc = 0
    var acc = 0

    while (pc < insts.size) {
        val inst = insts[pc]
        val pcbefore = pc
        if (inst.second > 0) {
            if (verbose) {
                println("EXITED BY INFINITE LOOP ON $pc (line = ${pc + 1}) WITH acc = $acc")
            }
            return acc
        } else {
            inst.second += 1
        }

        val op = inst.first.first
        val num = if (inst.first.second == "+") inst.first.third.toInt() else -inst.first.third.toInt()

        when (op) {
            "acc" -> {
                acc += num
                pc += 1
            }
            "jmp" -> pc += num
            else -> pc += 1
        }

        if (verbose) {
            println("TRACE: $pcbefore (line = ${pcbefore + 1}) - $op [ acc = $acc | pc' = $pc ]")
        }
    }

    if (verbose) {
        println("PROGRAM EXITED NORMALLY ON $pc (line = ${pc + 1}) WITH acc = $acc")
    }
    return acc
}

private fun evalWithRetCode(insts: InstructionSet, verbose: Boolean = false, loopLimit: Int = 0): Pair<Int, Int> {
    insts.reset()
    var pc = 0
    var acc = 0

    while (pc < insts.size) {
        val inst = insts[pc]
        val pcbefore = pc
        if (inst.second > loopLimit) {
            if (verbose) {
                println("EXITED BY INFINITE LOOP ON $pc (line = ${pc + 1}) WITH acc = $acc")
            }
            return Pair(1, acc)
        } else {
            inst.second += 1
        }

        val op = inst.first.first
        val num = if (inst.first.second == "+") inst.first.third.toInt() else -inst.first.third.toInt()

        when (op) {
            "acc" -> {
                acc += num
                pc += 1
            }
            "jmp" -> pc += num
            else -> pc += 1
        }

        if (verbose) {
            println("TRACE: $pcbefore (line = ${pcbefore + 1}) - $op [ acc = $acc | pc' = $pc ]")
        }
    }

    if (verbose) {
        println("PROGRAM EXITED NORMALLY ON $pc (line = ${pc + 1}) WITH acc = $acc")
    }
    return Pair(0, acc)
}

private fun dbg() {
    for (i in instructions.indices) {
        val newInsts = instructions.deepCopy()

        when (newInsts[i].first.first) {
            "nop" -> {
                print("NOP FOUND! ")
                newInsts[i].first = Triple("jmp", newInsts[i].first.second, newInsts[i].first.third)
            }
            "jmp" -> {
                print("JMP FOUND! ")
                newInsts[i].first = Triple("nop", newInsts[i].first.second, newInsts[i].first.third)
            }
            else -> continue
        }

        val (excode, acc) = evalWithRetCode(newInsts)
        println("ATTEMPT CHANGING LINE ${i + 1}: [ exit code = $excode | acc = $acc ]")
        if (excode == 0) {
            break
        }
    }
}

fun main() {
    println(eval(instructions))
    println(eval(instructionsTest))
    dbg()
}
