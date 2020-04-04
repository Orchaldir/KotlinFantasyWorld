package util.app

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import util.rendering.CanvasRenderer
import util.rendering.Renderer
import util.rendering.tile.TileRenderer
import util.requireGreater

abstract class TileApplication : Application() {
    private var columns = 0
    private var rows = 0
    private var tileWidth = 0
    private var tileHeight = 0
    val tiles: Int
        get() = columns * rows
    private var canvasRenderer: CanvasRenderer? = null
    val renderer: Renderer
        get() = canvasRenderer as Renderer

    protected fun init(
        primaryStage: Stage,
        windowTitle: String,
        columns: Int,
        rows: Int,
        tileWidth: Int,
        tileHeight: Int
    ): Scene {
        this.columns = requireGreater(columns, 0, "columns")
        this.rows = requireGreater(rows, 0, "rows")
        this.tileWidth = requireGreater(tileWidth, 0, "tileWidth")
        this.tileHeight = requireGreater(tileHeight, 0, "tileHeight")

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

    fun createTileRenderer(): TileRenderer = TileRenderer(renderer, 0, 0, tileWidth, tileWidth)
}