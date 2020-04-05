package util.math

import util.requireGreater

class Size(
    x: Int,
    y: Int
) {
    val x = requireGreater(x, 0, "x")
    val y = requireGreater(y, 0, "y")

    fun getCells(): Int = x * y

    fun getIndex(x: Int, y: Int): Int {
        requireInside(x, y)
        return y * this.x + x
    }

    private fun requireInside(x: Int, y: Int) {
        require(x in 0 until this.x) { "x=$x must be inside!" }
        require(y in 0 until this.y) { "y=$y must be inside!" }
    }
}