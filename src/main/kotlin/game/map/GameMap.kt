package game.map

import javafx.scene.paint.Color
import util.math.Size
import util.rendering.tile.TileRenderer

data class GameMap(
    val size: Size,
    val terrainList: List<Terrain>,
    val entities: Map<Int, Int>
) {

    fun isWalkable(index: Int, entity: Int): Boolean {
        if (!terrainList[index].isWalkable()) {
            return false
        }

        return when (entities[index]) {
            null -> true
            entity -> true
            else -> false
        }
    }

    fun isWalkable(index: Int, size: Int, entity: Int): Boolean {
        val indices = this.size.getIndices(index, size)

        if (indices.isEmpty()) {
            return false
        }

        indices.forEach { i ->
            if (!isWalkable(i, entity)) {
                return false
            }
        }

        return true
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