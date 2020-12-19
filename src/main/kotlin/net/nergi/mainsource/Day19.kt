package net.nergi.mainsource

import net.nergi.utils.getGroupedInputFile
import net.nergi.utils.lb

private val inputFile = getGroupedInputFile("day19.txt")

private val rules = inputFile[0].split(lb)

private val messages = inputFile[1].split(lb)

typealias RuleMap = MutableMap<Int, String>

private fun generateRuleMap(ruleSet: List<String> = rules): RuleMap {
    val mmap: RuleMap = mutableMapOf()

    for (rule in ruleSet) {
        val (num, content) = rule.split(": ")
        mmap[num.toInt()] = content
    }

    return mmap
}

private val mainRules = generateRuleMap()

/*
 * 0: 1 2
 * 1: "a"
 * 2: 1 3 | 3 1
 * 3: "b"
 * 
 * 1 2
 * a 2
 * a (1 3 | 3 1)
 * a (a b | b a)
 * aab | aba
 */

private fun combineStrLists(lists: List<List<String>>): List<String> {
    fun combineTwoStrLists(l1: List<String>, l2: List<String>): List<String> {
        val r = mutableListOf<String>()
        for (i in l1) {
            for (j in l2) {
                r.add(i + j)
            }
        }

        return r
    }

    return lists.dropLast(1).foldRight(lists[lists.size - 1]) { r, it -> combineTwoStrLists(r, it) }
}

private fun expandRule(rule: Int, ruleMap: RuleMap = mainRules): List<String> {
    // This function assumes that the rule number exists
    val selectedRule = ruleMap[rule]!!
    return if (selectedRule[0].toInt() == 34) {
        listOf("${selectedRule[1]}")
    } else {
        val subRules = selectedRule.split(" | ")
        val result = mutableListOf<String>()

        for (subRule in subRules) {
            val components = subRule.split(' ')
            val expansions = components.map { expandRule(it.toInt(), ruleMap) }

            combineStrLists(expansions).forEach { result.add(it) }
        }

        result
    }
}

fun main() {
    // Making sure parsing is correct
    println(rules[0])
    println(messages[0])

    // Test data
    val testRules = listOf(
        "0: 1 2",
        "1: \"a\"",
        "2: 1 3 | 3 1",
        "3: \"b\"",
    )

    val testRM = generateRuleMap(testRules)

    // More test data
    val testRules2 = listOf(
        "0: 4 1 5",
        "1: 2 3 | 3 2",
        "2: 4 4 | 5 5",
        "3: 4 5 | 5 4",
        "4: \"a\"",
        "5: \"b\"",
    )

    val testRM2 = generateRuleMap(testRules2)

    println(expandRule(0, testRM))
    println(expandRule(0, testRM2))

    // Part 1
    val r0canMatch = expandRule(0)
    println(messages.count { it in r0canMatch })

    // Part 2
    // First, we get the current rule 42 and 31
    val r42 = expandRule(42)
    val r31 = expandRule(31)
    
    // We also need to find the trace of rules that contain 8 or 11
    println(mainRules.filter { mv -> 
        val spl = mv.value.split(' ')
        "8" in spl || "11" in spl
    })

    // Only 0 has either of 8 and 11
    // Every rule's matches are all equal length
    // We can abuse this to our advantage:
    
    // Recursively matching 8
    fun countRule8Matches(str: String): Int {
        return if (str.length % r42[0].length != 0) {
            0
        } else {
            var res = 0
            for (i in 0 until str.length / r42[0].length) {
                if (str.subSequence(i * r42[0].length, (i + 1) * r42[0].length) in r42) {
                    res += 1
                } else {
                    break
                }
            }

            return res
        }
    }

    fun matchAny8(str: String): Boolean = countRule8Matches(str) > 0

    fun matchAny11(str: String): Boolean {
        val match8 = countRule8Matches(str)

        if (match8 <= 0) {
            return false
        }
        
        var cnt = 0

        val toMatch = str.drop(match8 * r42[0].length)
        for (i in 0 until toMatch.length / r31[0].length) {
            if (toMatch.subSequence(i * r31[0].length, (i + 1) * r31[0].length) in r31) {
                cnt += 1
            } else {
                return false
            }
        }

        return cnt > 0 && cnt < match8
    }

    println(messages.filter { matchAny8(it) && matchAny11(it) }.size)
}
