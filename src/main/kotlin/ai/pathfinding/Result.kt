package ai.pathfinding

sealed class PathfindingResult
data class Path(val totalCost: Int, val indices: List<Int>) : PathfindingResult()
object NoPath : PathfindingResult()