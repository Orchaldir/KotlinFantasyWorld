package game.map

import game.map.Walkability.*
import javafx.scene.paint.Color
import util.math.Size
import util.rendering.tile.TileRenderer

data class GameMap(
    val size: Size,
    val terrainList: List<Terrain>,
    val entities: Map<Int, Int>
) {

    fun checkWalkability(index: Int, entity: Int): Walkability {
        if (!size.isInside(index)) {
            return OUTSIDE_MAP
        } else if (!terrainList[index].isWalkable()) {
            return BLOCKED_BY_OBSTACLE
        }

        return when (entities[index]) {
            null, entity -> WALKABLE
            else -> BLOCKED_BY_ENTITY
        }
    }

    fun checkWalkability(index: Int, size: Int, entity: Int): Walkability {
        val indices = this.size.getIndices(index, size)

        if (indices.isEmpty()) {
            return OUTSIDE_MAP
        }

        indices.forEach { i ->
            val result = checkWalkability(i, entity)
            if (result != WALKABLE) {
                return result
            }
        }

        return WALKABLE
    }

    fun render(renderer: TileRenderer, startX: Int, startY: Int) {
        var index = 0
        for (y in startY until startY + size.y) {
            for (x in startX until startX + size.x) {
                if (entities.containsKey(index)) {
                    index++
                    continue
                }

                val terrain = terrainList[index++]
                val symbol = if (terrain == Terrain.FLOOR) {
                    "."
                } else {
                    "#"
                }

                renderer.renderUnicode(symbol, Color.WHITE, x, y)
            }
        }
    }

    fun builder() = GameMapBuilder(size, terrainList.toMutableList(), entities.toMutableMap())

}