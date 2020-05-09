package ai.pathfinding.graph

import util.math.rectangle.Size

abstract class PathfindingMap<T>(val cells: List<T>, val size: Size) : Graph<T> {

    override fun getSize() = size.cells

    override fun get(index: Int) = cells[index]

    override fun getNeighbors(index: Int): List<Neighbor> {
        size.requireInside(index)

        val neighbors = mutableListOf<Neighbor>()
        val (x, y) = size.getPos(index)

        addNeighbor(neighbors, x + 1, y)
        addNeighbor(neighbors, x, y + 1)
        addNeighbor(neighbors, x - 1, y)
        addNeighbor(neighbors, x, y - 1)

        return neighbors
    }

    abstract fun addNeighbor(neighbors: MutableList<Neighbor>, x: Int, y: Int)

}