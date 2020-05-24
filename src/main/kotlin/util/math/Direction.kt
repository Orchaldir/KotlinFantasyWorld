package util.math

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    fun isMatch(x: Int, y: Int) = x == this.x && y == this.y
}