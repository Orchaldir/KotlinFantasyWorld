package ai.pathfinding

import ai.pathfinding.graph.Graph
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class AStar<T> {

    fun find(graph: Graph<T>, start: Int, end: Int): PathfindingResult {
        logger.info("Find path from $start to $end.")

        if (!graph.isValid(end)) {
            logger.info("End is an obstacle")
            return NoPath
        }

        val openNodes = PriorityQueue<AStarNode>()
        val list = arrayOfNulls<AStarNode>(graph.getSize())
        val endNode = AStarNode(end)
        endNode.costSoFar = 0

        openNodes.add(endNode)
        list[end] = endNode

        while (!openNodes.isEmpty()) {
            val currentNode = openNodes.poll()

            if (currentNode.index == start) {
                return backtrack(currentNode)
            }

            for (neighbor in graph.getNeighbors(currentNode.index)) {
                var neighborNode = list[neighbor.index]

                if (neighborNode == null) {
                    neighborNode = AStarNode(neighbor.index)
                    list[neighbor.index] = neighborNode
                }

                val newCost = currentNode.costSoFar + neighbor.cost

                if (newCost < neighborNode.costSoFar) {
                    neighborNode.costSoFar = newCost
                    neighborNode.heuristic = newCost + graph.estimate(neighbor.index, start)
                    neighborNode.previous = currentNode
                    openNodes.add(neighborNode)
                }
            }
        }

        logger.info("Failed to find a path.")

        return NoPath
    }

    private fun backtrack(startNode: AStarNode): Path {
        val indices = mutableListOf<Int>()
        var currentNode: AStarNode? = startNode

        while (currentNode != null) {
            indices.add(currentNode.index)
            currentNode = currentNode.previous
        }

        logger.info("Found path with ${indices.size} nodes.")
        return Path(startNode.costSoFar, indices)
    }

    private data class AStarNode(val index: Int) : Comparable<AStarNode> {
        var costSoFar = Int.MAX_VALUE
        var heuristic = 0
        var previous: AStarNode? = null

        override fun compareTo(other: AStarNode) = heuristic.compareTo(other.heuristic)
    }
}