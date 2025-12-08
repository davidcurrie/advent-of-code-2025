package day8

import java.io.File
import kotlin.math.sqrt

fun main() {
    Day8().let {
        println(it.part1())
        println(it.part2())
    }
}

data class Coord(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Coord): Double {
        val dx = (x - other.x).toDouble()
        val dy = (y - other.y).toDouble()
        val dz = (z - other.z).toDouble()
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}

fun List<Coord>.pairwiseDistances(): List<Triple<Coord, Coord, Double>> =
    flatMapIndexed { i, a ->
        drop(i + 1).map { b -> Triple(a, b, a.distanceTo(b)) }
    }

typealias Circuit = Set<Coord>

fun Set<Circuit>.join(a: Coord, b: Coord): Set<Circuit> {
    val circuitA = first { it.contains(a) }
    val circuitB = first { it.contains(b) }
    return this - setOf(circuitA) - setOf(circuitB) + setOf(circuitA + circuitB)
}

class Day8 {

    private val input = File("src/main/kotlin/day8/input.txt")
        .readLines()
        .map { it.split(",").map(String::toInt).let { (x, y, z) -> Coord(x, y, z) } }
    private val distances = input.pairwiseDistances().sortedBy { it.third }
    private val initialCircuits = input.map { setOf(it) }.toSet()

    fun part1() = distances
        .take(1000)
        .fold(initialCircuits) { circuits, (a, b, _) -> circuits.join(a, b) }
        .sortedByDescending { it.size }
        .take(3)
        .fold(1) { acc, circuit -> acc * circuit.size }

    fun part2(): Int =
        distances.fold(initialCircuits) { circuits, (a, b, _) ->
            circuits.join(a, b).also { if (it.size == 1) return a.x * b.x }
        }.let { throw IllegalStateException() }

}