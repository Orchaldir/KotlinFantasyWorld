package util.app

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import mu.KotlinLogging
import util.rendering.CanvasRenderer
import util.rendering.Renderer
import util.rendering.tile.TileRenderer
import util.requireGreater

private val logger = KotlinLogging.logger {}

abstract class TileApplication : Application() {
    var columns = 0
        private set
    var rows = 0
        private set
    private var tileWidth = 0
    private var tileHeight = 0
    val tiles: Int
        get() = columns * rows
    private var canvasRenderer: CanvasRenderer? = null
    val renderer: Renderer
        get() = canvasRenderer as Renderer
    lateinit var tileRenderer: TileRenderer

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

        val canvasWidth = columns * tileWidth.toDouble()
        val canvasHeight = rows * tileHeight.toDouble()

        logger.info { "init(): width=$columns*$tileWidth=$canvasWidth height=$rows*$tileHeight=$canvasHeight" }

        val root = Group()
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
        tileRenderer = TileRenderer(renderer, 0, 0, tileWidth, tileHeight)

        windowScene.onKeyReleased = EventHandler { event: KeyEvent ->
            onKeyReleased(event.code)
        }

        windowScene.onMouseClicked = EventHandler { event ->
            onTileClicked(
                tileRenderer.getX(event.x.toInt()),
                tileRenderer.getY(event.y.toInt())
            )
        }

        return windowScene
    }

    open fun onKeyReleased(keyCode: KeyCode) {

    }

    open fun onTileClicked(x: Int, y: Int) {

    }
}