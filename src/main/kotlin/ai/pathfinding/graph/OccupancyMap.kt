package ai.pathfinding.graph

import util.math.Size

class OccupancyMap(list: List<Boolean>, size: Size) : PathfindingMap<Boolean>(list, size) {

    override fun isValid(index: Int) = get(index)

    override fun addNeighbor(neighbors: MutableList<Neighbor>, x: Int, y: Int) {
        val index = size.getIndexIfInside(x, y)

        if (index != null && get(index)) {
            neighbors.add(Neighbor(index, 1))
        }
    }

    override fun estimate(from: Int, to: Int) = size.getChebyshevDistance(from, to)

}