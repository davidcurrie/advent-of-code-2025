package day3

import java.io.File

fun main() {
    Day3().let {
        println(it.part1())
        println(it.part2())
    }
}

typealias Bank = List<Int>

class Day3 {

    val input = File("src/main/kotlin/day3/input.txt")
        .readLines()
        .map { line -> line.map { it.digitToInt() } }

    fun part1() = joltage(2)

    fun part2() = joltage(12)

    private fun joltage(length: Int) = input.sumOf { it.joltage(length) }

    private fun Bank.joltage(length: Int) =
        (1..length).fold(this to 0L) { (remainder, result), index ->
            val maxDigit = remainder.dropLast(length - index).withIndex().maxBy { it.value }
            remainder.drop(maxDigit.index + 1) to (result * 10) + maxDigit.value
        }.second

}