package util.math

data class Point(val x: Double, val y: Double) {

    fun rotateClockwise() = Point(y, -x)

}