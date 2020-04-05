package app.demo

import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication

private val logger = KotlinLogging.logger {}

class MovementDemo : TileApplication() {
    private lateinit var gameMap: GameMap

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Movement Demo", 60, 40, 20, 20)
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", tiles)

        gameMap = GameMapBuilder(columns, rows, Terrain.FLOOR).build()

        render()
    }

    private fun render() {
        logger.info("render()")

        gameMap.render(tileRenderer, 0, 0)

        logger.info("render(): finished")
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        logger.info("onKeyReleased(): keyCode=$keyCode")
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")
    }
}

fun main() {
    Application.launch(MovementDemo::class.java)
}