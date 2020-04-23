package ai.pathfinding.graph

interface Graph<T> {

    fun getSize(): Int

    operator fun get(index: Int): T

    fun isValid(index: Int): Boolean

    fun getNeighbors(index: Int): List<Neighbor>

    fun estimate(from: Int, to: Int): Int

}