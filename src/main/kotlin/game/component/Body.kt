package game.component

import util.math.Size

sealed class Body
data class SimpleBody(val position: Int) : Body()
data class BigBody(val position: Int, val size: Int) : Body()
data class SnakeBody(val positions: List<Int>) : Body()

fun calculateDistanceToPosition(size: Size, body: Body, position: Int): Int {
    val origins = getOrigins(size, body)

    if (origins.isEmpty()) throw IllegalStateException("No valid origins!")

    return origins.map { size.getManhattanDistance(it, position) }.min()!!
}

private fun getOrigins(size: Size, body: Body): List<Int> = when (body) {
    is SimpleBody -> listOf(body.position)
    is BigBody -> size.getIndices(body.position, body.size)
    is SnakeBody -> listOf(body.positions.first())
}