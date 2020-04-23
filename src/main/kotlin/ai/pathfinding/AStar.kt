package ai.pathfinding

import ai.pathfinding.graph.Graph
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class AStar<T> {

    fun find(graph: Graph<T>, start: Int, goal: Int): PathfindingResult {
        logger.info("Find path from $start to $goal.")

        if (start == goal) {
            logger.info("Goal is already reached")
            return GoalAlreadyReached
        } else if (!graph.isValid(goal)) {
            logger.info("Goal is an obstacle")
            return NoPath
        }

        val openNodes = PriorityQueue<AStarNode>()
        val list = arrayOfNulls<AStarNode>(graph.getSize())
        val goalNode = AStarNode(goal)
        goalNode.costSoFar = 0

        openNodes.add(goalNode)
        list[goal] = goalNode

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
        var currentNode: AStarNode? = startNode.previous

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