package app.demo

import javafx.application.Application
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.log.Message
import util.log.MessageLog
import util.log.MessageLogRenderer
import util.math.createPolygon
import util.math.rectangle.Size

private val logger = KotlinLogging.logger {}

class RenderingDemo : TileApplication(50, 20, 22, 32) {

    private var imageId = -1

    private val messageLog = MessageLog(
        listOf(
            Message("Oldest message in the log.", Color.WHITE),
            Message("Something is wrong!", Color.RED),
            Message("Warning for the user", Color.YELLOW)
        )
    )
    private val messageLogRenderer = MessageLogRenderer(20, 0, Size(30, 10))

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Rendering Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)
        imageId = renderer.loadImage("tiles\\paladin.png")
        render()
    }

    private fun render() {
        logger.info("render()")

        with(this.renderer) {
            setColor(Color.RED)
            setFont(32)
            renderUnicode("@", 100, 100)
            renderUnicode("🌳", 200, 200)
            setColor(Color.BLUE)
            renderRectangle(400, 300, 100, 200)
            setColor(Color.GREEN)
            renderPolygon(createPolygon(600.0, 400.0, 600.0, 500.0, 700.0, 450.0))
        }

        with(tileRenderer) {
            renderFullTile(Color.GREEN, 5, 10)
            renderFullTile(Color.BLUE, 6, 10)
            renderUnicodeTile("@", Color.CYAN, 5, 11, 2)
            renderPolygon(createPolygon(0.8, 0.3, 0.8, 0.7, 1.0, 0.5), Color.RED, 5, 11, 2)
        }

        with(tileRenderer) {
            renderText("This is a test.", Color.WHITE, 0, 0, 1)
        }

        messageLogRenderer.render(tileRenderer, messageLog)

        renderer.renderImage(imageId, 400, 300, 100, 100)

        logger.info("render(): finished")
    }
}

fun main() {
    Application.launch(RenderingDemo::class.java)
}