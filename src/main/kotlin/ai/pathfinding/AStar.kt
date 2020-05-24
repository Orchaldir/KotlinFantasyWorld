package ai.pathfinding

import ai.pathfinding.graph.Graph
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class AStar<T> {

    fun find(graph: Graph<T>, start: Int, goal: Int, pathSize: Int) =
        find(graph, start, setOf(goal), pathSize)

    fun find(graph: Graph<T>, start: Int, goals: Set<Int>, pathSize: Int): PathfindingResult {
        logger.info("Find path from $start to $goals.")

        val openNodes = PriorityQueue<AStarNode>()
        val list = arrayOfNulls<AStarNode>(graph.getSize())
        var isAnyGoalReachable = false

        for (goal in goals) {
            if (start == goal) {
                logger.info("Goal is already reached")
                return GoalAlreadyReached
            } else if (graph.isValid(goal)) {
                isAnyGoalReachable = true
            }

            val goalNode = AStarNode(goal)
            goalNode.costSoFar = 0
            openNodes.add(goalNode)
            list[goal] = goalNode
        }

        if (!isAnyGoalReachable) {
            logger.info("All goals are an obstacle")
            return NoPathFound(goals = goals, size = pathSize)
        }

        while (!openNodes.isEmpty()) {
            val currentNode = openNodes.poll()

            if (currentNode.index == start) {
                return backtrack(currentNode, pathSize)
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

        return NoPathFound(goals = goals, size = pathSize)
    }

    private fun backtrack(startNode: AStarNode, pathSize: Int): Path {
        val indices = mutableListOf<Int>()
        var currentNode: AStarNode? = startNode.previous

        while (currentNode != null) {
            indices.add(currentNode.index)
            currentNode = currentNode.previous
        }

        logger.info("Found path with ${indices.size} nodes.")
        return Path(size = pathSize, totalCost = startNode.costSoFar, indices = indices)
    }

    private data class AStarNode(val index: Int) : Comparable<AStarNode> {
        var costSoFar = Int.MAX_VALUE
        var heuristic = 0
        var previous: AStarNode? = null

        override fun compareTo(other: AStarNode) = heuristic.compareTo(other.heuristic)
    }
}