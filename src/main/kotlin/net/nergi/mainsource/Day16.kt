package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile

private val inputGroups = getGroupedInputFile("day16.txt").map { it.filter { ch -> ch != '\r' }.split('\n') }

private val myTicket = inputGroups[1].drop(1)

private val nearbyTickets = inputGroups[2].drop(1)

private data class Criteria(val name: String, val range1: IntRange, val range2: IntRange)

private val reg: Regex = Regex("(.+): (\\d+)-(\\d+) or (\\d+)-(\\d+)")

private val givenFields = inputGroups[0].map {
    val match = reg.find(it)
    val groups = match!!.groupValues
    Criteria(groups[1], (groups[2].toInt())..(groups[3].toInt()), (groups[4].toInt())..(groups[5].toInt()))
}

private fun isValidNum(i: Int): Pair<Boolean, Int?> {
    for ((_, r1, r2) in givenFields) {
        if (i in r1 || i in r2) {
            return true to null
        }
    }
    return false to i
}

val allTickets = myTicket + nearbyTickets

val validTickets: List<String> = allTickets.filter { isTicketValid(it).first }

private fun isTicketValid(t: String): Pair<Boolean, Int> {
    var b = true
    var er = 0

    val values = t.split(',').map(String::toInt)
    for (v in values) {
        val (validity, intIfInvalid) = isValidNum(v)

        b = b && validity
        if (intIfInvalid != null) {
            er += intIfInvalid
        }
    }

    return b to er
}

private fun determineOrder(): List<Criteria> {
    val nTicketValues = (myTicket + validTickets).map { it.split(',').map { n -> n.toInt() } }

    val ticketFields: MutableList<List<Int>> = mutableListOf()
    for (i in nTicketValues[0].indices) {
        ticketFields.add(nTicketValues.map { it[i] })
    }

    val tfieldsMut = ticketFields.toMutableList()
    val availableFields = givenFields.toMutableList()
    val foundFields: MutableList<Criteria> = MutableList(20) { Criteria("N/A", 0..1, 0..1) }
    val exclusions: MutableList<Int> = mutableListOf()

    while (availableFields.isNotEmpty()) {
        for (i in tfieldsMut.indices) {
            if (i in exclusions) {
                continue
            }

            val valueSet = tfieldsMut[i]
            val validCrits = availableFields.filter { valueSet.all { v -> v in it.range1 || v in it.range2 } && it !in foundFields }

            if (validCrits.size == 1) {
                exclusions.add(i)
                foundFields[i] = validCrits[0]
                availableFields.remove(validCrits[0])
                break
            }
        }
    }

    return foundFields
}

fun main() {
    // Part 1
    println(nearbyTickets.map { isTicketValid(it).second }.sum())

    // Part 2
    val order = determineOrder()
    val mt = myTicket[0].split(',').map(String::toInt)
    var product = 1L

    for (i in mt.indices) {
        if ("departure" in order[i].name) {
            product *= mt[i].toLong()
            println("${mt[i]} | ${order[i].name}")
        }
    }

    println(product)
}
