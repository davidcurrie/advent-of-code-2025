package day10

import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import java.io.File

fun main() {
    Day10().let {
        println(it.part1())
        println(it.part2())
    }
}

data class Machine(
    val lights: List<Boolean>,
    val buttons: List<List<Int>>,
    val joltages: List<Int>
) {
    companion object {
        private val statesRegex = Regex("""\[(.*?)]""")
        private val buttonsRegex = Regex("""\((.*?)\)""")
        private val joltagesRegex = Regex("""\{(.*?)}""")
    }

    constructor(line: String): this(
        lights = statesRegex.find(line)?.groupValues[1]?.map { it == '#' }
            ?: throw IllegalArgumentException("Missing bracketed light states (e.g. [..#..])"),
        buttons = buttonsRegex.findAll(line)
            .map { it.groupValues[1].trim() }
            .map { content -> content.split(',').map { it.trim().toInt() } }
            .toList(),
        joltages = joltagesRegex.find(line)
            ?.groupValues?.get(1)
            ?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.map { it.toInt() }
            ?: emptyList()
    )

    fun minButtonPressesForLight(): Int {
        val queue = ArrayDeque<Pair<List<Boolean>, Int>>()
        queue.add(List(lights.size) { false } to 0)
        while (queue.isNotEmpty()) {
            val (state, presses) = queue.removeFirst()
            for (button in buttons) {
                val nextState = state.mapIndexed { i, b -> if (i in button) !b else b }
                if (nextState == lights) return presses + 1
                queue.add(nextState to presses + 1)
            }
        }
        throw IllegalStateException("No solution found")
    }

    fun minButtonPressesForJoltages(): Int = Context().use { ctx ->
        val solver = ctx.mkOptimize()
        val zero = ctx.mkInt(0)

        // Counts number of presses for each button, and ensures it is positive
        val buttonPresses = buttons.indices
            .map { ctx.mkIntConst("button#$it") }
            .onEach { button -> solver.Add(ctx.mkGe(button, zero)) }
            .toTypedArray()

        // For each joltage counter, require that the sum of presses of all buttons that increment it is equal to the
        // target value
        joltages.forEachIndexed { counter, targetValue ->
            val buttonsThatIncrement = buttons
                .withIndex()
                .filter { (_, counters) -> counter in counters }
                .map { buttonPresses[it.index] }
                .toTypedArray()
            val target = ctx.mkInt(targetValue)
            val sumOfPresses = ctx.mkAdd(*buttonsThatIncrement) as IntExpr
            solver.Add(ctx.mkEq(sumOfPresses, target))
        }

        // Describe that the presses is the sum of all individual button presses, and should be as low as possible
        val presses = ctx.mkIntConst("presses")
        solver.Add(ctx.mkEq(presses, ctx.mkAdd(*buttonPresses)))
        solver.MkMinimize(presses)

        // Solve
        if (solver.Check() != Status.SATISFIABLE) error("No solution found")
        solver.getModel().evaluate(presses, false).let { it as IntNum }.int
    }

}


class Day10 {
    private val input = File("src/main/kotlin/day10/input.txt")
        .readLines()
        .map { Machine(it) }

    fun part1() = input.sumOf { it.minButtonPressesForLight() }
    fun part2() = input.sumOf { it.minButtonPressesForJoltages() }
}