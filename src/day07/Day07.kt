package day07

import Day

/*
--- Day 7: No Space Left On Device ---
You can hear birds chirping and raindrops hitting leaves as the expedition proceeds. Occasionally, you can even hear much louder sounds in the distance; how big do the animals get out here, anyway?

The device the Elves gave you has problems with more than just its communication system. You try to run a system update:

$ system-update --please --pretty-please-with-sugar-on-top
Error: No space left on device
Perhaps you can delete some files to make space for the update?

You browse around the filesystem to assess the situation and save the resulting terminal output (your puzzle input). For example:

$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
The filesystem consists of a tree of files (plain data) and directories (which can contain other directories or files). The outermost directory is called /. You can navigate around the filesystem, moving into or out of directories and listing the contents of the directory you're currently in.

Within the terminal output, lines that begin with $ are commands you executed, very much like some modern computers:

cd means change directory. This changes which directory is the current directory, but the specific result depends on the argument:
cd x moves in one level: it looks in the current directory for the directory named x and makes it the current directory.
cd .. moves out one level: it finds the directory that contains the current directory, then makes that directory the current directory.
cd / switches the current directory to the outermost directory, /.
ls means list. It prints out all of the files and directories immediately contained by the current directory:
123 abc means that the current directory contains a file named abc with size 123.
dir xyz means that the current directory contains a directory named xyz.
Given the commands and output in the example above, you can determine that the filesystem looks visually like this:

- / (dir)
  - a (dir)
    - e (dir)
      - i (file, size=584)
    - f (file, size=29116)
    - g (file, size=2557)
    - h.lst (file, size=62596)
  - b.txt (file, size=14848514)
  - c.dat (file, size=8504156)
  - d (dir)
    - j (file, size=4060174)
    - d.log (file, size=8033020)
    - d.ext (file, size=5626152)
    - k (file, size=7214296)
Here, there are four directories: / (the outermost directory), a and d (which are in /), and e (which is in a). These directories also contain files of various sizes.

Since the disk is full, your first step should probably be to find directories that are good candidates for deletion. To do this, you need to determine the total size of each directory. The total size of a directory is the sum of the sizes of the files it contains, directly or indirectly. (Directories themselves do not count as having any intrinsic size.)

The total sizes of the directories above can be found as follows:

The total size of directory e is 584 because it contains a single file i of size 584 and no other directories.
The directory a has total size 94853 because it contains files f (size 29116), g (size 2557), and h.lst (size 62596), plus file i indirectly (a contains e which contains i).
Directory d has total size 24933642.
As the outermost directory, / contains every file. Its total size is 48381165, the sum of the size of every file.
To begin, find all of the directories with a total size of at most 100000, then calculate the sum of their total sizes. In the example above, these directories are a and e; the sum of their total sizes is 95437 (94853 + 584). (As in this example, this process can count files more than once!)

Find all of the directories with a total size of at most 100000. What is the sum of the total sizes of those directories?

--- Part Two ---
Now, you're ready to choose a directory to delete.

The total disk space available to the filesystem is 70000000. To run the update, you need unused space of at least 30000000. You need to find a directory you can delete that will free up enough space to run the update.

In the example above, the total size of the outermost directory (and thus the total amount of used space) is 48381165; this means that the size of the unused space must currently be 21618835, which isn't quite the 30000000 required by the update. Therefore, the update still requires a directory with total size of at least 8381165 to be deleted before it can run.

To achieve this, you have the following options:

Delete directory e, which would increase unused space by 584.
Delete directory a, which would increase unused space by 94853.
Delete directory d, which would increase unused space by 24933642.
Delete directory /, which would increase unused space by 48381165.
Directories e and a are both too small; deleting them would not free up enough space. However, directories d and / are both big enough! Between these, choose the smallest: d, increasing unused space by 24933642.

Find the smallest directory that, if deleted, would free up enough space on the filesystem to run the update. What is the total size of that directory?
 */
fun main() {
    val day = Day07()
    day.runTest()
    day.run()
}

class Day07 : Day {
    override fun part1(input: List<String>): Int {
        val files = createFileStructure(input)
        val dirs = files.findChildDirsRecursive()
        return dirs.sumOf { if(it.size() < 100000) it.size() else 0 }
    }

    override fun part2(input: List<String>): Int {
        val totalStorage = 70000000
        val neededStorage = 30000000
        val files = createFileStructure(input)
        val remainingStorage = totalStorage - files.size()
        val storageToClear = neededStorage - remainingStorage

        val dirs = files.findChildDirsRecursive()
        var dirToDelete: File.Dir? = null
        dirs.forEach { dir ->
            if(dir.size() > storageToClear && dir.size() < (dirToDelete?.size() ?: Int.MAX_VALUE)) {
                dirToDelete = dir
            }
        }
        println("Should delete dir: ${dirToDelete?.name}")
        return dirToDelete?.size() ?: -1
    }

