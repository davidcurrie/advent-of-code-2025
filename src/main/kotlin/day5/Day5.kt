package day5

import java.io.File

fun main() {
    Day5().let {
        println(it.part1())
        println(it.part2())
    }
}

data class Range(val start: Long, val end: Long)

class Day5 {

    val ranges: List<Range>
    val ingredients: List<Long>

    init {
        val (a, b) = File("src/main/kotlin/day5/input.txt").readText().split("\n\n")
        ranges = a.lines().map { line ->
            line.split('-').let { (s, e) -> Range(s.toLong(), e.toLong()) }
        }
        ingredients = b.lines().map(String::toLong)
    }

    fun part1() =
        ingredients.count { ingredient ->
            ranges.any { range -> ingredient in range.start..range.end }
        }

    fun part2() =
        ranges.sortedBy { it.start }.let { sorted ->
            sorted.drop(1).fold(listOf(sorted.first())) { merged, range ->
                val last = merged.last()
                if (range.start <= last.end) merged.dropLast(1) + Range(last.start, maxOf(last.end, range.end))
                else merged + range
            }.sumOf { it.end - it.start + 1 }
        }

}