package app.demo

import app.demo.FieldOfViewDemo.Status.BLOCKING
import app.demo.FieldOfViewDemo.Status.CLEAR
import game.GameRenderer
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
import util.math.Octant
import util.math.getGlobal
import util.math.rectangle.Size
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

    private var position = 990

    override fun start(primaryStage: Stage) {
        init(primaryStage, "FieldOfView Demo")
        render()
    }

    data class Config(
        val mapSize: Size,
        val position: Int,
        val x: Int,
        val y: Int,
        val range: Int,
        val isBlocking: (position: Int) -> Boolean
    )

    fun createConfig(mapSize: Size, position: Int, range: Int, isBlocking: (position: Int) -> Boolean): Config {
        val (x, y) = mapSize.getPos(position)
        return Config(mapSize, position, x, y, range, isBlocking)
    }

    data class Slope(val x: Int, val y: Int)

    private fun calculateTopX(top: Slope, localX: Int) = if (top.x == 1) localX else
        ((localX * 2 + 1) * top.y + top.x - 1) / (top.x * 2)

    private fun calculateBottomX(bottom: Slope, localX: Int) = if (bottom.y == 0) 0 else
        ((localX * 2 - 1) * bottom.y + bottom.x) / (bottom.x * 2)

    private fun createSlopeAboveCurrent(x: Int, y: Int) = Slope(x * 2 - 1, y * 2 + 1)

    private fun createSlopeBelowPrevious(x: Int, y: Int) = Slope(x * 2 + 1, y * 2 + 1)

    enum class Status {
        UNDEFINED,
        BLOCKING,
        CLEAR;
    }

    private fun calculateShadowCastFieldOfView(config: Config): MutableSet<Int> {
        val visibleCells = mutableSetOf(config.position)

        val top = Slope(1, 1)
        val bottom = Slope(1, 0)

        Octant.values().forEach {
            calculateShadowCastFieldOfView(config, visibleCells, it, 1, top, bottom)
        }

        return visibleCells
    }

    private fun calculateShadowCastFieldOfView(
        config: Config,
        visibleCells: MutableSet<Int>,
        octant: Octant,
        startX: Int,
        parentTop: Slope,
        parentBottom: Slope
    ) {
        var top = parentTop
        var bottom = parentBottom
        logger.info("$octant startX=$startX top=$top bottom=$bottom")

        for (localX in startX until config.range) {
            val topY = calculateTopX(top, localX)
            val bottomY = calculateBottomX(bottom, localX)

            logger.info("x=$localX topY=$topY bottomY=$bottomY")

            var status = Status.UNDEFINED

            for (localY in topY downTo bottomY) {
                val (x, y) = octant.getGlobal(config.x, config.y, localX, localY)

                val index = config.mapSize.getIndex(x, y)
                visibleCells.add(index)

                val isBlocking = config.isBlocking(index)

                logger.info("  x=$localX y=$localY isBlocking=$isBlocking previous=$status")

                if (isBlocking) {
                    if (status == CLEAR) {
                        val newBottom = createSlopeAboveCurrent(localX, localY)

                        if (localY == bottomY) {
                            bottom = newBottom
                            break
                        } else calculateShadowCastFieldOfView(
                            config, visibleCells, octant,
                            localX + 1,
                            top,
                            newBottom
                        )
                    }
                    status = BLOCKING
                } else {
                    if (status == BLOCKING) top = createSlopeBelowPrevious(localX, localY)

                    status = CLEAR
                }
            }

            if (status != CLEAR) break
        }
    }

    private fun render() {
        logger.info("render()")

        renderer.clear()

        val config = createConfig(map.size, position, 10) { position -> !map.terrainList[position].isWalkable() }
        calculateShadowCastFieldOfView(config).forEach { renderNode(it, Color.GREEN) }
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
                render()
            }
        }
    }
}

fun main() {
    Application.launch(FieldOfViewDemo::class.java)
}