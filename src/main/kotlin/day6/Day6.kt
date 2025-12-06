package day6

import java.io.File
import kotlin.collections.first

fun main() {
    Day6().let {
        println(it.part1())
        println(it.part2())
    }
}

class Day6 {

    val input = File("src/main/kotlin/day6/input.txt")
        .readLines()
    val separators = listOf(-1) +
            input.first().indices.filter { index -> input.all { it[index] == ' ' }} +
            listOf(input.first().length)
    val sums = input.map { separators.zipWithNext().map { (start, end) -> it.substring(start + 1, end) } }.let { rows ->
        rows.first().indices.map { index -> rows.map { row -> row[index] } }
    }.map { block -> block.last().trim() to block.dropLast(1) }

    fun part1() = sums.sumOf { (operation, rows) -> calculate(operation, rows.map { it.trim().toLong() }) }

    fun part2() = sums.sumOf { (operation, rows) ->
        val columns = rows.first().indices.map { i ->
            rows.map { it[i] }.joinToString(separator = "").trim().toLong()
        }
        calculate(operation, columns)
    }

    private fun calculate(operation: String, numbers: List<Long>): Long = when (operation) {
        "*" -> numbers.fold(1, Long::times)
        "+" -> numbers.sum()
        else -> throw IllegalStateException("Unknown operation: $operation")
    }

}