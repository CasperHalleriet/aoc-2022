package day11

import Day

fun main() {
    val day = Day11()
    day.runTest()
    day.run()
}

class Day11 : Day {
    override fun part1(input: List<String>): Int {
        val monkeys = input.toMonkeys()
        val monkeysByName = monkeys.associateBy { it.name }
        repeat(20) {
            monkeys.forEach { monkey ->
                monkey.inspect(3) { receivingMonkey, value ->
                    monkeysByName[receivingMonkey]?.catch(value)
                }
            }
        }
        val sorted = monkeys.sortedByDescending { it.getInspections() }
        val result = sorted[0].getInspections().toLong() * sorted[1].getInspections().toLong()
        println(result)
        return 0
    }

    override fun part2(input: List<String>): Int {
        val monkeys = input.toMonkeys()
        val monkeysByName = monkeys.associateBy { it.name }
        repeat(10000) {
            monkeys.forEach { monkey ->
                monkey.inspect(1) { receivingMonkey, value ->
                    monkeysByName[receivingMonkey]?.catch(value)
                }
            }
            val round = it+1
            if((round % 1000) == 0 || round == 1 || round == 20) {
                println("After round ${it + 1}")
                monkeys.forEach {
                    println("${it.name}: inspected ${it.getInspections()} items")
                }
                println()
            }
        }
        val sorted = monkeys.sortedByDescending { it.getInspections() }
        val result = sorted[0].getInspections().toLong() * sorted[1].getInspections().toLong()
        println(result)
        return 0
    }
}

private fun List<String>.toMonkeys(): List<Monkey> {
    val monkeys = mutableListOf<Monkey>()
    val monkeyData = mutableListOf<String>()
    this.forEach {
        if (it.isBlank()) {
            monkeys.add(Monkey.fromInput(monkeyData))
            monkeyData.clear()
        } else {
            monkeyData.add(it)
        }
    }
    monkeys.add(Monkey.fromInput(monkeyData))
    return monkeys
}

class Monkey private constructor(
    val name: String,
    items: List<Long>,
    private val increateWorryLevel: Operation,
    private val divisableBy: Int,
    private val ifTrueThrowsTo: String,
    private val ifFalseThrowsTo: String
) {
    private val mutableItems = items.toMutableList()
    private var inspections: Int = 0
    fun catch(int: Long) {
        mutableItems.add(int)
    }

    fun inspect(worryLevelDivider: Int, throwTo: (String, Long) -> Unit) {
        while (mutableItems.isNotEmpty()) {
            inspections++
            var worryLevel = mutableItems.removeFirst()
            worryLevel = increateWorryLevel.execute(worryLevel)
            worryLevel /= worryLevelDivider
            if (worryLevel % divisableBy == 0L) {
                throwTo(ifTrueThrowsTo, worryLevel)
            } else {
                throwTo(ifFalseThrowsTo, worryLevel)
            }
        }
    }

    fun getInspections(): Int {
        return inspections
    }

    fun getItems(): List<Long> {
        return mutableItems
    }

    companion object {
        fun fromInput(input: List<String>): Monkey {
            val name = input[0].replace(":", "").lowercase()
            val startingItems = input[1].split(": ")[1].split(", ").map { it.toLong() }
            val operation = Operation.fromString(input[2].split(": ")[1].split("= ")[1])

            val divisable = input[3].split("by ")[1].toInt()
            val ifTrue = input[4].split("throw to ")[1]
            val ifFalse = input[5].split("throw to ")[1]
//
//            println("Monkey name: $name")
//            println("Items: $startingItems")
//            println("Operation: $operation")
//            println("Checks for divisable by: $divisable")
//            println("ifTrue throws to: $ifTrue")
//            println("ifFalse throws to: $ifFalse")
            return Monkey(name, startingItems, operation, divisable, ifTrue, ifFalse)
        }
    }
}

class Operation private constructor(
    private val item1: Value,
    private val operation: String,
    private val item2: Value
) {
    fun execute(oldValue: Long): Long {
        val value1 = when (item1) {
            is Value.Old -> oldValue
            is Value.Number -> item1.value
        }
        val value2 = when (item2) {
            is Value.Old -> oldValue
            is Value.Number -> item2.value
        }
        return when (operation) {
            "+" -> value1 + value2
            "*" -> value1 * value2
            "-" -> value1 - value2
            "/" -> value1 / value2
            else -> oldValue
        }
    }

    override fun toString(): String {
        return "$item1 $operation $item2"
    }

    sealed interface Value {
        class Number(val value: Long) : Value {
            override fun toString(): String {
                return "$value"
            }
        }

        object Old : Value {
            override fun toString(): String {
                return "old"
            }
        }
    }

    companion object {
        fun fromString(string: String): Operation {
            val items = string.split(" ")
            val item1 = if (items[0] == "old") Value.Old else Value.Number(items[0].toLong())
            val item2 = if (items[2] == "old") Value.Old else Value.Number(items[2].toLong())
            val operation = items[1]
            return Operation(item1, operation, item2)
        }
    }
}