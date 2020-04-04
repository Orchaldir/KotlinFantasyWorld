package util.rendering

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

class CanvasRenderer(
    private val graphicsContext: GraphicsContext
) : Renderer {
    override fun clear(x: Int, y: Int, width: Int, height: Int) =
        graphicsContext.clearRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    override fun renderUnicode(text: String, centerX: Int, centerY: Int) =
        graphicsContext.fillText(text, centerX.toDouble(), centerY.toDouble())

    override fun renderRectangle(x: Int, y: Int, width: Int, height: Int) =
        graphicsContext.fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    override fun setColor(color: Color) {
        graphicsContext.fill = color
    }

    override fun setFont(size: Int, name: String) = with(graphicsContext) {
        font = Font(name, size.toDouble())
        textAlign = TextAlignment.CENTER
        textBaseline = VPos.CENTER
    }
}