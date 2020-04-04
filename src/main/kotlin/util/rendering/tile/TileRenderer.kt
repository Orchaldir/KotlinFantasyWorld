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

    fun renderCharacter(
        text: String,
        x: Int,
        y: Int,
        color: Color
    ) {
        renderer.setColor(color)
        renderer.renderUnicode(text, getCenterPixelX(x), getCenterPixelY(y))
    }

    fun renderTile(x: Int, y: Int, color: Color) {
        renderer.setColor(color)
        renderer.renderRectangle(getStartPixelX(x), getStartPixelY(y), tileWidth, tileHeight)
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

    private fun getCenterPixelX(x: Int): Int {
        return getStartPixelX(x) + tileWidth / 2
    }

    private fun getCenterPixelY(y: Int): Int {
        return getStartPixelY(y) + tileHeight / 2
    }
}