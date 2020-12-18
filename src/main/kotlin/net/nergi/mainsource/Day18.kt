package net.nergi.mainsource

import net.nergi.utils.getInputFile
import java.math.BigInteger

private val homework = getInputFile("day18.txt")

typealias BinApp = (BigInteger, BigInteger) -> BigInteger

private open class Token(val cnt: String)

private class TkValue(cnt: String) : Token(cnt) {
    fun toBigInteger(): BigInteger = cnt.toBigInteger()

    override fun toString(): String = "TkValue $cnt"
}

private class TkOper(cnt: String) : Token(cnt) {
    fun getOper(): BinApp? {
        return when (cnt) {
            "+" -> { x, y -> x + y }
            "*" -> { x, y -> x * y }
            else -> null
        }
    }

    override fun toString(): String = "TkOper $cnt"
}

private open class EvToken {
    override fun toString(): String = "Undefined Token"
}

private class EvVal(val bgInt: BigInteger) : EvToken() {
    override fun toString(): String = bgInt.toString()
}

private class EvApp(val op: TkOper, val v1: EvToken, val v2: EvToken) : EvToken() {
    override fun toString(): String = "($v1 ${op.cnt} $v2)"
}

private fun tokenise(str: String): List<Token> {
    return if (str.isEmpty()) (
        emptyList()
        ) else {
        when {
            str[0].isDigit() -> {
                val pred = Char::isDigit
                listOf(TkValue(str.takeWhile(pred))) + tokenise(str.dropWhile(pred))
            }
            str[0] in "+*()" -> listOf(TkOper("${str[0]}")) + tokenise(str.drop(1))
            else -> tokenise(str.drop(1))
        }
    }
}

val part1Table: Map<String, Int> = mapOf(
    "(" to 1,
    ")" to 1,
    "$" to 2,
    "+" to 7,
    "*" to 7,
)

val part2Table: Map<String, Int> = mapOf(
    "(" to 1,
    ")" to 1,
    "$" to 2,
    "+" to 7,
    "*" to 6,
)

@Throws(IllegalArgumentException::class)
private fun parse(lst: List<Token>, precTable: Map<String, Int> = part1Table): EvToken {
    fun isSuperseder(s1: String, s2: String): Boolean {
        return precTable[s1]!! > precTable[s2]!!
    }

    fun innerParse(lst: List<Token>, exprStk: MutableList<EvToken>, opStk: MutableList<TkOper>): Pair<EvToken, List<Token>> {
        if (lst.isEmpty()) {
            while (opStk.size > 1) {
                val op = opStk.removeAt(0)
                val t1 = exprStk.removeAt(0)
                val t2 = exprStk.removeAt(0)
                exprStk.add(EvApp(op, t2, t1))
            }

            return exprStk[0] to emptyList()
        } else {
            val cur = lst[0]
            return when {
                cur is TkValue -> {
                    exprStk.add(0, EvVal(cur.toBigInteger()))
                    innerParse(lst.drop(1), exprStk, opStk)
                }
                cur is TkOper -> {
                    if (cur.cnt !in listOf("(", ")")) {
                        if (isSuperseder(cur.cnt, opStk[0].cnt)) {
                            opStk.add(0, cur)
                            innerParse(lst.drop(1), exprStk, opStk)
                        } else {
                            val op = opStk.removeAt(0)
                            val t1 = exprStk.removeAt(0)
                            val t2 = exprStk.removeAt(0)
                            exprStk.add(0, EvApp(op, t2, t1))
                            opStk.add(0, cur)
                            innerParse(lst.drop(1), exprStk, opStk)
                        }
                    } else {
                        when (cur.cnt) {
                            "(" -> {
                                val (ex, rest) = innerParse(lst.drop(1), mutableListOf(), mutableListOf(TkOper("$")))
                                exprStk.add(0, ex)
                                innerParse(rest, exprStk, opStk)
                            }
                            ")" -> {
                                innerParse(emptyList(), exprStk, opStk).first to lst.drop(1)
                            }
                            else -> throw IllegalArgumentException("How'd you get here [brk]?")
                        }
                    }
                }
                else -> throw IllegalArgumentException("How'd you get here [invType]?")
            }
        }
    }

    return innerParse(lst, mutableListOf(), mutableListOf(TkOper("$"))).first
}

private val finalInput = homework.map(::tokenise).map(::parse)
private val finalInput2 = homework.map(::tokenise).map { parse(it, part2Table) }

@Throws(IllegalArgumentException::class)
private fun eval(ev: EvToken): BigInteger {
    return when {
        ev is EvVal -> ev.bgInt
        ev is EvApp -> {
            val func = ev.op.getOper()!!
            func(eval(ev.v1), eval(ev.v2))
        }
        else -> throw IllegalArgumentException("How'd you get here [how]?")
    }
}

private val evaluated = finalInput.map(::eval)
private val evaluated2 = finalInput2.map(::eval)

fun main() {
    // Verif
    println(homework.size)
    println(finalInput.size)
    println(evaluated.size)
    println("---------------------------------------------------")

    // Test
    println(eval(parse(tokenise("1 + 2 * 3 + 4 * 5 + 6"))))
    println(eval(parse(tokenise("3 * (2 + 1) + 6"))))
    println(eval(parse(tokenise("1 + (2 * 3) + (4 * (5 + 6))"))))
    println(eval(parse(tokenise("1 + (2 * (2 + 1 * 3) + 4) * 2"))))
    println(eval(parse(tokenise("2 * 3 + (4 * 5)"))))
    println(eval(parse(tokenise("5 + (8 * 3 + 9 + 3 * 4 * 3)"))))
    println(eval(parse(tokenise("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))"))))
    println(eval(parse(tokenise("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))))
    println("---------------------------------------------------")

    // Part 1
    println(evaluated.fold(BigInteger.ZERO) { r, i -> r + i })
    for (i in 0 until 10) {
        println(finalInput[i])
    }

    // Part 2
    println(evaluated2.fold(BigInteger.ZERO) { r, i -> r + i })
    for (i in 0 until 10) {
        println(finalInput2[i])
    }
}
