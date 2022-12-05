package day05

import Day

fun main() {
    val day = Day05()
    day.runTest()
    day.run()
}

/**
The expedition can depart as soon as the final supplies have been unloaded from the ships. Supplies are stored in stacks of marked crates, but because the needed supplies are buried under many other crates, the crates need to be rearranged.

The ship has a giant cargo crane capable of moving crates between stacks. To ensure none of the crates get crushed or fall over, the crane operator will rearrange them in a series of carefully-planned steps. After the crates are rearranged, the desired crates will be at the top of each stack.

The Elves don't want to interrupt the crane operator during this delicate procedure, but they forgot to ask her which crate will end up where, and they want to be ready to unload them as soon as possible so they can embark.

They do, however, have a drawing of the starting stacks of crates and the rearrangement procedure (your puzzle input). For example:

[D]
[N] [C]
[Z] [M] [P]
1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
In this example, there are three stacks of crates. Stack 1 contains two crates: crate Z is on the bottom, and crate N is on top. Stack 2 contains three crates; from bottom to top, they are crates M, C, and D. Finally, stack 3 contains a single crate, P.

Then, the rearrangement procedure is given. In each step of the procedure, a quantity of crates is moved from one stack to a different stack. In the first step of the above rearrangement procedure, one crate is moved from stack 2 to stack 1, resulting in this configuration:

[D]
[N] [C]
[Z] [M] [P]
1   2   3
In the second step, three crates are moved from stack 1 to stack 3. Crates are moved one at a time, so the first crate to be moved (D) ends up below the second and third crates:

[Z]
[N]
[C] [D]
[M] [P]
1   2   3
Then, both crates are moved from stack 2 to stack 1. Again, because crates are moved one at a time, crate C ends up below crate M:

[Z]
[N]
[M]     [D]
[C]     [P]
1   2   3
Finally, one crate is moved from stack 1 to stack 2:

[Z]
[N]
[D]
[C] [M] [P]
1   2   3
The Elves just need to know which crate will end up on top of each stack; in this example, the top crates are C in stack 1, M in stack 2, and Z in stack 3, so you should combine these together and give the Elves the message CMZ.

After the rearrangement procedure completes, what crate ends up on top of each stack?


 */
class Day05 : Day {
    override fun part1(input: List<String>): Int {
        val crateMover9000 = CrateMover9000()
        val separatorIndex = input.indexOf("")
        val instructions = input.subList(separatorIndex + 1, input.size).map { Instruction.create(it) }
        val stacks = input.subList(0, separatorIndex - 1)
        crateMover9000.parseInput(stacks)
        instructions.forEach {
            crateMover9000.executeInstruction(it)
        }
        crateMover9000.printOutput()
        return -1
    }

    override fun part2(input: List<String>): Int {
        val crateMover9000 = CrateMover9001()
        val separatorIndex = input.indexOf("")
        val instructions = input.subList(separatorIndex + 1, input.size).map { Instruction.create(it) }
        val stacks = input.subList(0, separatorIndex - 1)
        crateMover9000.parseInput(stacks)
        instructions.forEach {
            crateMover9000.executeInstruction(it)
        }
        crateMover9000.printOutput()
        return -1
    }
}

abstract class CrateMover {
    protected val stacks: MutableList<MutableList<Char>> = mutableListOf()

    abstract fun executeInstruction(instruction: Instruction)

    fun parseInput(input: List<String>) {
        input.reversed().forEach { row ->
            val chars = row.chunked(4).map { it[1] }
            chars.forEachIndexed { index, item ->
                val list = stacks.getOrNull(index) ?: run {
                    val newList = mutableListOf<Char>()
                    stacks.add(newList)
                    newList
                }
                if (item.isLetter()) {
                    list.add(item)
                }
            }
        }
    }

    fun printOutput() {
        println(stacks.map { it.last() }.joinToString(""))
    }
}

class Instruction private constructor(
    val move: Int,
    val from: Int,
    val to: Int
) {
    companion object {
        fun create(string: String): Instruction {
            val items = string.split(" ")
            val move = items[1].toInt()
            val from = items[3].toInt()
            val to = items[5].toInt()
            return Instruction(move, from, to)
        }
    }

    override fun toString(): String {
        return "move $move from $from to $to"
    }
}

class CrateMover9000 : CrateMover() {
    override fun executeInstruction(instruction: Instruction) {
        val from = stacks[instruction.from - 1]
        val to = stacks[instruction.to - 1]

    }
}

class CrateMover9001: CrateMover() {
    override fun executeInstruction(instruction: Instruction) {
        val from = stacks[instruction.from - 1]
        val to = stacks[instruction.to - 1]

        val moveables = mutableListOf<Char>()
        for (amount in 0 until instruction.move) {
            val char = from.removeLastOrNull()
            if (char != null) {
                moveables.add(0, char)
            }
        }
        to.addAll(moveables)
        println(moveables)
    }

}

