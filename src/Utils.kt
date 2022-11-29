import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()
fun readInput(pkg: String, name: String) = File("src/$pkg", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)


interface Day {
    fun run() {
        val pkg = javaClass.`package`.name
        val input = readInput(pkg, javaClass.simpleName)
        println(part1(input))
        println(part2(input))
    }

    fun runTest() {
        println("================ Test ===================")
        val pkg = javaClass.`package`.name
        val input = readInput(pkg, "${javaClass.simpleName}Test")
        println(part1(input))
        println(part2(input))
        println("================ Test ===================")
    }

    fun part1(input: List<String>): Int
    fun part2(input: List<String>): Int

    fun runWithDiagnostics() {
        val start = System.currentTimeMillis()
        val pkg = javaClass.`package`.name
        val input = readInput(pkg, javaClass.simpleName)
        println(part1(input))
        val endP1 = System.currentTimeMillis()
        println(part2(input))
        val endP2 = System.currentTimeMillis()
        println("\nDiagnostics")
        println("\t\t" + " | "+ "Duration")
        println("----------------------------")
        println("Part 1\t" + " | " + "${endP1 - start}ms")
        println("Part 2\t" + " | "  + "${endP2 - start}ms")
    }
}

