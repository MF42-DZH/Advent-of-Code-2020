package net.nergi.mainsource

import net.nergi.utils.getInputFile
import net.nergi.utils.test

fun main() {
    val lines = getInputFile("day1.txt").map { it.toInt() }

    byLoops(lines)
    byTreeTraversal(lines)

    println(fpair())
    test(::fpair, 256)
    println(ftriple())
    test(::ftriple, 256)
}

fun byLoops(lines: List<Int>) {
    // Find pair of values that add to 2020, then find their product
    for (i in lines.indices) {
        for (j in i until lines.size) {
            if (lines[i] + lines[j] == 2020) {
                println("${lines[i]} * ${lines[j]} = ${lines[i] * lines[j]}")
            }
        }
    }

    // Find triplet of values that add to 2020, then find their product
    for (i in lines.indices) {
        for (j in i + 1 until lines.size) {
            for (k in j + 1 until lines.size) {
                if (lines[i] + lines[j] + lines[k] == 2020) {
                    println("${lines[i]} * ${lines[j]} * ${lines[k]} = ${lines[i] * lines[j] * lines[k]}")
                }
            }
        }
    }
}

private val linesPreloaded = getInputFile("day1.txt").map { it.toInt() }
private val linesSet = linesPreloaded.toHashSet()

private fun fpair(): Int {
    // for (i in linesPreloaded.indices) {
    //     for (j in i until linesPreloaded.size) {
    //         if (linesPreloaded[i] + linesPreloaded[j] == 2020) {
    //             return linesPreloaded[i] * linesPreloaded[j]
    //         }
    //     }
    // }
    for (i in linesSet) {
        if (linesSet.contains(2020 - i)) {
            return i * (2020 - i)
        }
    }

    return 0
}

private fun ftriple(): Int {
    // for (i in linesPreloaded.indices) {
    //     for (j in i + 1 until linesPreloaded.size) {
    //         for (k in j + 1 until linesPreloaded.size) {
    //             if (linesPreloaded[i] + linesPreloaded[j] + linesPreloaded[k] == 2020) {
    //                 return linesPreloaded[i] + linesPreloaded[j] + linesPreloaded[k]
    //             }
    //         }
    //     }
    // }
    for (i in linesSet) {
        for (j in linesSet) {
            if (linesSet.contains(2020 - i - j)) {
                return i * j * (2020 - i - j)
            }
        }
    }

    return 0
}

data class Tree<T>(val value: T, var left: Tree<T>? = null, var right: Tree<T>? = null)

fun byTreeTraversal(lines: List<Int>) {
    val tree = buildSortedIntTree(lines)
    println(tree.value)
}

fun buildSortedIntTree(ints: List<Int>): Tree<Int> {
    val root = Tree(ints[0])
    for (int in ints.drop(1)) {
        // Navigate and build as we go along
        var currentNode: Tree<Int>? = root
        while (true) {
            if (int <= currentNode!!.value) {
                // GO LEFT
                if (currentNode.left == null) {
                    currentNode.left = Tree(int)
                    break
                } else {
                    currentNode = currentNode.left
                }
            } else {
                // GO RIGHT
                if (currentNode.right == null) {
                    currentNode.right = Tree(int)
                    break
                } else {
                    currentNode = currentNode.right
                }
            }
        }
    }

    return root
}
