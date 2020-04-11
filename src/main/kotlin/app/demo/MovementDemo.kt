package app.demo

import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.ecs.EcsBuilder
import util.ecs.EcsState
import util.math.Size
import util.rendering.tile.UnicodeTile

private val logger = KotlinLogging.logger {}

class MovementDemo : TileApplication() {
    private lateinit var ecsState: EcsState

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Movement Demo", 60, 40, 20, 20)
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", tiles)

        val size = Size(columns, rows)
        val gameMap = GameMapBuilder(size, Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .addRectangle(20, 10, 10, 10, Terrain.WALL)
            .addRectangle(40, 25, 10, 10, Terrain.WALL)
            .setTerrain(25, 19, Terrain.FLOOR)
            .build()

        ecsState = with(EcsBuilder()) {
            addData(gameMap)
            register<Body>()
            register<Graphic>()
            add(SimpleBody(size.getIndex(10, 5)) as Body)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            buildEntity()
            add(BigBody(size.getIndex(10, 25), 4) as Body)
            add(Graphic(UnicodeTile("D", Color.RED)))
            buildEntity()
            add(
                SnakeBody(
                    listOf(
                        size.getIndex(49, 5),
                        size.getIndex(50, 5),
                        size.getIndex(50, 6),
                        size.getIndex(50, 7),
                        size.getIndex(50, 8)
                    )
                ) as Body
            )
            add(Graphic(UnicodeTile("S", Color.GREEN)))
            buildEntity()
            build()
        }

        render()
    }

    private fun render() {
        logger.info("render()")

        ecsState.getData<GameMap>()?.render(tileRenderer, 0, 0)

        renderEntities()

        logger.info("render(): finished")
    }

    private fun renderEntities() {
        val size = Size(columns, rows)
        val bodyStore = ecsState.get<Body>()
        val graphicStore = ecsState.get<Graphic>()

        for (entityId in ecsState.entityIds) {
            val body = bodyStore!![entityId]
            val graphic = graphicStore!![entityId]

            if (body != null && graphic != null) {
                when (body) {
                    is SimpleBody -> tileRenderer.renderTile(
                        graphic.get(0),
                        size.getX(body.position),
                        size.getY(body.position)
                    )
                    is BigBody -> tileRenderer.renderTile(
                        graphic.get(0),
                        size.getX(body.position),
                        size.getY(body.position),
                        body.size
                    )
                    is SnakeBody -> for (pos in body.positions) {
                        tileRenderer.renderTile(graphic.get(0), size.getX(pos), size.getY(pos))
                    }
                }
            }
        }
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