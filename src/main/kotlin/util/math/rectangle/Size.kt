package util.math.rectangle

import util.math.Direction
import util.requireGreater

data class Size(
    val x: Int,
    val y: Int
) {

    init {
        requireGreater(x, 0, "x")
        requireGreater(y, 0, "y")
    }

    val cells: Int
        get() = x * y

    fun getIndex(x: Int, y: Int): Int {
        requireInside(x, y)
        return y * this.x + x
    }

    fun getIndexIfInside(x: Int, y: Int) = if (isInside(x, y)) {
        y * this.x + x
    } else {
        null
    }

    fun getIndices(index: Int, size: Int): List<Int> {
        if (!isAreaInside(index, size)) return emptyList()

        val indices = mutableListOf<Int>()

        for (dy in 0 until size) {
            var currentIndex = index + dy * x

            for (dx in 0 until size) {
                indices.add(currentIndex++)
            }
        }

        return indices
    }

    fun getX(index: Int) = index % x

    fun getY(index: Int) = index / x

    fun getPos(index: Int) = Pair(getX(index), getY(index))

    fun getDirection(from: Int, to: Int): Direction {
        val (fromX, fromY) = getPos(from)
        val (toX, toY) = getPos(to)
        val diffX = toX - fromX
        val diffY = toY - fromY

        return Direction.values().firstOrNull { d -> d.isMatch(diffX, diffY) }
            ?: throw IllegalArgumentException("From $from & to $to are not neighbors!")
    }

    fun getNeighbor(index: Int, direction: Direction) = getOffset(index, direction.x, direction.y)

    private fun getOffset(index: Int, deltaX: Int, deltaY: Int): Int? {
        val (x, y) = getPos(index)
        val offsetX = x + deltaX
        val offsetY = y + deltaY

        return if (isInside(offsetX, offsetY)) {
            getIndex(offsetX, offsetY)
        } else {
            null
        }
    }

    // distance

    fun getDistance(calculator: DistanceCalculator, from: Int, to: Int): Int {
        val (fromX, fromY) = getPos(from)
        val (toX, toY) = getPos(to)

        return calculateDistance(calculator, fromX, fromY, toX, toY)
    }

    // inside check

    fun isInside(index: Int) = index in 0 until cells

    fun isInside(x: Int, y: Int) = isInsideForX(x) && isInsideForY(y)

    fun isAreaInside(index: Int, size: Int): Boolean {
        val (startX, startY) = getPos(index)
        val endX = startX + size - 1
        val endY = startY + size - 1

        return isInside(startX, startY) && isInside(endX, endY)
    }

    fun isInsideForX(x: Int) = x in 0 until this.x

    fun isInsideForY(y: Int) = y in 0 until this.y

    private fun requireInside(x: Int, y: Int) {
        require(isInsideForX(x)) { "x=$x must be inside!" }
        require(isInsideForY(y)) { "y=$y must be inside!" }
    }

    fun requireInside(index: Int) {
        require(isInside(index)) { "index=$index must be inside!" }
    }
}