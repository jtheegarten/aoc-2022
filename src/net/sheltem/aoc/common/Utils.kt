package net.sheltem.aoc.common

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

typealias PositionInt = Pair<Int, Int>
typealias Position = Pair<Long, Long>

const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"

val numericRegex = Regex("\\d+")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun String.lastAsInt(delimiter: String) = this.split(delimiter).last().toInt()

suspend inline fun <T, R> Iterable<T>.mapParallel(crossinline transform: (T) -> R): List<R> = coroutineScope {
    map { async { transform(it) } }.map { it.await() }
}

enum class MathOperation(val sign: String) {
    ADD("+"),
    MULTIPLY("*"),
    SUBSTRACT("-"),
    DIVIDE("/");

    fun opposite() =
        when (this) {
            ADD -> SUBSTRACT
            MULTIPLY -> DIVIDE
            SUBSTRACT -> ADD
            DIVIDE -> MULTIPLY
        }

    companion object {
        fun fromSign(value: String) = MathOperation.values().first { it.sign == value }
    }
}

enum class Direction(val coords: PositionInt) {
    NORTH(0 to -1),
    EAST(1 to 0),
    SOUTH(0 to 1),
    WEST(-1 to 0),
    NEUTRAL(0 to 0);

    fun turnRight() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
        else -> NORTH
    }
    fun turnLeft() = when (this) {
        NORTH -> WEST
        EAST -> NORTH
        SOUTH -> EAST
        WEST -> SOUTH
        else -> NORTH
    }
}

enum class Direction8(val coords: PositionInt) {
    NORTH(0 to -1),
    NORTH_EAST(1 to -1),
    EAST(1 to 0),
    SOUTH_EAST(1 to 1),
    SOUTH(0 to 1),
    SOUTH_WEST(-1 to 1),
    WEST(-1 to 0),
    NORTH_WEST(-1 to -1),
    NEUTRAL(0 to 0);

    val longCoords = coords.first.toLong() to coords.second.toLong()

    companion object {
        fun cardinalValues() = listOf(NORTH, EAST, SOUTH, WEST)

        fun fromCaret(caret: Char): Direction8 {
            return when (caret) {
                '>' -> EAST
                '^' -> NORTH
                '<' -> WEST
                'v' -> SOUTH
                else -> error("Not a valid caret")
            }
        }
    }
}

fun Pair<Long, Long>.neighbours(other: Collection<Direction8>): List<Pair<Long, Long>> = other.map { it.coords.first + first to it.coords.second + second }
fun Pair<Long, Long>.neighbour(other: Direction8): Pair<Long, Long> = this.first + other.coords.first to this.second + other.coords.second
fun Collection<Pair<Long, Long>>.bounds() = (minOf { it.first } to minOf { it.second }) to (maxOf { it.first } to maxOf { it.second })

fun PositionInt.move(direction: Direction8) = first + direction.coords.first to second + direction.coords.second
fun PositionInt.move(direction: Direction, distance: Int = 1) = first + (direction.coords.first * distance) to second + (direction.coords.second * distance)

fun Collection<Long>.multiply(): Long = reduce { acc, l -> acc * l }

fun Collection<MatchResult>.toListLong(): List<Long> = mapNotNull { number -> number.value.toLong() }
fun Sequence<MatchResult>.toListLong(): List<Long> = toList().toListLong()


infix operator fun Pair<Long, Long>.plus(other: Pair<Long, Long>): Pair<Long, Long> = first + other.first to second + other.second
