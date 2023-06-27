package day12

import AoCGrid
import Coordinate
import Day
import cellsIndexed
import columnCount
import createGrid
import print
import rowCount
import surrounding
import kotlin.math.abs

fun main() {
    val day = Day12()
    day.runTest()
    day.run()
}

private val ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray()

class Day12 : Day {
    override fun part1(input: List<String>): Int {
        val grid = createGrid(input.size, input[0].length) {
            input[it.y][it.x]
        }
        grid.print(",")
        val p1 = Part1(grid)
        return p1.start()
//        return -1
    }

    override fun part2(input: List<String>): Int {
        return -1
    }
}

private class Part1(val grid: AoCGrid<Char>) {
    fun start(): Int {
        val (start, end) = findStartAndEnd()
        val results = visit(start, end, emptyList())
        return results ?: -1
    }

    fun visit(coordinate: Coordinate, endCoordinate: Coordinate, visited: List<Coordinate>): Int? {
//        println("Now at $coordinate. isEndcoordinate = ${coordinate == endCoordinate}, already visited: ${visited.joinToString()}")
        if (coordinate == endCoordinate) {
//            println("Found coordinate, visited: ${visited.joinToString { "(${it.x} ${it.y})" }}")
            return visited.size
        }
        val newlyVisited = visited + coordinate
        val surrounding = grid.surrounding(coordinate, seesCorners = false)
        val results = surrounding.map { neighbour ->
//            println("wanting to visit: $neighbour in grid: ${grid.columnCount()} vs ${grid.rowCount()}")
            if (!visited.contains(neighbour) && canVisit(grid.heightFor(coordinate), grid.heightFor(neighbour))) {
                visit(neighbour, endCoordinate, newlyVisited)
            } else {
                null
            }
        }
        val filtered = results.filterNotNull().minOrNull()
        return filtered
    }

    fun canVisit(yourHeight: Int, otherHeight: Int): Boolean {
        return abs(yourHeight - otherHeight) <= 1
    }

    private fun AoCGrid<Char>.heightFor(coordinate: Coordinate): Int {
        return when (val char = this[coordinate.y][coordinate.x]) {
            'S' -> 0
            'E' -> ALPHABET.size - 1
            else -> ALPHABET.indexOf(char)
        }
    }

    fun findStartAndEnd(): Pair<Coordinate, Coordinate> {
        var startCoordinate: Coordinate? = null
        var endCoordinate: Coordinate? = null
        grid.cellsIndexed { c, coordinate ->
            if (c == 'S') {
                startCoordinate = coordinate
            } else if (c == 'E') {
                endCoordinate = coordinate
            }
        }
        val start = startCoordinate ?: throw IllegalStateException("No start coordinate found")
        val end = endCoordinate ?: throw IllegalStateException("No end coordinate found")
        return Pair(start, end)
    }
}