package game.map

import javafx.scene.paint.Color
import util.math.Size
import util.rendering.tile.TileRenderer

data class GameMap(
    val size: Size,
    val terrainList: List<Terrain>
) {

    fun render(renderer: TileRenderer, startX: Int, startY: Int) {
        var index = 0
        for (y in startY until startY + size.y) {
            for (x in startX until startX + size.x) {
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