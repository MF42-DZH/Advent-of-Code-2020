package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb

private typealias Deck = MutableList<Int>

private fun Deck.readFirst(): Int = this[0]

private fun Deck.drawFirst(): Int = this.removeAt(0)

private data class Player(val id: Int, val deck: Deck) {
    fun read(): Int {
        return deck.readFirst()
    }

    fun draw(): Int {
        return deck.drawFirst()
    }

    fun placeAtBottom(card: Int) {
        deck.add(card)
    }

    fun getScore(): Long {
        return deck.reversed().foldIndexed(0L) { index, r, it -> r + (it.toLong() * (index.toLong() + 1L)) }
    }

    override fun toString(): String {
        return "Player $id: $deck"
    }
}

private val startingStates = getGroupedInputFile("day22.txt")

private val PlayerFactory = object {
    private val reg = Regex("Player (\\d+):")

    fun generatePlayers(states: List<String>): Pair<Player, Player> {
        var p1: Player? = null
        var p2: Player? = null

        for (state in states) {
            val splitted = state.split(lb)
            val id = reg.find(splitted[0])!!.groupValues[1].toInt()
            val cards = splitted.drop(1).map(String::toInt).toMutableList()

            if (id == 1) {
                p1 = Player(id, cards)
            } else {
                p2 = Player(id, cards)
            }
        }

        return p1!! to p2!!
    }
}

// Mutes the states of the players
// Returns the winner
private fun playGame(p1: Player, p2: Player): Player {
    while (p1.deck.isNotEmpty() && p2.deck.isNotEmpty()) {
        val lst = listOf(p1, p2)

        // There is no p1 == p2 case as all cards are unique.
        val win = if (p1.read() > p2.read()) {
            0
        } else {
            1
        }

        val opp = 1 - win

        lst[win].placeAtBottom(lst[win].draw())
        lst[win].placeAtBottom(lst[opp].draw())
    }

    return if (p1.deck.isNotEmpty()) {
        p1
    } else {
        p2
    }
}

private typealias History = MutableList<Pair<List<Int>, List<Int>>>

private fun playRecursiveGame(p1: Player, p2: Player): Player {
    val prevDecks: History = mutableListOf()

    while (p1.deck.isNotEmpty() && p2.deck.isNotEmpty()) {
        if ((p1.deck.toList() to p2.deck.toList()) in prevDecks) {
            return p1
        }

        prevDecks.add(p1.deck.toList() to p2.deck.toList())

        val lst = listOf(p1, p2)

        // There is no p1 == p2 case as all cards are unique.
        val p1r = p1.read()
        val p2r = p2.read()

        if (p1r <= p1.deck.size - 1 && p2r <= p2.deck.size - 1) {
            val win = playRecursiveGame(
                Player(p1.id, p1.deck.drop(1).take(p1r).toMutableList()),
                Player(p2.id, p2.deck.drop(1).take(p2r).toMutableList()),
            )

            lst[win.id - 1].placeAtBottom(lst[win.id - 1].draw())
            lst[win.id - 1].placeAtBottom(lst[1 - (win.id - 1)].draw())
        } else {
            val win = if (p1r > p2r) {
                0
            } else {
                1
            }

            val opp = 1 - win

            lst[win].placeAtBottom(lst[win].draw())
            lst[win].placeAtBottom(lst[opp].draw())
        }
    }

    return if (p1.deck.isNotEmpty()) {
        p1
    } else {
        p2
    }
}

fun main() {
    // Part 1
    val (part1p1, part1p2) = PlayerFactory.generatePlayers(startingStates)
    val winner1 = playGame(part1p1, part1p2)
    println("Player ${winner1.id} wins with ${winner1.getScore()} points!")

    // Test
    val wtest = playRecursiveGame(
        Player(1, mutableListOf(43, 19)),
        Player(2, mutableListOf(2, 29, 14)),
    )
    println("Test has ended with $wtest (${wtest.getScore()}) as the winner.")

    // Test 2
    val wtest2 = playRecursiveGame(
        Player(1, mutableListOf(9, 2, 6, 3, 1)),
        Player(2, mutableListOf(5, 8, 4, 7, 10)),
    )
    println("Test has ended with $wtest2 (${wtest2.getScore()}) as the winner.")

    // Part 2
    val (part2p1, part2p2) = PlayerFactory.generatePlayers(startingStates)
    val winner2 = playRecursiveGame(part2p1, part2p2)
    println("Player ${winner2.id} wins with ${winner2.getScore()} points!")
}
