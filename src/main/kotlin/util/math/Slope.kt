package util.math

data class Slope(val x: Int, val y: Int) {

    fun calculateTopX(localX: Int) = if (x == 1) localX else
        ((localX * 2 + 1) * y + x - 1) / (x * 2)

    fun calculateBottomX(localX: Int) = if (y == 0) 0 else
        ((localX * 2 - 1) * y + x) / (x * 2)

}

fun createSlopeAboveCurrent(x: Int, y: Int) = Slope(x * 2 - 1, y * 2 + 1)

fun createSlopeBelowPrevious(x: Int, y: Int) = Slope(x * 2 + 1, y * 2 + 1)