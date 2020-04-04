package util.rendering

import javafx.scene.paint.Color

interface Renderer {
    fun clear(x: Int, y: Int, width: Int, height: Int)

    fun renderUnicode(text: String, centerX: Int, centerY: Int)
    fun renderRectangle(x: Int, y: Int, width: Int, height: Int)

    fun setColor(color: Color)
    fun setFont(size: Int, name: String = "Liberation Mono")
}