package util.app

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import util.rendering.CanvasRenderer
import util.rendering.Renderer

abstract class TileApplication : Application() {
    private var columns = 0
    private var rows = 0
    private var tileWidth = 0
    private var tileHeight = 0
    private var canvasRenderer: CanvasRenderer? = null
    val renderer: Renderer
        get() = canvasRenderer as Renderer
    val tiles: Int
        get() = columns * rows

    protected fun init(
        primaryStage: Stage,
        windowTitle: String,
        columns: Int,
        rows: Int,
        tileWidth: Int,
        tileHeight: Int
    ): Scene {
        this.columns = columns
        this.rows = rows
        this.tileWidth = tileWidth
        this.tileHeight = tileHeight

        val root = Group()
        val canvasWidth = columns * tileWidth.toDouble()
        val canvasHeight = rows * tileHeight.toDouble()
        val canvas = Canvas(canvasWidth, canvasHeight)
        root.children.add(canvas)
        val windowScene = Scene(root)

        with(primaryStage) {
            title = windowTitle
            scene = windowScene
            isResizable = false
            show()
        }

        canvasRenderer = CanvasRenderer(canvas.graphicsContext2D)

        return windowScene
    }
}