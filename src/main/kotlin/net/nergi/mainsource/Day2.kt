package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test

private val counts: MutableMap<Char, Int> = mutableMapOf()

fun parsedPassword(passLine: String): Boolean {
    counts.clear()

    val parts = passLine.split(": ")

    // The format guarantees 2 parts, so:
    // Parse requirements
    val reqParts = parts[0].split(' ')
    val countReq = reqParts[0].split('-')
    val requirement = Pair(reqParts[1][0], Pair(countReq[0].toInt(), countReq[1].toInt()))

    // Parse char counts
    for (ch in parts[1]) {
        if (counts[ch] == null) {
            counts[ch] = 0
        }

        counts[ch] = counts[ch]!! + 1
    }

    // Test validity
    val c = counts[requirement.first]
    return if (c == null) {
        false
    } else {
        c >= requirement.second.first && c <= requirement.second.second
    }
}

fun parsedPasswordReg(passLine: String): Boolean {
    counts.clear()

    // The format guarantees 2 parts, so:
    // Parse requirements
    val line = parse(passLine)!!

    // Parse char counts
    for (ch in line.pass) {
        if (counts[ch] == null) {
            counts[ch] = 0
        }

        counts[ch] = counts[ch]!! + 1
    }

    // Test validity
    val c = counts[line.char]
    return if (c == null) {
        false
    } else {
        c >= line.num1 && c <= line.num2
    }
}

fun newParsedPassword(passLine: String): Boolean {
    val parts = passLine.split(": ")

    // The format guarantees 2 parts, so:
    // Parse requirements
    val reqParts = parts[0].split(' ')
    val countReq = reqParts[0].split('-')
    val requirement = Pair(reqParts[1][0], Pair(countReq[0].toInt(), countReq[1].toInt()))

    // Test validity
    return (parts[1].safeGet(requirement.second.first - 1) == requirement.first) xor
        (parts[1].safeGet(requirement.second.second - 1) == requirement.first)
}

fun newParsedPasswordReg(passLine: String): Boolean {
    // The format guarantees 2 parts, so:
    // Parse requirements
    val line = parse(passLine)!!

    // Test validity
    return (line.pass.safeGet(line.num1 - 1) == line.char) xor
        (line.pass.safeGet(line.num2 - 1) == line.char)
}

fun String.safeGet(index: Int): Char? {
    return if (index < 0 || index >= this.length) {
        null
    } else {
        this[index]
    }
}

data class PasswordLine(val num1: Int, val num2: Int, val char: Char, val pass: String)

val passRegex = Regex("([0-9]+)-([0-9]+) (\\S): (\\S+)")

fun parse(str: String): PasswordLine? {
    val result = passRegex.find(str) ?: return null
    val (_, n1, n2, ch, ps) = result.groupValues

    return PasswordLine(n1.toInt(), n2.toInt(), ch[0], ps)
}

val passes = getInputFile("day2.txt")

fun main() {
    parse("1-3 a: abcde")

    println("abcde (a; 1-3): ${parsedPassword("1-3 a: abcde")}")
    println("bcdef (a; 1-3): ${parsedPassword("1-3 a: bcdef")}")
    println(passes.map { parsedPassword(it) }.count { it })

    println("abcde (a; 1-3): ${newParsedPassword("1-3 a: abcde")}")
    println("abade (a; 1-3): ${newParsedPassword("1-3 a: abade")}")
    println(passes.map { newParsedPassword(it) }.count { it })

    println("abcde (a; 1-3): ${parsedPasswordReg("1-3 a: abcde")}")
    println("bcdef (a; 1-3): ${parsedPasswordReg("1-3 a: bcdef")}")
    println(passes.map { parsedPasswordReg(it) }.count { it })

    println("abcde (a; 1-3): ${newParsedPasswordReg("1-3 a: abcde")}")
    println("abade (a; 1-3): ${newParsedPasswordReg("1-3 a: abade")}")
    println(passes.map { newParsedPasswordReg(it) }.count { it })

    parse("1-3 a: abcde")

    test(::p1, 256)
    test(::p2, 256)
    test(::p1r, 256)
    test(::p2r, 256)
}

fun p1(): Int {
    return passes.map { parsedPassword(it) }.count { it }
}

fun p2(): Int {
    return passes.map { newParsedPassword(it) }.count { it }
}

fun p1r(): Int {
    return passes.map { parsedPasswordReg(it) }.count { it }
}

fun p2r(): Int {
    return passes.map { newParsedPasswordReg(it) }.count { it }
}
