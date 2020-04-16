package game.map

import util.math.Size

data class GameMap(
    val size: Size,
    val terrainList: List<Terrain>,
    val entities: Map<Int, Int>
) {

    fun checkWalkability(position: Int, entity: Int): Walkability {
        if (!size.isInside(position)) {
            return OutsideMap
        } else if (!terrainList[position].isWalkable()) {
            return BlockedByObstacle
        }

        return when (val blockingEntity = entities[position]) {
            null, entity -> Walkable(position)
            else -> BlockedByEntity(blockingEntity)
        }
    }

    fun checkWalkability(position: Int, size: Int, entity: Int): Walkability {
        val indices = this.size.getIndices(position, size)

        if (indices.isEmpty()) {
            return OutsideMap
        }

        indices.forEach { i ->
            val result = checkWalkability(i, entity)
            if (result !is Walkable) {
                return result
            }
        }

        return Walkable(position)
    }

    fun builder() = GameMapBuilder(size, terrainList.toMutableList(), entities.toMutableMap())

}