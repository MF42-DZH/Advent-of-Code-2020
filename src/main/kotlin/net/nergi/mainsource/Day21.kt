package net.nergi.mainsource

import net.nergi.utils.getInputFile

private val lineReg = Regex("(.+ ?) \\(contains (.+)\\)")

private typealias IngredientList = Pair<List<String>, List<String>>

// Order: Ingredients, Allergens
@Throws(IllegalArgumentException::class)
private fun getLineData(str: String): IngredientList {
    val match = lineReg.find(str) ?: throw IllegalArgumentException("Invalid line.")
    val (_, ing, alg) = match.groupValues

    return (ing.split(' ')) to (alg.split(", "))
}

private fun <T> Set<T>.symDiff(other: Set<T>): Set<T> {
    return (this.minus(other)).plus(other.minus(this))
}

private fun <T> List<T>.symDiff(other: List<T>): List<T> {
    return (this.minus(other)).plus(other.minus(this))
}

private fun parseInputSet(list: List<IngredientList>): Map<String, List<Set<String>>> {
    val mmap: MutableMap<String, MutableList<Set<String>>> = mutableMapOf()
    for ((ings, algs) in list) {
        for (alg in algs) {
            if (alg !in mmap.keys) {
                mmap[alg] = mutableListOf()
            }

            mmap[alg]!!.add(ings.toSet())
        }
    }

    return mmap
}

private fun parseAISet(setMap: Map<String, List<Set<String>>>): Map<String, Set<String>> {
    val mmap: MutableMap<String, Set<String>> = mutableMapOf()
    for ((al, ingLists) in setMap) {
        val ing = ingLists.reduce { r, it -> r.intersect(it) }
        mmap[al] = ing
    }

    return mmap
}

private fun countRemainingIngredients(ingredients: List<List<String>>, toNotCount: Set<String>): Int {
    return ingredients.fold(0) { r, it -> r + (it.count { i -> i !in toNotCount }) }
}

private val mainInput = getInputFile("day21.txt").map(::getLineData)
private val testInput = getInputFile("d21t.txt").map(::getLineData)

private val mainMap = parseInputSet(mainInput)
private val testMap = parseInputSet(testInput)

private val mainFinalMap = parseAISet(mainMap)
private val testFinalMap = parseAISet(testMap)

private fun determineCanonicalDangerous(mapping: Map<String, Set<String>>): String {
    val availableAllergens = mapping.keys.toMutableList()
    val found: MutableList<Pair<String, String>> = mutableListOf()

    while (availableAllergens.isNotEmpty()) {
        val f = mapping.filter { (_, v) -> v.filter { it !in found.map { it.second } }.size == 1 }
        val s = f.values.first().filter { it !in found.map { it.second } }[0]

        found.add(f.keys.first() to s)
        println("$availableAllergens | $found | $s")

        availableAllergens.remove(f.keys.first())
    }

    found.sortBy { it.first }
    return found.map { it.second }.joinToString(prefix = "", postfix = "", separator = ",")
}

fun main() {
    // Verify
    println(testInput)
    println(testMap)
    println(testFinalMap)
    println(countRemainingIngredients(testInput.map { it.first }, testFinalMap.values.reduce { r, it -> r.union(it) }))
    println(determineCanonicalDangerous(testFinalMap))

    println("-------------------------------------------------------------------")

    // Part 1
    println(mainFinalMap)
    println(countRemainingIngredients(mainInput.map { it.first }, mainFinalMap.values.reduce { r, it -> r.union(it) }))
    println(determineCanonicalDangerous(mainFinalMap))
}
