package ai.pathfinding

sealed class PathfindingResult
object GoalAlreadyReached : PathfindingResult()
data class NoPathFound(val goal: Int, val size: Int) : PathfindingResult()
object NotSearched : PathfindingResult()
data class Path(val size: Int, val totalCost: Int, val indices: List<Int>) : PathfindingResult()