package util.math.fov

import util.math.rectangle.Size

data class FovConfig(
    val mapSize: Size,
    val position: Int,
    val x: Int,
    val y: Int,
    val range: Int,
    val isBlocking: (position: Int) -> Boolean
)

fun createFovConfig(mapSize: Size, position: Int, range: Int, isBlocking: (position: Int) -> Boolean): FovConfig {
    val (x, y) = mapSize.getPos(position)
    return FovConfig(mapSize, position, x, y, range, isBlocking)
}