package app.demo

import app.demo.FieldOfViewDemo.Status.CLEAR
import app.demo.FieldOfViewDemo.Status.OPAQUE
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

    data class Slope(val x: Int, val y: Int)

    enum class Status {
        UNDEFINED,
        OPAQUE,
        CLEAR;
    }

    private fun calculateShadowCastFieldOfView(position: Int, range: Int): MutableSet<Int> {
        val visibleCells = mutableSetOf(position)
        val (x, y) = map.size.getPos(position)
        val top = Slope(1, 1)
        val bottom = Slope(1, 0)

        Octant.values().forEach {
            calculateShadowCastFieldOfView(visibleCells, it, x, y, range, 1, top, bottom)
        }

        return visibleCells
    }

    private fun calculateShadowCastFieldOfView(
        visibleCells: MutableSet<Int>,
        octant: Octant,
        originX: Int,
        originY: Int,
        range: Int,
        startX: Int,
        parentTop: Slope,
        parentBottom: Slope
    ) {
        var top = parentTop
        var bottom = parentBottom
        logger.info("$octant startX=$startX top=$top bottom=$bottom")

        for (localX in startX until range) {
            val topY = if (top.x == 1) localX else
                ((localX * 2 + 1) * top.y + top.x - 1) / (top.x * 2)
            val bottomY = if (bottom.y == 0) 0 else
                ((localX * 2 - 1) * bottom.y + bottom.x) / (bottom.x * 2)

            logger.info("x=$localX topY=$topY bottomY=$bottomY")

            var status = Status.UNDEFINED

            for (localY in topY downTo bottomY) {
                val (x, y) = octant.getGlobal(originX, originY, localX, localY)

                val index = map.size.getIndex(x, y)
                visibleCells.add(index)

                val isOpaque = !map.terrainList[index].isWalkable()

                logger.info("  x=$localX y=$localY isOpaque=$isOpaque status=$status")

                if (isOpaque) {
                    if (status == CLEAR) {
                        val newBottom = Slope(localX * 2 - 1, localY * 2 + 1)

                        if (localY == bottomY) {
                            bottom = newBottom
                            break
                        } else calculateShadowCastFieldOfView(
                            visibleCells, octant, originX, originY,
                            range,
                            localX + 1,
                            top,
                            newBottom
                        )
                    }
                    status = OPAQUE
                } else {
                    if (status == OPAQUE) top = Slope(localX * 2 + 1, localY * 2 + 1)

                    status = CLEAR
                }
            }

            if (status != CLEAR) break
        }
    }

    private fun render() {
        logger.info("render()")

        renderer.clear()

        calculateShadowCastFieldOfView(position, 10).forEach { renderNode(it, Color.GREEN) }
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