    private fun createFileStructure(input: List<String>): File.Dir {
        val root = File.Dir("/")
        var curDir: File.Dir? = null
        val commands = Command.fromInput(input)
        commands.forEach { command ->
            when (command) {
                is Command.CD.Into -> {
                    curDir?.openDirectory(command.into)?.also {
                        curDir = it
                    }
                }

                Command.CD.Out -> {
                    curDir?.close()?.also {
                        curDir = it
                    }
                }

                is Command.LS -> {
                    command.getOutput().forEach { output ->
                        when (output) {
                            is Command.LS.Output.Dir -> {
                                curDir?.addDirectory(output.name)
                            }

                            is Command.LS.Output.File -> {
                                curDir?.addDataFile(output.name, output.size)
                            }
                        }
                    }
                }

                Command.CD.Root -> {
                    curDir = root
                }
            }
        }
        return root
    }
}

sealed interface Command {
    sealed interface CD : Command {
        class Into(val into: String) : CD
        object Out : CD
        object Root : CD
    }

    class LS : Command {
        private val outputs = mutableListOf<Output>()

        fun addOutput(output: Output) {
            outputs.add(output)
        }

        fun getOutput(): List<Output> {
            return outputs
        }

        sealed interface Output {
            class File(val size: Int, val name: String) : Output
            class Dir(val name: String) : Output
        }
    }

    companion object {
        fun fromInput(input: List<String>): List<Command> {
            val commands = mutableListOf<Command>()
            input.forEach {
                if (it.startsWith("$")) {
                    val args = it.split(" ")
                    if (args[1] == "cd") {
                        val command = when (val string = args[2]) {
                            ".." -> CD.Out
                            "/" -> CD.Root
                            else -> CD.Into(string)
                        }
                        commands.add(command)
                    } else if (args[1] == "ls") {
                        commands.add(LS())
                    }
                } else {
                    val output = it.split(" ")
                    val parsedOutput: LS.Output = if (output[0].toIntOrNull() != null) {
                        LS.Output.File(output[0].toInt(), output[1])
                    } else {
                        LS.Output.Dir(output[1])
                    }
                    (commands.lastOrNull() as? LS)?.addOutput(parsedOutput)
                }
            }

            return commands
        }
    }
}

sealed interface File {
    val name: String

    fun toString(indentations: Int): String

    fun asJsonString(indentations: Int): String

    class Dir(override val name: String) : File {
        private var parent: Dir? = null
        private val files = mutableListOf<File>()

        fun openDirectory(directoryName: String): Dir? {
            return files.filterIsInstance<Dir>().find { it.name == directoryName }
        }

        fun close(): Dir? {
            return parent
        }

        fun addDataFile(name: String, size: Int) {
            files.add(Data(name, size))
        }

        fun addDirectory(name: String) {
            files.add(Dir(name).apply { parent = this@Dir })
        }

        fun size(): Int {
            return files.sumOf {
                when (it) {
                    is Data -> it.size
                    is Dir -> it.size()
                }
            }
        }

        fun getDirs(): List<Dir> {
            return files.filterIsInstance<Dir>()
        }

        fun findChildDirsRecursive(): List<Dir> {
            val dirs = mutableListOf<Dir>()
            dirs.add(this)
            this.getDirs().forEach {
                dirs.addAll(it.findChildDirsRecursive())
            }
            return dirs
        }

        override fun toString(indentations: Int): String {
            val builder = StringBuilder()
            val indent = (0..indentations).joinToString(separator = "") { "\t" }
            builder.append("$indent - $name (dir)")
            files.forEach {
                builder.appendLine()
                builder.append(it.toString(indentations + 1))
            }
            return builder.toString()
        }

        override fun asJsonString(indentations: Int): String {
            val builder = StringBuilder()
            builder.append(
                "{ " +
                        "\"name\": \"$name\"," +
                        "\"files\": ["
            )
            files.forEach {
                builder.append(it.asJsonString(0))
            }
            builder.append("]" + "},")
            return builder.toString()
        }
    }

    class Data(override val name: String, val size: Int) : File {
        override fun toString(indentations: Int): String {
            val builder = StringBuilder()
            val indent = (0..indentations).joinToString(separator = "") { "\t" }
            builder.append("$indent - $name (file, size=$size)")
            return builder.toString()
        }

        override fun asJsonString(indentations: Int): String {
            val builder = StringBuilder()
            builder.append(
                "{ " +
                        "\"name\": \"$name\"," +
                        "\"size\": \"size\"," +
                        "},"
            )
            return builder.toString()
        }
    }
}



