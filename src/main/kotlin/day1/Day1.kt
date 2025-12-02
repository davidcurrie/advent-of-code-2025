package day1

import java.io.File

fun main() {
    Day1().let {
        println(it.part1())
        println(it.part2())
    }
}

class Day1 {

    data class Rotation(val direction: Int, val steps: Int)
    data class State(val position: Int, val result: Int) {
        fun rotate(rotation: Rotation) =
            ((position + (rotation.steps * rotation.direction) + 100) % 100).let { newPosition ->
                State(newPosition, result + if (newPosition == 0) 1 else 0)
            }
    }

    val input = File("src/main/kotlin/day1/input.txt")
        .readLines()
        .map { Rotation(if (it.first() == 'L') -1 else 1, it.drop(1).toInt()) }

    fun part1() = solve(input)

    fun part2() = solve(input.flatMap { rotation -> (1..rotation.steps).map { Rotation(rotation.direction, 1) } })

    private fun solve(rotations: List<Rotation>) =
        rotations.fold(State(50, 0)) { state, delta -> state.rotate(delta) }.result

}