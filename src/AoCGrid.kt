import java.lang.Integer.max

typealias AoCGrid<T> = Array<Array<T>>
fun createNumericGrid(input: List<String>): AoCGrid<Int> {
    return input.map { row ->
        row.map { it.digitToInt() }.toTypedArray()
    }.toTypedArray()
}

inline fun <reified T> createGridFromCoordinates(coordinates: List<Coordinate>, defValue: T): AoCGrid<T> {
    var maxY = 0
    var maxX = 0
    coordinates.forEach {
        maxX = max(it.x, maxX)
        maxY = max(it.y, maxY)
    }
    return createGrid(maxY +1, maxX + 1) {
        defValue
    }
}

inline fun <reified T> createGrid(rows: Int, columns: Int, initializer: (Coordinate) -> T) : AoCGrid<T> {
    return AoCGrid(rows) { row ->
        Array(columns) { column ->
            initializer(Coordinate(column, row))
        }
    }
}

fun <T> AoCGrid<T>.getValue(coordinate: Coordinate): T? {
    return getOrNull(coordinate.y)?.getOrNull(coordinate.x)
}

fun <T> AoCGrid<T>.getValueOrDefault(coordinate: Coordinate, default: T): T {
    return getValue(coordinate) ?: default
}

data class Coordinate(val x: Int, val y: Int)

fun <T> AoCGrid<T>.getNewCoordinate(coordinate: Coordinate, direction: Direction): Coordinate? {
    val newX = coordinate.x + direction.xModifier
    val newY = coordinate.y + direction.yModifier
    return if(newY < this.size && newX < this[0].size && newX >= 0 && newY >= 0) Coordinate(newX, newY) else null
}

fun <T> AoCGrid<T>.mutateValue(coordinate: Coordinate, block: (T) -> T) {
    this[coordinate.y][coordinate.x] = block(this[coordinate.y][coordinate.x])
}

fun <T> AoCGrid<T>.cellsIndexed(block: (T, Coordinate) -> Unit) {
    this.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, value ->
            val coordinate = Coordinate(columnIndex, rowIndex)
            block(value, coordinate)
        }
    }
}

fun <T> AoCGrid<T>.print(delimiter: String = ",") {
    val builder: StringBuilder = StringBuilder()
    this.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, value ->
            builder.append(value)
            builder.append(delimiter)
        }
        builder.appendLine()
    }
    println(builder.toString())
}

sealed class Direction(val xModifier: Int, val yModifier: Int) {
    object TopLeft: Direction(-1,-1)
    object TopCenter: Direction(0,-1)
    object TopRight: Direction(1,-1)
    object MidLeft: Direction(-1, 0)
    object MidRight: Direction(1, 0)
    object BottomLeft: Direction(-1, +1)
    object BottomCenter: Direction(0, +1)
    object BottomRight: Direction(1, +1)
    class Relative(xModifier: Int, yModifier: Int): Direction(xModifier, yModifier)
}

fun AoCGrid<Int>.surrounding(coordinate: Coordinate): List<Coordinate> {
    return listOfNotNull(
        getNewCoordinate(coordinate, Direction.TopLeft),
        getNewCoordinate(coordinate, Direction.TopCenter),
        getNewCoordinate(coordinate, Direction.TopRight),
        getNewCoordinate(coordinate, Direction.MidLeft),
        getNewCoordinate(coordinate, Direction.MidRight),
        getNewCoordinate(coordinate, Direction.BottomLeft),
        getNewCoordinate(coordinate, Direction.BottomCenter),
        getNewCoordinate(coordinate, Direction.BottomRight)
    )
}

fun <T> AoCGrid<T>.rowCount(): Int {
    return this.size
}

fun <T> AoCGrid<T>.columnCount(): Int {
    return maxOf { it.size }
}

fun <T> AoCGrid<T>.totalSize(): Int {
    return rowCount() * columnCount()
}

fun AoCGrid<Int>.runStep(): Int {
    var flashes = 0
    cellsIndexed { value, coordinate ->
        mutateValue(coordinate) { it + 1 }
    }
    cellsIndexed { value, coordinate ->
        flashes += tryFlash(coordinate)
    }
    return flashes
}

fun AoCGrid<Int>.tryFlash(coordinate: Coordinate): Int {
    var flashes = 0
    mutateValue(coordinate) { value ->
        if(value > 9) {
            flashes++
            0
        } else {
            value
        }
    }
    if(flashes == 1) {
        surrounding(coordinate).forEach { surroundingCoordinate ->
            mutateValue(surroundingCoordinate) {
                if(it == 0) it else it + 1
            }
            flashes += if (getValueOrDefault(surroundingCoordinate, 0) > 9) tryFlash(surroundingCoordinate) else 0
        }
    }

    return flashes
}

sealed interface FoldInstruction {
    class Horizontal(val axis: Int): FoldInstruction
    class Vertical(val axis: Int): FoldInstruction

    companion object {
        fun parse(string: String): FoldInstruction {
            val instruction = string.split(' ')[2].split('=')
            val axis = instruction[1].toInt()
            return if(instruction[0] == "y") {
                Horizontal(axis)
            } else {
                Vertical(axis)
            }
        }
    }
}

fun AoCGrid<String>.fold(foldInstruction: FoldInstruction): AoCGrid<String> {
    return when(foldInstruction) {
        is FoldInstruction.Horizontal -> foldHorizontal(foldInstruction.axis)
        is FoldInstruction.Vertical -> foldVertical(foldInstruction.axis)
    }
}

fun AoCGrid<String>.foldHorizontal(axis: Int): AoCGrid<String> {
    return createGrid(axis, this.columnCount()) {
        val opposite = Coordinate(it.x, axis + (axis - it.y))
        val curValue = this.getValue(it)
        val oppositeValue = this.getValue(opposite)
        if(curValue == "#" || oppositeValue == "#") {
            "#"
        } else {
            "."
        }
    }
}

fun AoCGrid<String>.foldVertical(axis: Int): AoCGrid<String> {
    return createGrid(this.rowCount(), axis) {
        val opposite = Coordinate(axis + (axis - it.x), it.y)
        val curValue = this.getValue(it)
        val oppositeValue = this.getValue(opposite)
        if(curValue == "#" || oppositeValue == "#") {
            "#"
        } else {
            "."
        }
    }
}