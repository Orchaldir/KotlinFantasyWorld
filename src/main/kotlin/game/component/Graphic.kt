package game.component

import javafx.scene.paint.Color
import util.rendering.tile.Tile
import util.rendering.tile.UnicodeTile

val DEFAULT_GRAPHIC = UnicodeTile("?", Color.PINK)

data class Graphic(val tiles: List<Tile>) {

    fun get(index: Int): Tile = tiles.getOrElse(index) { DEFAULT_GRAPHIC }

}