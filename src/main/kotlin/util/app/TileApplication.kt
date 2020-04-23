package util.app

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.math.Size
import util.rendering.CanvasRenderer
import util.rendering.Renderer
import util.rendering.tile.TileRenderer
import util.requireGreater

private val logger = KotlinLogging.logger {}

abstract class TileApplication(
    sizeX: Int,
    sizeY: Int,
    tileWidth: Int,
    tileHeight: Int
) : Application() {
    val size = Size(requireGreater(sizeX, 0, "sizeX"), requireGreater(sizeY, 0, "sizeY"))
    private val tileWidth = requireGreater(tileWidth, 0, "tileWidth")
    private val tileHeight = requireGreater(tileHeight, 0, "tileHeight")
    private var canvasRenderer: CanvasRenderer? = null
    val renderer: Renderer
        get() = canvasRenderer as Renderer
    lateinit var tileRenderer: TileRenderer

    private var lastX = -1
    private var lastY = -1

    protected fun init(
        primaryStage: Stage,
        windowTitle: String
    ): Scene {

        val canvasWidth = size.x * tileWidth.toDouble()
        val canvasHeight = size.y * tileHeight.toDouble()

        logger.info { "init(): width=${size.x}*$tileWidth=$canvasWidth height=${size.y}*$tileHeight=$canvasHeight" }

        val root = Group()
        val canvas = Canvas(canvasWidth, canvasHeight)
        root.children.add(canvas)
        val windowScene = Scene(root, Color.BLACK)

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
                tileRenderer.getY(event.y.toInt()),
                event.button
            )
        }

        windowScene.onMouseMoved = EventHandler { event ->
            val x = tileRenderer.getX(event.x.toInt())
            val y = tileRenderer.getY(event.y.toInt())

            if (x != lastX || y != lastY) {
                onMouseMoved(x, y)
                lastX = x
                lastY = y
            }
        }

        return windowScene
    }

    open fun onKeyReleased(keyCode: KeyCode) {

    }

    open fun onTileClicked(x: Int, y: Int, button: MouseButton) {

    }

    open fun onMouseMoved(x: Int, y: Int) {

    }
}