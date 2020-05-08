package util.rendering

import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import util.math.Point
import util.math.Polygon

class CanvasRenderer(
    private val graphicsContext: GraphicsContext
) : Renderer {
    private val images = mutableMapOf<Int, Image>()
    private val imageNames = mutableMapOf<String, Int>()

    init {
        graphicsContext.isImageSmoothing = false
    }

    override fun loadImage(filename: String) = imageNames.computeIfAbsent(filename) {
        val newId = images.size
        val image = Image(filename)
        images[newId] = image
        newId
    }

    override fun clear() {
        val canvas = graphicsContext.canvas
        graphicsContext.clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

    override fun renderImage(id: Int, x: Int, y: Int, width: Int, height: Int) {
        images[id]?.let {
            graphicsContext.drawImage(it, x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override fun renderUnicode(text: String, centerX: Int, centerY: Int) =
        graphicsContext.fillText(text, centerX.toDouble(), centerY.toDouble())

    override fun renderRectangle(x: Int, y: Int, width: Int, height: Int) =
        graphicsContext.fillRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    override fun renderPolygon(polygon: Polygon) {
        graphicsContext.fillPolygon(
            polygon.points.map(Point::x).toDoubleArray(),
            polygon.points.map(Point::y).toDoubleArray(),
            polygon.points.size
        )
    }

    override fun renderPolygon(polygon: Polygon, x: Int, y: Int, width: Int, height: Int) {
        graphicsContext.save()
        graphicsContext.translate(x.toDouble(), y.toDouble())
        graphicsContext.scale(width.toDouble(), height.toDouble())
        renderPolygon(polygon)
        graphicsContext.restore()
    }

    override fun setColor(color: Color) {
        graphicsContext.fill = color
    }

    override fun setFont(size: Int, name: String) = with(graphicsContext) {
        font = Font(name, size.toDouble())
        textAlign = TextAlignment.CENTER
        textBaseline = VPos.CENTER
    }
}