package net.nergi.utils

import java.io.File
import java.nio.file.Paths

val lb: String = System.lineSeparator()
val sp: String = File.separator

// Designed for Windows, sadly:
private val workDir = Paths.get("").toAbsolutePath().toString()
private val inputDir = "$workDir${sp}inputs$sp"

fun getInputFile(name: String): List<String> {
    val file = File("${inputDir}$name")
    return file.readLines()
}

fun getRawInputFile(name: String): String {
    val file = File("${inputDir}$name")
    return file.readText()
}

fun getGroupedInputFile(name: String): List<String> {
    return getRawInputFile(name).split("$lb$lb")
}

fun <T> getProcessedInputFile(name: String, processor: (String) -> T): T {
    return processor(getRawInputFile(name))
}
