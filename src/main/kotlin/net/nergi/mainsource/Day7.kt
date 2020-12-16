package net.nergi.mainsource

import net.nergi.utils.getInputFile

private val fileLines = getInputFile("day7.txt").map {
    it.filter { ch -> ch !in ",." }
        .split(' ')
        .filter { word -> word !in listOf("contains", "contain", "no", "other", "bag", "bags") }
}

data class Bag(val colour: String, val inside: MutableMap<String, Long> = mutableMapOf()) {
    override fun equals(other: Any?): Boolean {
        return if (other is Bag) {
            this.colour == other.colour
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = colour.hashCode()
        result = 31 * result + inside.hashCode()
        return result
    }
}

private fun String.isNumber(): Boolean = this.all { it.isDigit() }

private fun parseLine(line: List<String>): Bag {
    val colour = line.takeWhile { !it.isNumber() }
    var rest = line.dropWhile { !it.isNumber() }

    // println(colour)
    // println(rest)

    val bag = Bag(colour.joinToString(separator = " ", prefix = "", postfix = ""))

    while (rest.isNotEmpty()) {
        val currentInt = rest[0]
        rest = rest.drop(1)

        val curcol = rest.takeWhile { !it.isNumber() }
        rest = rest.dropWhile { !it.isNumber() }

        bag.inside[curcol.joinToString(separator = " ", prefix = "", postfix = "")] = currentInt.toLong()
    }

    return bag
}

private val bags = fileLines.map(::parseLine)
private val goalOne = Bag("shiny gold")

fun <T> MutableList<T>.dequeue(): T = this.removeAt(0)

private fun expandForGold(bag: Bag): Boolean {
    val queue = mutableListOf(bag)
    // println("ORIGIN: ${bag.colour}")

    while (queue.isNotEmpty()) {
        val currentBag = queue.dequeue()
        // println("INSIDE BAG: ${currentBag.colour}")

        for (col in currentBag.inside.keys) {
            // println("CHECKING BAG: $col")
            if (col == "shiny gold") {
                // println("FOUND!")
                return true
            } else {
                // println("ENQUEUED: $col")
                queue.add(bags.filter { it.colour == col }[0])
            }
        }
    }

    return false
}

private val start = bags.filter { it.colour == "shiny gold" }[0]
private fun getAmount(bag: Bag): Long {
    var result = 0L

    for (col in bag.inside.keys) {
        val currentBag = bags.filter { it.colour == col }[0]

        result += if (currentBag.inside.isEmpty()) {
            bag.inside[col]!!
        } else {
            val currentValue = getAmount(currentBag)
            currentValue * bag.inside[col]!! + bag.inside[col]!!
        }
    }

    return result
}

fun main() {
    for (i in 0 until 10) {
        println(bags[i])
    }

    val expanded = bags.map(::expandForGold)
    println(expanded.count { it })

    println(getAmount(start))

    val ascended = bags.map { Pair(it.colour, getAmount(it)) }
    println(ascended.maxByOrNull { it.second })
}
