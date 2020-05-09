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
    val origins = getPositions(mapSize, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    return origins.map { mapSize.getDistance(calculator, it, position) }.min()!!
}

fun calculateDistance(calculator: DistanceCalculator, mapSize: Size, from: Body, to: Body): Int {
    val origins = getPositions(mapSize, from)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    val destinations = getPositions(mapSize, to)

    if (destinations.isEmpty()) throw IllegalStateException("No valid destinations!")

    return origins.flatMap { o -> destinations.map { d -> mapSize.getDistance(calculator, o, d) } }.min()!!
}

private fun getPositions(mapSize: Size, body: Body): List<Int> = when (body) {
    is SimpleBody -> listOf(body.position)
    is BigBody -> mapSize.getIndices(body.position, body.size)
    is SnakeBody -> listOf(body.positions.first())
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