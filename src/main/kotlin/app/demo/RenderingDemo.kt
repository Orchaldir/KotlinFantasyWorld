package app.demo

import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication

private val logger = KotlinLogging.logger {}

class RenderingDemo : TileApplication() {
    override fun start(primaryStage: Stage) {
        init(primaryStage, "Rendering Demo", 50, 20, 22, 32)
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", tiles)
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
            renderUnicode("@", Color.CYAN, 5, 11, 2)
        }

        logger.info("render(): finished")
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        logger.info("onKeyReleased(): keyCode=$keyCode")
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(RenderingDemo::class.java)
        }
    }
}