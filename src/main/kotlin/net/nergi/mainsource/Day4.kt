package net.nergi.mainsource

import net.nergi.utils.getRawInputFile
import net.nergi.utils.lb
import net.nergi.utils.test

private val passportStrings = getRawInputFile("day4.txt").split("$lb$lb")

private data class Passport(
    var byr: String? = null,
    var iyr: String? = null,
    var eyr: String? = null,
    var hgt: String? = null,
    var hcl: String? = null,
    var ecl: String? = null,
    var pid: String? = null,
    var cid: String? = null,
) {
    val isValid: Boolean
        get() {
            return byr != null &&
                iyr != null &&
                eyr != null &&
                hgt != null &&
                hcl != null &&
                ecl != null &&
                pid != null
        }

    val isValidPartTwo: Boolean
        get() {
            return (byr != null && byr!!.toInt() in 1920..2002) &&
                (iyr != null && iyr!!.toInt() in 2010..2020) &&
                (eyr != null && eyr!!.toInt() in 2020..2030) &&
                (hgt != null && heightVal(hgt!!)) &&
                (hcl != null && hairVal(hcl!!)) &&
                (ecl != null && ecl in listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")) &&
                (pid != null && pid!!.isNumber() && pid!!.length == 9)
        }

    private fun heightVal(h: String): Boolean {
        val height = h.dropLast(2)

        return when (h.takeLast(2)) {
            "cm" -> height.toInt() in 150..193
            "in" -> height.toInt() in 59..76
            else -> false
        }
    }

    private fun hairVal(h: String): Boolean {
        return h[0] == '#' && h.drop(1).all { it in "0123456789abcdef" }
    }

    private fun String.isNumber(): Boolean {
        return this.all { it.isDigit() }
    }
}

private fun pairise(pairString: String): Pair<String, String> {
    val (fs, sn) = pairString.split(":")
    return Pair(fs, sn)
}

private fun parseString(passString: String): Passport {
    val splitted = passString.split(" ", lb)
    val pairs = splitted.map(::pairise)
    val currentPassport = Passport()

    for ((k, v) in pairs) {
        when (k) {
            "byr" -> currentPassport.byr = v
            "iyr" -> currentPassport.iyr = v
            "eyr" -> currentPassport.eyr = v
            "hgt" -> currentPassport.hgt = v
            "hcl" -> currentPassport.hcl = v
            "ecl" -> currentPassport.ecl = v
            "pid" -> currentPassport.pid = v
            "cid" -> currentPassport.cid = v
        }
    }

    return currentPassport
}

fun main() {
    val passes = passportStrings.map(::parseString)
    println(passes.count { it.isValid })
    println(passes.count { it.isValidPartTwo })

    test({ passportStrings.map(::parseString).count { it.isValid } }, 256)
    test({ passportStrings.map(::parseString).count { it.isValidPartTwo } }, 256)
}
