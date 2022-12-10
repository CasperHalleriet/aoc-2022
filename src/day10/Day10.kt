package day10

import Day

fun main() {
    val day = Day10()
    day.runTest()
    day.run()
}

class Day10 : Day {
    override fun part1(input: List<String>): Int {
        val cycles = input.run()
        return cycles.getSignalStrength(20) +
                cycles.getSignalStrength(60) +
                cycles.getSignalStrength(100) +
                cycles.getSignalStrength(140) +
                cycles.getSignalStrength(180) +
                cycles.getSignalStrength(220)
    }

    override fun part2(input: List<String>): Int {
        input.render()
        return -1
    }

    private fun List<Int>.getSignalStrength(cycle: Int): Int {
        return getOrElse(cycle) { 1 } * cycle
    }

    private fun List<String>.run(): List<Int> {
        val clock = Clock()
        val instructions = this.asInstructionQueue()

        var curValue = 1
        val cycles = mutableListOf(curValue)

        clock.addInstructions(instructions)
        clock.run(onStartCycle = { _, _ ->
            cycles.add(curValue)
        }, finishInstruction = {
            curValue += (it as? Instruction.AddX)?.amount ?: 0
        })
        return cycles
    }

    private fun List<String>.render(): List<String> {
        val clock = Clock().apply {
            addInstructions(this@render.asInstructionQueue())
        }
        val rows = mutableListOf<String>()
        var curValue = 1
        var spritePosition = "###....................................."

        var crtRow = ""

        clock.run(
            duringCycle = { _, _ ->
                val lit = spritePosition.getOrNull(crtRow.length) == '#'
                crtRow += if (lit) "#" else "."
                if (crtRow.length == 40) {
                    rows.add(crtRow)
                    crtRow = ""
                }
            },
            finishInstruction = {
                curValue += (it as? Instruction.AddX)?.amount ?: 0
                spritePosition = "###".padStart(curValue + 2, '.').padEnd(40, '.')
            }
        )
        println(rows.joinToString("\n"))
        return listOf()
    }

    private fun List<String>.asInstructionQueue(): List<Instruction> {
        return buildList {
            this@asInstructionQueue.forEach { item ->
                val (exec, arg) = item.split(" ").run { this[0] to this.getOrNull(1)?.toInt() }
                when (exec) {
                    "addx" -> add(Instruction.AddX(arg!!))
                    "noop" -> add(Instruction.NoOp())
                }
            }
        }
    }
}

sealed class Instruction(cycleCount: Int) {
    var cycles: Int = cycleCount

    class AddX(val amount: Int) : Instruction(2) {
        override fun toString(): String {
            return "addx $amount"
        }
    }

    class NoOp : Instruction(1) {
        override fun toString(): String {
            return "noop"
        }
    }
}

class Clock {
    private var instructionList: MutableList<Instruction> = mutableListOf()
    private var cycle = 0

    fun run(
        onStartCycle: (Int, Instruction) -> Unit = { _, _ -> },
        duringCycle: (Int, Instruction) -> Unit = { _, _ -> },
        endCycle: (Int, Instruction) -> Unit = { _, _ -> },
        finishInstruction: (Instruction) -> Unit
    ) {
        var currentInstruction: Instruction? = null
        while (instructionList.isNotEmpty()) {
            if (currentInstruction == null) {
                currentInstruction = instructionList.removeFirst()
            }
            onStartCycle(cycle++, currentInstruction)
            duringCycle(cycle, currentInstruction)
            currentInstruction.cycles--
            val lastInstruction = currentInstruction
            if (currentInstruction.cycles == 0) {
                finishInstruction(currentInstruction)
                currentInstruction = null
            }
            endCycle(cycle, lastInstruction)
        }
    }

    fun addInstructions(instruction: List<Instruction>) {
        instructionList.addAll(instruction)
    }
}