package day12

import java.io.File

fun main() {
    println(Day12().part1())
}

class Day12 {

    companion object {
        val treeLine = Regex("""^(\d+)x(\d+):\s*(.*)$""")
    }

    val blocks = File("src/main/kotlin/day12/input.txt").readText().split("\n\n")

    val presents = blocks.dropLast(1).map { block ->
        val shape = block.lines().mapIndexed { y, line ->
            line.mapIndexedNotNull { x, ch -> if (ch == '#') Pair(x, y) else null }
        }.flatten().toSet()
        Present(shape)
    }

    val trees = blocks.last().lines().map { line ->
        val match = treeLine.matchEntire(line.trim())!!
        Tree(
            width = match.groupValues[1].toInt(),
            height = match.groupValues[2].toInt(),
            counts = match.groupValues[3].trim().split(Regex("""\s+""")).map { it.toInt() }
        )
    }

    fun part1(): Int {
        return trees.count { it.canFit(presents) }
    }

}

typealias Coord = Pair<Int, Int>
typealias Shape = Set<Coord>

fun Shape.bounds(): Pair<Int, Int> {
    val maxX = maxOf { it.first }
    val maxY = maxOf { it.second }
    return Pair(maxX + 1, maxY + 1)
}

data class Present(val shape: Shape) {
    fun variants(): Set<Shape> {
        fun normalize(shape: Shape): Shape {
            val minX = shape.minOf { it.first }
            val minY = shape.minOf { it.second }
            return shape.map { Pair(it.first - minX, it.second - minY) }.toSet()
        }

        fun rotateOnce(p: Coord): Coord = Coord(-p.second, p.first)
        fun rotate(coords: Shape, times: Int): Shape {
            var cur = coords
            repeat(times % 4) { cur = cur.map { rotateOnce(it) }.toSet() }
            return cur
        }

        val variants = mutableSetOf<Shape>()
        for (flip in 0..1) {
            val flipped = if (flip == 1) shape.map { Coord(-it.first, it.second) }.toSet() else shape
            for (r in 0..3) {
                val transformed = rotate(flipped, r)
                variants.add(normalize(transformed))
            }
        }
        return variants
    }
}

data class Tree(val width: Int, val height: Int, val counts: List<Int>) {

    fun canFit(presents: List<Present>): Boolean {
        if (counts.size > presents.size) throw IllegalStateException("Too many present types")

        val totalNeeded = counts.sum()
        if (totalNeeded == 0) return true

        val areaNeeded = counts.withIndex().sumOf { (i, count) -> count * presents[i].shape.size }
        if (areaNeeded > width * height) return false

        // precompute all possible placements for each present type - a placement is represented as a list of occupied cell indices
        val placements = mutableListOf<IntArray>()
        val typeToPlacementIndices = Array(presents.size) { mutableListOf<Int>() }

        presents.forEachIndexed { type, present ->
            val need = counts.get(type)
            if (need == 0) return@forEachIndexed
            val seen = HashSet<String>() // dedupe identical placements by canonical key of sorted cells
            for (variant in present.variants()) {
                val (variantWidth, variantHeight) = variant.bounds()
                if (variantWidth > width || variantHeight > height) continue
                for (xOffset in 0..(width - variantWidth)) {
                    for (yOffset in 0..(height - variantHeight)) {
                        val cellsList = IntArray(variant.size)
                        var idx = 0
                        var fits = true
                        for ((vx, vy) in variant) {
                            val gx = xOffset + vx
                            val gy = yOffset + vy
                            if (gx < 0 || gy < 0 || gx >= width || gy >= height) { fits = false; break }
                            cellsList[idx++] = gy * width + gx
                        }
                        if (!fits) continue
                        // canonical key
                        val sorted = cellsList.sortedArray()
                        val key = sorted.joinToString(",")
                        if (seen.add(key)) {
                            val placementId = placements.size
                            placements.add(sorted)
                            typeToPlacementIndices[type].add(placementId)
                        }
                    }
                }
            }
            // quick prune: if not enough distinct placements to satisfy the required count, impossible
            if (typeToPlacementIndices[type].size < need) return false
        }

        // occupancy and used placements
        val occupied = BooleanArray(width * height)
        val usedPlacement = BooleanArray(placements.size)
        val remaining = counts.toIntArray()

        // helper to compute available placements for a type given the current occupancy
        fun availablePlacementsForType(type: Int): List<Int> {
            val result = mutableListOf<Int>()
            for (placementId in typeToPlacementIndices.getOrElse(type) { mutableListOf() }) {
                if (usedPlacement[placementId]) continue
                if (placements[placementId].none { occupied[it] }) result.add(placementId)
            }
            return result
        }

        // choose next type with remaining > 0 and minimal available placements
        fun chooseType(): Int {
            var bestType = -1
            var bestCount = Int.MAX_VALUE
            for (type in presents.indices) {
                val need = remaining.get(type)
                if (need == 0) continue
                val available = availablePlacementsForType(type).size
                if (available == 0) return type // immediate fail spot
                if (available < bestCount) {
                    bestCount = available
                    bestType = type
                }
            }
            return bestType
        }

        // recursive search
        fun dfs(placedSoFar: Int): Boolean {
            if (placedSoFar == totalNeeded) return true

            val type = chooseType()
            val availablePlacements = availablePlacementsForType(type)
            if (availablePlacements.size < remaining[type]) return false

            // try placements for this type
            for (placementId in availablePlacements) {
                val cells = placements[placementId]
                // place
                usedPlacement[placementId] = true
                for (c in cells) occupied[c] = true
                remaining[type] = remaining[type] - 1

                if (dfs(placedSoFar + 1)) return true

                // backtrack
                remaining[type] = remaining[type] + 1
                for (c in cells) occupied[c] = false
                usedPlacement[placementId] = false
            }
            return false
        }

        return dfs(0)
    }
}
