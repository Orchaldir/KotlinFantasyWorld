package app.demo

import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication

private val logger = KotlinLogging.logger {}

class RenderingDemo : TileApplication() {
    override fun start(primaryStage: Stage) {
        init(primaryStage, "Rendering Demo", 50, 30, 22, 32)
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", tiles)
        render()
    }

    private fun render() {
        logger.info("render()")
        logger.info("render(): finished")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(RenderingDemo::class.java)
        }
    }
}