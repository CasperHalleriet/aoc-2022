package day13

import Day
import java.lang.reflect.Member
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

fun main() {
    val day = Day13()
    day.runTest()
    day.run()
}

class Day13 : Day {
    override fun part1(input: List<String>): Int {
        val programDTO = ProgramDTO(null, "slug")

        try {

        } catch (e: Exception) {
            println(e.message)
        }


        return -1
    }

    override fun part2(input: List<String>): Int {
        return -1
    }
}

val test = programDTO::guid.requireNotNull()

inline fun <T> KProperty0<T?>.requireNotNull(): T {
    return this.get() ?: throw Exception("${declaringClass().simpleName}.$name should be not null but is null")
}

fun KProperty<*>.declaringClass(): Class<*> {
    return (this.javaField as Member? ?: this.javaGetter)?.declaringClass
        ?: error("Unable to access declaring class")
}

data class ProgramDTO(
    val guid: String?,
    val slug: String?
)


