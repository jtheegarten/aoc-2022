package net.sheltem.aoc.y2024

import net.sheltem.common.*

suspend fun main() {
    Day06().run()
}

class Day06 : Day<Long>(41, 6) {

    override suspend fun part1(input: List<String>): Long = input
        .toGrid()
        .let {
            val start = it.find('^')
            it.visit(start)
                .first
                .size
                .toLong()
        }

    override suspend fun part2(input: List<String>): Long = input
        .toGrid()
        .let {
            val start = it.find('^')
            it.visit(start)
                .first
                .countParallel { obstruction ->
                    it.visit(start, obstruction).second
                }.toLong()
        }

    private fun Grid<Char>.visit(start: PositionInt, obstruction: PositionInt? = null): Pair<Set<PositionInt>, Boolean> {
        var direction = Direction.NORTH

        var pos = start

        val visited = mutableSetOf<Pair<PositionInt, Direction>>()
        var loop = false

        while (true) {

            if (visited.contains(pos to direction)) {
                loop = true
                break
            } else {
                visited.add(pos to direction)
            }

            val next = pos move direction
            when {
                !this.contains(next) -> break
                this[next] == '#' || next == obstruction -> direction = direction.turnRight()
                else -> {
                    pos = next
                }
            }
        }

        return visited.map { it.first }.toSet() to loop
    }
}
