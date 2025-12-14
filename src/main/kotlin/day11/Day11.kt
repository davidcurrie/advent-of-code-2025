package day11

import java.io.File

fun main() {
    Day11().let {
        println(it.part1())
        println(it.part2())
    }
}

class Day11 {

    private val input = File("src/main/kotlin/day11/input.txt")
        .readLines().associate {
            val (from, to) = it.split(":")
            from to to.trim().split(" ")
        }

    fun part1() = paths("you", "out")

    fun paths(from: String, to: String): Int {
        val stack = ArrayDeque<List<String>>()
        stack.addLast(listOf(from))
        var count = 0
        while (stack.isNotEmpty()) {
            val path = stack.removeLast()
            val current = path.last()
            if (current == to) {
                count++
            } else {
                val neighbors = input[current] ?: emptyList()
                for (neighbor in neighbors) {
                    if (neighbor !in path) {
                        stack.addLast(path + neighbor)
                    }
                }
            }
        }
        return count
    }

    fun part2(): Long {
        // Array tracks count of paths to 'out' without 'fft' or 'dac', with 'fft', with 'dac', and with both
        val counts = mutableMapOf<String, LongArray>()

        for (node in topologySort().asReversed()) {
            val array = LongArray(4)
            val nodeBit = when (node) {
                "fft" -> 1
                "dac" -> 2
                else -> 0
            }
            if (node == "out") {
                array[nodeBit] = 1L
                counts[node] = array
                continue
            }
            val neighbours = input[node] ?: emptyList()
            for (neighbour in neighbours) {
                val neighbourArray = counts[neighbour] ?: LongArray(4)
                for (mask in 0..3) {
                    array[mask or nodeBit] += neighbourArray[mask]
                }
            }
            counts[node] = array
        }

        return counts["svr"]?.get(3) ?: throw IllegalStateException("No solution found")
    }

    private fun topologySort(): List<String> {
        val allNodes = input.keys + input.values.flatten()
        val references = allNodes.associateWith { node -> input.values.flatten().count { it == node } }.toMutableMap()

        val queue = ArrayDeque<String>()
        queue.addAll(references.filter { (_, deg) -> deg == 0 }.keys)

        val order = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val head = queue.removeFirst()
            order.add(head)
            for (v in input[head] ?: emptyList()) {
                references[v] = references.getValue(v) - 1
                if (references.getValue(v) == 0) queue.addLast(v)
            }
        }
        return order
    }

}