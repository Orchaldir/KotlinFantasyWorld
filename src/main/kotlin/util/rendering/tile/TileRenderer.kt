package util.rendering.tile

import javafx.scene.paint.Color
import util.rendering.Renderer
import util.requireGreater

class TileRenderer(
    private val renderer: Renderer,
    pixelX: Int,
    pixelY: Int,
    tileWidth: Int,
    tileHeight: Int
) {
    private val startPixelX = requireGreater(pixelX, -1, "pixelX")
    private val startPixelY = requireGreater(pixelY, -1, "pixelY")
    private val tileWidth = requireGreater(tileWidth, 0, "tileWidth")
    private val tileHeight = requireGreater(tileHeight, 0, "tileHeight")

    fun renderUnicode(
        text: String,
        color: Color,
        x: Int,
        y: Int,
        size: Int = 1
    ) {
        with(renderer) {
            setColor(color)
            setFont(size * tileHeight)
            renderUnicode(text, getCenterPixelX(x, size), getCenterPixelY(y, size))
        }
    }

    fun renderFullTile(
        color: Color,
        x: Int,
        y: Int,
        size: Int = 1
    ) {
        renderer.setColor(color)
        renderer.renderRectangle(getStartPixelX(x), getStartPixelY(y), tileWidth * size, tileHeight * size)
    }

    fun renderTile(
        tile: Tile,
        x: Int,
        y: Int,
        size: Int = 1
    ) {
        when (tile) {
            is FullTile -> renderFullTile(tile.color, x, y, size)
            is UnicodeTile -> renderUnicode(tile.symbol, tile.color, x, y, size)
            is EmptyTile -> return
        }
    }

    fun getX(pixelX: Int): Int {
        return (pixelX - startPixelX) / tileWidth
    }

    fun getY(pixelY: Int): Int {
        return (pixelY - startPixelY) / tileHeight
    }

    private fun getStartPixelX(x: Int): Int {
        return startPixelX + x * tileWidth
    }

    private fun getStartPixelY(y: Int): Int {
        return startPixelY + y * tileHeight
    }

    private fun getCenterPixelX(x: Int, size: Int): Int {
        return getStartPixelX(x) + size * tileWidth / 2
    }

    private fun getCenterPixelY(y: Int, size: Int): Int {
        return getStartPixelY(y) + size * tileHeight / 2
    }
}