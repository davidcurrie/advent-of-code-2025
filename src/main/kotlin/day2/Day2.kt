package day2

import java.io.File

fun main() {
    Day2().let {
        println(it.part1())
        println(it.part2())
    }
}

class Day2 {

    val input = File("src/main/kotlin/day2/input.txt")
        .readLines()[0].split(",")
        .flatMap {
            val (start, end) = it.split("-")
            start.toLong()..end.toLong()
        }

    fun part1() = solve(::part1Invalid)

    fun part2() = solve(::part2Invalid)

    fun solve(invalid: (String) -> Boolean) =
        input.filter { invalid(it.toString()) }.sum()

    private fun part1Invalid(str: String) =
        str.length % 2 == 0 && str.take(str.length / 2) == str.substring(str.length / 2)

    private fun part2Invalid(str: String) =
        (1..(str.length / 2)).any { str.chunked(it).distinct().size == 1 }
}