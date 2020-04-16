package game.map

import javafx.scene.paint.Color
import util.math.Size
import util.rendering.tile.TileRenderer

class GameMapRenderer(
    private val startX: Int,
    private val startY: Int,
    private val size: Size,
    private val offsetX: Int,
    private val offsetY: Int
) {

    constructor(size: Size) : this(0, 0, size, 0, 0)

    fun render(renderer: TileRenderer, map: GameMap) {
        for (y in 0 until size.y) {
            for (x in 0 until size.x) {
                if (!size.isInside(x + offsetX, y + offsetY)) {
                    continue
                }

                val mapIndex = size.getIndex(x + offsetX, y + offsetY)

                if (map.entities.containsKey(mapIndex)) {
                    continue
                }

                val terrain = map.terrainList[mapIndex]
                val symbol = if (terrain == Terrain.FLOOR) {
                    "."
                } else {
                    "#"
                }

                renderer.renderUnicodeTile(symbol, Color.WHITE, startX + x, startY + y)
            }
        }
    }

}