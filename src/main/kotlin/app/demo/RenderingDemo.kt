package app.demo

import javafx.application.Application
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.log.Message
import util.log.MessageLog
import util.log.MessageLogRenderer

private val logger = KotlinLogging.logger {}

class RenderingDemo : TileApplication(50, 20, 22, 32) {

    private val messageLog = MessageLog(
        listOf(
            Message("Oldest message in the log.", Color.WHITE),
            Message("Something is wrong!", Color.RED),
            Message("Warning for the user", Color.YELLOW)
        )
    )
    private val messageLogRenderer = MessageLogRenderer(20, 0)

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Rendering Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)
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
        }

        with(tileRenderer) {
            renderFullTile(Color.GREEN, 5, 10)
            renderFullTile(Color.BLUE, 6, 10)
            renderUnicodeTile("@", Color.CYAN, 5, 11, 2)
        }

        with(tileRenderer) {
            renderText("This is a test.", Color.WHITE, 0, 0, 1)
        }

        messageLogRenderer.render(tileRenderer, messageLog)

        logger.info("render(): finished")
    }
}

fun main() {
    Application.launch(RenderingDemo::class.java)
}