package game.map

import javafx.scene.paint.Color
import util.rendering.tile.TileRenderer
import util.requireGreater

class GameMap(
    sizeX: Int,
    sizeY: Int,
    private val terrainList: List<Terrain>
) {
    private val sizeX = requireGreater(sizeX, 0, "sizeX")
    private val sizeY = requireGreater(sizeY, 0, "sizeY")

    fun render(renderer: TileRenderer, startX: Int, startY: Int) {
        var index = 0
        for (y in startY until startY + sizeY) {
            for (x in startX until startX + sizeX) {
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
}