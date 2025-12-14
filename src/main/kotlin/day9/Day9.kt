package day9

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day9().let {
        println(it.part1())
        println(it.part2())
    }
}

data class Coord(val x: Int, val y: Int)

data class Line(val a: Coord, val b: Coord) {
    val leftTop = Coord(min(a.x, b.x), min(a.y, b.y))
    val rightBottom = Coord(max(a.x, b.x), max(a.y, b.y))
}

data class Area(val a: Coord, val b: Coord) {
    val leftTop = Coord(min(a.x, b.x), min(a.y, b.y))
    val rightBottom = Coord(max(a.x, b.x), max(a.y, b.y))
    fun size() = (abs(a.x - b.x) + 1L) * (abs(a.y - b.y) + 1L)
    fun intersects(line: Line) =
        line.leftTop.x < rightBottom.x && line.leftTop.y < rightBottom.y && line.rightBottom.x > leftTop.x && line.rightBottom.y > leftTop.y
}

fun List<Coord>.pairwiseAreas(): List<Area> = flatMapIndexed { i, a -> drop(i + 1).map { b -> Area(a, b) } }

data class Polygon(val points: List<Coord>) {
    private val edges = (points + points[0]).zipWithNext().map { (a, b) -> Line(a, b) }
    fun intersects(area: Area) = edges.any { area.intersects(it) }
}

class Day9 {
    private val input = File("src/main/kotlin/day9/input.txt")
        .readLines()
        .map { it.split(",").map(String::toInt).let { (x, y) -> Coord(x, y) } }
    private val areas = input.pairwiseAreas().sortedByDescending { it.size() }

    fun part1() = areas.first().size()
    fun part2() = areas.first { area -> !Polygon(input).intersects(area) }.size()
}