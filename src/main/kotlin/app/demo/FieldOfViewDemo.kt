package app.demo

import game.GameRenderer
import game.map.GameMapBuilder
import game.map.Terrain
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import kotlin.random.Random
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class FieldOfViewDemo : TileApplication(60, 45, 20, 20) {
    private val map = with(GameMapBuilder(size, Terrain.FLOOR)) {
        addBorder(Terrain.WALL)
        val rng = Random
        repeat(500) { setTerrain(rng.nextInt(size.cells), Terrain.WALL) }
        build()
    }
    private val mapRender = GameRenderer(0, 0, size)

    private var position = 400

    override fun start(primaryStage: Stage) {
        init(primaryStage, "FieldOfView Demo")
        render()
    }

    private fun render() {
        logger.info("render()")

        renderer.clear()
        mapRender.renderMap(tileRenderer, map)
        renderNode(position, "@", Color.BLUE)

        logger.info("render(): finished")
    }

    private fun renderNode(position: Int?, tile: String, color: Color, nodeSize: Int = 1) {
        if (position is Int) {
            val (x, y) = size.getPos(position)
            tileRenderer.renderFullTile(Color.BLACK, x, y, nodeSize)
            tileRenderer.renderText(tile, color, x, y, nodeSize)
        }
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        logger.info("onKeyReleased(): keyCode=$keyCode")

        when (keyCode) {
            KeyCode.ESCAPE -> exitProcess(0)
            else -> return
        }
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y button=$button")

        if (mapRender.area.isInside(x, y)) {
            position = mapRender.area.convert(x, y)
            render()
        }
    }
}

fun main() {
    Application.launch(FieldOfViewDemo::class.java)
}