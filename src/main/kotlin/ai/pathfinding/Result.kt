package ai.pathfinding

sealed class PathfindingResult
object GoalAlreadyReached : PathfindingResult()
data class Path(val totalCost: Int, val indices: List<Int>) : PathfindingResult()
object NoPath : PathfindingResult()