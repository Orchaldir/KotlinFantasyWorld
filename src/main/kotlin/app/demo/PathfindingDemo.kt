package app.demo

import ai.pathfinding.AStar
import ai.pathfinding.Path
import game.GameRenderer
import game.map.GameMapBuilder
import game.map.Terrain
import javafx.application.Application
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseButton.PRIMARY
import javafx.scene.input.MouseButton.SECONDARY
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

class PathfindingDemo : TileApplication(60, 45, 20, 20) {
    private val map = with(GameMapBuilder(size, Terrain.FLOOR)) {
        addBorder(Terrain.WALL)
        val rng = Random
        repeat(500) { setTerrain(rng.nextInt(size.cells), Terrain.WALL) }
        build()
    }
    private val mapRender = GameRenderer(0, 0, size)
    private val occupancyMap = map.createOccupancyMap(0)
    private val pathfinding = AStar<Boolean>()

    private var start: Int? = null
    private var end: Int? = null

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Pathfinding Demo")
        render()
    }

    private fun render() {
        logger.info("render()")

        renderer.clear()

        mapRender.renderMap(tileRenderer, map)

        if (start != null && end != null) {
            val path = pathfinding.find(occupancyMap, start!!, end!!)

            if (path is Path) {
                path.indices.forEach { renderNode(it, "P", Color.BLUE) }
            }
        }

        renderNode(start, "S", Color.GREEN)
        renderNode(end, "E", Color.RED)

        logger.info("render(): finished")
    }

    private fun renderNode(position: Int?, tile: String, color: Color) {
        if (position is Int) {
            val (x, y) = size.getPos(position)
            tileRenderer.renderFullTile(Color.BLACK, x, y)
            tileRenderer.renderText(tile, color, x, y)
        }
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y button=$button")

        if (mapRender.area.isInside(x, y)) {
            val position = mapRender.area.convert(x, y)

            when (button) {
                PRIMARY -> start = position
                SECONDARY -> end = position
                else -> return
            }

            render()
        }
    }
}

fun main() {
    Application.launch(PathfindingDemo::class.java)
}