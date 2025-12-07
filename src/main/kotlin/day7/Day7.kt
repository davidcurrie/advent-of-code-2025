package day7

import java.io.File

fun main() {
    Day7().let {
        println(it.part1())
        println(it.part2())
    }
}

class Day7 {

    val input = File("src/main/kotlin/day7/input.txt")
        .readLines()
    val start = input[0].indexOf('S')

    fun part1() =
        input.drop(1).fold(Pair(0L, setOf(start))) { (splits, beams), line ->
            val newBeams = beams.flatMap { beam ->
                if (line[beam] == '^') listOf(beam - 1, beam + 1) else listOf(beam)
            }
            Pair(splits + newBeams.size - beams.size, newBeams.toSet())
        }.first

    fun part2(): Long {
        val memo = HashMap<Pair<Int, Int>, Long>()

        fun dfs(row: Int, pos: Int): Long {
            if (row == input.size) return 1L
            val key = row to pos
            memo[key]?.let { return it }

            val result = if (input[row][pos] == '^') {
                dfs(row + 1, pos - 1) + dfs(row + 1, pos + 1)
            } else {
                dfs(row + 1, pos)
            }

            memo[key] = result
            return result
        }

        return dfs(1, start)
    }

}