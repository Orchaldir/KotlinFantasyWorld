package game.component

import util.math.Direction
import util.math.Direction.NORTH
import util.math.rectangle.DistanceCalculator
import util.math.rectangle.Size

sealed class Body
data class SimpleBody(val position: Int, val direction: Direction = NORTH) : Body()
data class BigBody(val position: Int, val size: Int, val direction: Direction = NORTH) : Body()
data class SnakeBody(val positions: List<Int>, val direction: Direction = NORTH) : Body()

fun calculateDistanceToPosition(calculator: DistanceCalculator, mapSize: Size, body: Body, position: Int): Int {
    val origins = getPositionsUnderBody(mapSize, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    return origins.map { mapSize.getDistance(calculator, it, position) }.min()!!
}

fun calculateDistanceToPositions(calculator: DistanceCalculator, mapSize: Size, from: Body, positions: List<Int>): Int {
    val origins = getPositionsUnderBody(mapSize, from)

    if (origins.isEmpty() || positions.isEmpty()) return Int.MAX_VALUE

    return origins.flatMap { o -> positions.map { d -> mapSize.getDistance(calculator, o, d) } }.min()!!
}

fun calculateDistance(calculator: DistanceCalculator, mapSize: Size, from: Body, to: Body) =
    calculateDistanceToPositions(calculator, mapSize, from, getPositionsUnderBody(mapSize, to))

fun getPositionsAround(
    calculator: DistanceCalculator,
    mapSize: Size,
    body: Body,
    positionSize: Int,
    distanceFilter: (node: Int) -> Boolean
): Set<Int> {
    return if (positionSize == 1) {
        (0..mapSize.cells).filter {
            distanceFilter(calculateDistanceToPosition(calculator, mapSize, body, it))
        }.toSet()
    } else {
        (0..mapSize.cells).filter {
            distanceFilter(
                calculateDistanceToPositions(
                    calculator,
                    mapSize,
                    body,
                    mapSize.getIndices(it, positionSize)
                )
            )
        }.toSet()
    }
}

fun getPositionsAround(
    calculator: DistanceCalculator,
    mapSize: Size,
    body: Body,
    positionSize: Int,
    distance: Int
) = getPositionsAround(calculator, mapSize, body, positionSize) { d -> d == distance }

fun getPositionsUnderBody(mapSize: Size, body: Body): List<Int> = when (body) {
    is SimpleBody -> listOf(body.position)
    is BigBody -> mapSize.getIndices(body.position, body.size)
    is SnakeBody -> body.positions
}

fun getPosition(body: Body) = when (body) {
    is SimpleBody -> body.position
    is BigBody -> body.position
    is SnakeBody -> body.positions.first()
}

fun getSize(body: Body) = when (body) {
    is BigBody -> body.size
    else -> 1
}

fun updateBody(body: Body, position: Int, direction: Direction) = when (body) {
    is SimpleBody -> body.copy(position, direction)
    is BigBody -> body.copy(position, body.size, direction)
    is SnakeBody -> {
        val positions = body.positions.toMutableList()
        positions.removeAt(positions.lastIndex)
        body.copy(listOf(position) + positions, direction)
    }
}