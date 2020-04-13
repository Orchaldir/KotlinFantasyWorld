package app.demo

import game.InitAction
import game.MoveAction
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.INIT_REDUCER
import game.reducer.MOVE_REDUCER
import game.renderEntities
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.ecs.EcsBuilder
import util.ecs.EcsState
import util.math.Direction.*
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.rendering.tile.UnicodeTile

private val logger = KotlinLogging.logger {}

val MOVEMENT_DEMO_REDUCER: Reducer<Any, EcsState> = { state, action ->
    when (action) {
        is InitAction -> INIT_REDUCER(state, action)
        is MoveAction -> MOVE_REDUCER(state, action)
        else -> state
    }
}

class MovementDemo : TileApplication(60, 40, 20, 20) {
    private lateinit var store: DefaultStore<Any, EcsState>
    private var entityId = 0

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Movement Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val gameMap = GameMapBuilder(size, Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .addRectangle(20, 10, 10, 10, Terrain.WALL)
            .addRectangle(40, 25, 10, 10, Terrain.WALL)
            .setTerrain(25, 19, Terrain.FLOOR)
            .build()

        val ecsState = with(EcsBuilder()) {
            addData(gameMap)
            registerComponent<Body>()
            registerComponent<Graphic>()
            add(SimpleBody(size.getIndex(10, 5)) as Body)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            buildEntity()
            add(BigBody(size.getIndex(10, 25), 4) as Body)
            add(Graphic(UnicodeTile("D", Color.RED)))
            buildEntity()
            add(
                SnakeBody(List(20) { size.getIndex(50, 5) }) as Body
            )
            add(Graphic(UnicodeTile("S", Color.GREEN)))
            buildEntity()
            build()
        }

        store = DefaultStore(ecsState, MOVEMENT_DEMO_REDUCER, listOf(::logAction))
        store.subscribe(this::render)
        store.dispatch(InitAction)
    }

    private fun render(state: EcsState) {
        logger.info("render()")

        renderer.clear()

        state.getData<GameMap>().render(tileRenderer, 0, 0)

        renderEntities(tileRenderer, size, state)

        logger.info("render(): finished")
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        logger.info("onKeyReleased(): keyCode=$keyCode")

        if (keyCode == KeyCode.UP) {
            store.dispatch(MoveAction(entityId, NORTH))
        } else if (keyCode == KeyCode.RIGHT) {
            store.dispatch(MoveAction(entityId, EAST))
        } else if (keyCode == KeyCode.DOWN) {
            store.dispatch(MoveAction(entityId, SOUTH))
        } else if (keyCode == KeyCode.LEFT) {
            store.dispatch(MoveAction(entityId, WEST))
        } else if (keyCode == KeyCode.F1) {
            entityId = 0
        } else if (keyCode == KeyCode.F2) {
            entityId = 1
        } else if (keyCode == KeyCode.F3) {
            entityId = 2
        }
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")
    }
}

fun main() {
    Application.launch(MovementDemo::class.java)
}