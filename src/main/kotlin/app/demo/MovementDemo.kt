package app.demo

import game.GameRenderer
import game.action.Action
import game.action.FinishTurn
import game.action.Init
import game.action.Move
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.FINISH_TURN_REDUCER
import game.reducer.INIT_REDUCER
import game.reducer.MOVE_REDUCER
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.character.skill.SkillUsage
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.ecs.EcsBuilder
import util.ecs.EcsState
import util.log.MessageLog
import util.log.MessageLogRenderer
import util.math.Direction.*
import util.math.Size
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.rendering.tile.UnicodeTile

private val logger = KotlinLogging.logger {}

private const val LOG_SIZE = 5
private const val STATUS_SIZE = 1

class MovementDemo : TileApplication(60, 45, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Movement Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val speed = Skill("Speed")
        val skillManager = SkillManager(listOf(speed))
        val skillUsage = SkillUsage(speed = speed)

        val gameMap = GameMapBuilder(Size(size.x, size.y - LOG_SIZE - STATUS_SIZE), Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .addRectangle(20, 10, 10, 10, Terrain.WALL)
            .addRectangle(40, 25, 10, 10, Terrain.WALL)
            .setTerrain(25, 19, Terrain.FLOOR)
            .build()

        val ecsState = with(EcsBuilder()) {
            addData(gameMap)
            addData(MessageLog())
            addData(skillManager)
            addData(skillUsage)
            addData(TimeSystem())
            registerComponent<Body>()
            registerComponent<Controller>()
            registerComponent<Graphic>()
            registerComponent<Health>()
            registerComponent<Statistics>()
            add(SimpleBody(gameMap.size.getIndex(10, 5)) as Body)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            add(Player as Controller)
            add(Statistics(mapOf(speed to 6)))
            buildEntity()
            add(BigBody(gameMap.size.getIndex(10, 25), 4) as Body)
            add(Graphic(UnicodeTile("D", Color.RED)))
            add(Player as Controller)
            add(Statistics(mapOf(speed to 4)))
            buildEntity()
            add(
                SnakeBody(List(20) { gameMap.size.getIndex(50, 5) }) as Body
            )
            add(Graphic(UnicodeTile("S", Color.GREEN)))
            add(Player as Controller)
            add(Statistics(mapOf(speed to 2)))
            buildEntity()
            build()
        }

        val reducer: Reducer<Action, EcsState> = { state, action ->
            when (action) {
                is FinishTurn -> FINISH_TURN_REDUCER(state, action)
                is Init -> INIT_REDUCER(state, action)
                is Move -> MOVE_REDUCER(state, action)
                else -> state
            }
        }

        store = DefaultStore(ecsState, reducer, listOf(::logAction))
        store.subscribe(this::render)
        store.dispatch(Init)
    }

    private fun render(state: EcsState) {
        logger.info("render()")

        renderer.clear()

        val map = state.getData<GameMap>()
        val mapRender = GameRenderer(0, LOG_SIZE, map.size)
        mapRender.renderMap(tileRenderer, map)
        mapRender.renderEntities(tileRenderer, state)

        val messageLogRenderer = MessageLogRenderer(0, 0, Size(size.x, LOG_SIZE))
        messageLogRenderer.render(tileRenderer, state.getData())

        val timeSystem = state.getData<TimeSystem>()
        val turnData = state.getData<TurnData>()

        val text = getStatusText(timeSystem, turnData)

        tileRenderer.renderText(
            text,
            Color.WHITE,
            0,
            size.y - 1
        )

        logger.info("render(): finished")
    }

    private fun getStatusText(timeSystem: TimeSystem, turnData: TurnData): String {
        val entity = timeSystem.getCurrent()
        return "Turn=${timeSystem.turn} Entity=$entity " + if (turnData.isFinished()) {
            "Press space to finish turn"
        } else {
            "Movement=${turnData.movementPoints}/${turnData.maxMovementPoints}"
        }
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        val entity = store.getState().getData<TimeSystem>().getCurrent()

        when (keyCode) {
            KeyCode.UP -> store.dispatch(Move(entity, NORTH))
            KeyCode.RIGHT -> store.dispatch(Move(entity, EAST))
            KeyCode.DOWN -> store.dispatch(Move(entity, SOUTH))
            KeyCode.LEFT -> store.dispatch(Move(entity, WEST))
            KeyCode.SPACE -> store.dispatch(FinishTurn(entity))
            else -> logger.info("onKeyReleased(): keyCode=$keyCode")
        }
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")
    }
}

fun main() {
    Application.launch(MovementDemo::class.java)
}