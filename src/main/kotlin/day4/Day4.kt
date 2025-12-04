package day4

import java.io.File

fun main() {
    Day4().let {
        println(it.part1())
        println(it.part2())
    }
}

data class Coordinate(val row: Int, val column: Int) {
    fun neighbours() =
        (-1..1).flatMap { dr ->
            (-1..1).map { dc -> Coordinate(row + dr, column + dc) }
        }.filter { it != this }
}

class Day4 {

    val input = File("src/main/kotlin/day4/input.txt")
        .readLines()
        .withIndex()
        .flatMap { (row, line) ->
            line.withIndex().mapNotNull { (column, char) -> if (char == '@') Coordinate(row, column) else null }
        }
        .toSet()

    fun part1() = input.removable().size

    fun part2() =
        generateSequence(input) { rolls ->
            val removable = rolls.removable()
            if (removable.isEmpty()) null else rolls - removable
        }.last().let { final ->
            input.size - final.size
        }

    private fun Set<Coordinate>.removable() = filter { roll ->
        roll.neighbours().intersect(this).size < 4
    }.toSet()

}
