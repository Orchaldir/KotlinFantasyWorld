package game.component

import util.math.Size

sealed class Body
data class SimpleBody(val position: Int) : Body()
data class BigBody(val position: Int, val size: Int) : Body()
data class SnakeBody(val positions: List<Int>) : Body()

fun calculateDistanceToPosition(mapSize: Size, body: Body, position: Int): Int {
    val origins = getPositions(mapSize, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    return origins.map { mapSize.getChebyshevDistance(it, position) }.min()!!
}

fun calculateDistance(mapSize: Size, from: Body, to: Body): Int {
    val origins = getPositions(mapSize, from)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    val destinations = getPositions(mapSize, to)

    if (destinations.isEmpty()) throw IllegalStateException("No valid destinations!")

    return origins.flatMap { o -> destinations.map { d -> mapSize.getChebyshevDistance(o, d) } }.min()!!
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