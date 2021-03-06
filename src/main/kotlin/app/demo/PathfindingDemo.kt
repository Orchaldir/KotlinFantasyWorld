package app.demo

import ai.pathfinding.AStar
import ai.pathfinding.Path
import game.GameRenderer
import game.map.GameMapBuilder
import game.map.Terrain
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseButton.PRIMARY
import javafx.scene.input.MouseButton.SECONDARY
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.math.rectangle.Chebyshev
import kotlin.random.Random
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class PathfindingDemo : TileApplication(60, 45, 20, 20) {
    private val map = with(GameMapBuilder(size, Terrain.FLOOR)) {
        addBorder(Terrain.WALL)
        val rng = Random
        repeat(500) { setTerrain(rng.nextInt(size.cells), Terrain.WALL) }
        build()
    }
    private val mapRender = GameRenderer(0, 0, size)
    private var occupancyMap = map.createOccupancyMap(Chebyshev, 0)
    private val pathfinding = AStar<Boolean>()
    private var renderOccupancyMap = false

    private var start: Int? = null
    private var goal: Int? = null
    private var pathSize = 1

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Pathfinding Demo")
        render()
    }

    private fun render() {
        logger.info("render(): pathSize=$pathSize")

        renderer.clear()

        if (renderOccupancyMap) {
            mapRender.renderOccupancyMap(tileRenderer, occupancyMap)
        } else {
            mapRender.renderMap(tileRenderer, map)
        }

        if (start != null && goal != null) {
            val path = pathfinding.find(occupancyMap, start!!, goal!!, pathSize)

            if (path is Path) {
                path.indices.forEach { renderNode(it, "P", Color.BLUE) }
            }
        }

        renderNode(start, "S", Color.GREEN, pathSize)
        renderNode(goal, "G", Color.RED, pathSize)

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
            KeyCode.DIGIT1 -> pathSize = 1
            KeyCode.DIGIT2 -> pathSize = 2
            KeyCode.DIGIT3 -> pathSize = 3
            KeyCode.F1 -> renderOccupancyMap = false
            KeyCode.F2 -> renderOccupancyMap = true
            KeyCode.ESCAPE -> exitProcess(0)
            else -> return
        }

        occupancyMap = map.createOccupancyMap(calculator = Chebyshev, entitySize = pathSize, entity = 0)

        render()
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y button=$button")

        if (mapRender.area.isInside(x, y)) {
            val position = mapRender.area.convert(x, y)

            when (button) {
                PRIMARY -> start = position
                SECONDARY -> goal = position
                else -> return
            }

            render()
        }
    }
}

fun main() {
    Application.launch(PathfindingDemo::class.java)
}