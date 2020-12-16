package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb
import net.nergi.utils.test

private val groupAnswers = getGroupedInputFile("day6.txt")

private fun parseGroup(answers: String): Int = answers.filter { it !in lb }.toHashSet().size

private fun parseGroupPart2(answers: String): Int {
    var s = 0
    for (ch in "abcdefghijklmnopqrstuvwxyz") {
        if (answers.split(lb).all { ch in it }) {
            ++s
        }
    }

    return s
}

fun main() {
    println(groupAnswers[0])
    println(parseGroup("aa${lb}aa${lb}aa${lb}aa$lb"))
    println(parseGroup(groupAnswers[0]))

    println(groupAnswers.map(::parseGroup).sum())
    println(groupAnswers.map(::parseGroupPart2).sum())

    test({ groupAnswers.map(::parseGroup).sum() }, 256)
    test({ groupAnswers.map(::parseGroupPart2).sum() }, 256)
}
