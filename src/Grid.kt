import kotlin.math.max
import kotlin.math.min

data class Grid(val minX: Int,
                val minY: Int,
                val maxX: Int,
                val maxY: Int,) {

    private var grid = Array((maxY - minY) + 1) {Array((maxX - minX) + 1) {0} }

    fun markLine(line: Line) {
        val moveRight = (line.to.x - line.from.x) > 0
        val moveLeft = (line.to.x - line.from.x) < 0
        val moveDown = (line.to.y - line.from.y) > 0
        val moveUp = (line.to.y - line.from.y) < 0

        var curCoordinate = line.from
        val endCoordinate = line.to
        while(curCoordinate != endCoordinate) {
            markCoordinate(curCoordinate)
            val moveYTranslation = if(moveUp) -1 else if (moveDown) 1 else 0
            val moveXTranslation = if(moveLeft) -1 else if (moveRight) 1 else 0
            curCoordinate = Coordinate(curCoordinate.x + moveXTranslation, curCoordinate.y + moveYTranslation)
        }
        markCoordinate(endCoordinate)
    }

    fun markCoordinate(coordinate: Coordinate) {
        val curValue = grid[coordinate.y][coordinate.x]
        grid[coordinate.y][coordinate.x] = curValue + 1
    }

    fun getGrid(): Array<Array<Int>> {
        return grid
    }

    fun gridString(): String {
        val builder= StringBuilder()

        grid.forEach { row ->
            row.forEach { column ->
                builder.append(" ${if(column == 0) "." else column} ")
            }
            builder.appendLine()
        }
        return builder.toString()
    }

    companion object {
        var minX: Int = 0
        var minY: Int = 0
        var maxX: Int = 0
        var maxY: Int = 0

        fun create(coordinates: List<Coordinate>): Grid {
            coordinates.forEach {
                minX = min(minX, it.x)
                minY = min(minY, it.y)
                maxX = max(maxX, it.x)
                maxY = max(maxY, it.y)
            }
            return Grid(minX, minY, maxX, maxY)
        }
    }
}

class Line(val from: Coordinate, val to: Coordinate) {

    fun isStraightLine(): Boolean {
        return from.x == to.x || from.y == to.y
    }

    companion object {
        fun create(string: String): Line {
            val splits = string.split(" -> ")
            val from = splits[0].split(',')
            val to = splits[1].split(',')
            val fromCoordinate = Coordinate(from[0].toInt(), from[1].toInt())
            val toCoordinate = Coordinate(to[0].toInt(), to[1].toInt())
            return Line(fromCoordinate, toCoordinate)
        }
    }
}