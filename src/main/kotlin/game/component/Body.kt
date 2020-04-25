package game.component

import util.math.Size

sealed class Body
data class SimpleBody(val position: Int) : Body()
data class BigBody(val position: Int, val size: Int) : Body()
data class SnakeBody(val positions: List<Int>) : Body()

fun calculateDistanceToPosition(size: Size, body: Body, position: Int): Int {
    val origins = getPositions(size, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    return origins.map { size.getChebyshevDistance(it, position) }.min()!!
}

fun calculateDistance(size: Size, body: Body, target: Body): Int {
    val origins = getPositions(size, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    val destinations = getPositions(size, target)

    if (destinations.isEmpty()) throw IllegalStateException("No valid destinations!")

    return origins.flatMap { o -> destinations.map { d -> size.getChebyshevDistance(o, d) } }.min()!!
}

private fun getPositions(size: Size, body: Body): List<Int> = when (body) {
    is SimpleBody -> listOf(body.position)
    is BigBody -> size.getIndices(body.position, body.size)
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