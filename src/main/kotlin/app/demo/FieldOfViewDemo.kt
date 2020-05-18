package app.demo

import game.GameRenderer
import game.component.Perception
import game.map.GameMapBuilder
import game.map.Terrain
import game.map.Walkable
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.math.fov.ShadowCasting
import util.math.fov.createFovConfig
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
    private val fovAlgorithm = ShadowCasting()
    private var perception = Perception(10)

    private var position = 990

    override fun start(primaryStage: Stage) {
        init(primaryStage, "FieldOfView Demo")
        update()
    }

    private fun update() {
        logger.info("update()")

        val config = createFovConfig(
            map.size,
            position,
            perception.maxRange
        ) { position -> !map.terrainList[position].isWalkable() }

        val visibleCells = fovAlgorithm.calculateVisibleCells(config)

        perception = perception.update(visibleCells)

        render()
    }

    private fun render() {
        logger.info("render()")

        renderer.clear()

        perception.knownTiles.forEach { renderNode(it, Color.GRAY) }
        perception.visibleTiles.forEach { renderNode(it, Color.GREEN) }
        renderNode(position, Color.BLUE)

        mapRender.renderMap(tileRenderer, map)

        logger.info("render(): finished")
    }

    private fun renderNode(position: Int, color: Color) {
        val (x, y) = size.getPos(position)
        tileRenderer.renderFullTile(color, x, y)
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
            val newPosition = mapRender.area.convert(x, y)

            if (map.checkWalkability(newPosition, 0) is Walkable) {
                position = newPosition
                update()
            }
        }
    }
}

fun main() {
    Application.launch(FieldOfViewDemo::class.java)
}