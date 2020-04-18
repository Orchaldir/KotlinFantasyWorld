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

class MeleeCombatDemo : TileApplication(60, 45, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Melee Combat Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val fighting = Skill("Fighting")
        val speed = Skill("Speed")
        val toughness = Skill("Toughness")
        val skillManager = SkillManager(listOf(fighting, speed, toughness))
        val skillUsage = SkillUsage(speed = speed, toughness = toughness)

        val gameMap = GameMapBuilder(Size(size.x, size.y - LOG_SIZE - STATUS_SIZE), Terrain.FLOOR)
            .addBorder(Terrain.WALL)
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
            add(Player as Controller)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            add(Health())
            add(Statistics(mapOf(fighting to 8, speed to 6, toughness to 6)))
            buildEntity()
            add(SimpleBody(gameMap.size.getIndex(15, 10)) as Body)
            add(AI as Controller)
            add(Graphic(UnicodeTile("O", Color.GREEN)))
            add(Health())
            add(Statistics(mapOf(fighting to 6, speed to 6, toughness to 8)))
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
        val entity = timeSystem.entities.first()
        val health = state.getStorage<Health>().getOrThrow(entity)

        val text = getStatusText(timeSystem, health, turnData)

        tileRenderer.renderText(
            text,
            Color.WHITE,
            0,
            size.y - 1
        )

        logger.info("render(): finished")
    }

    private fun getStatusText(
        timeSystem: TimeSystem,
        health: Health,
        turnData: TurnData
    ): String {
        return "Turn=${timeSystem.turn} " +
                "Health=${health.state.toDisplayText()} " +
                "Movement=${turnData.movementPoints} " +
                "Actions=${turnData.actionsPerTurn}"
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        val entityId = store.getState().getData<TimeSystem>().entities.first()

        when (keyCode) {
            KeyCode.UP -> store.dispatch(Move(entityId, NORTH))
            KeyCode.RIGHT -> store.dispatch(Move(entityId, EAST))
            KeyCode.DOWN -> store.dispatch(Move(entityId, SOUTH))
            KeyCode.LEFT -> store.dispatch(Move(entityId, WEST))
            KeyCode.SPACE -> store.dispatch(FinishTurn(entityId))
            else -> logger.info("onKeyReleased(): keyCode=$keyCode")
        }
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")
    }
}

fun main() {
    Application.launch(MeleeCombatDemo::class.java)
}