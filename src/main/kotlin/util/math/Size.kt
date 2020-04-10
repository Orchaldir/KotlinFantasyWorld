package util.math

import util.requireGreater

class Size(
    x: Int,
    y: Int
) {
    val x = requireGreater(x, 0, "x")
    val y = requireGreater(y, 0, "y")
    val cells: Int
        get() = x * y

    fun getIndex(x: Int, y: Int): Int {
        requireInside(x, y)
        return y * this.x + x
    }

    fun getIndices(index: Int, size: Int): List<Int> {
        if (!isAreaInside(index, size)) {
            return emptyList()
        }

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

    private fun requireInside(index: Int) {
        require(isInside(index)) { "index=$index must be inside!" }
    }
}