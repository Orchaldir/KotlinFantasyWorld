package util.rendering.tile

import javafx.scene.paint.Color

sealed class Tile
object EmptyTile : Tile()
data class FullTile(val color: Color) : Tile()
data class ImageTile(val id: Int) : Tile()
data class UnicodeTile(val symbol: String, val color: Color) : Tile()