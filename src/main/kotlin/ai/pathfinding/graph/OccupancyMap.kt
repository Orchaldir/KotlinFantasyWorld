package ai.pathfinding.graph

import util.math.rectangle.DistanceCalculator
import util.math.rectangle.Size

class OccupancyMap(
    val calculator: DistanceCalculator,
    cells: List<Boolean>,
    size: Size
) : PathfindingMap<Boolean>(cells, size) {

    override fun isValid(index: Int) = get(index)

    override fun addNeighbor(neighbors: MutableList<Neighbor>, x: Int, y: Int) {
        val index = size.getIndexIfInside(x, y)

        if (index != null && get(index)) {
            neighbors.add(Neighbor(index, 1))
        }
    }

    override fun estimate(from: Int, to: Int) = size.getDistance(calculator, from, to)

}