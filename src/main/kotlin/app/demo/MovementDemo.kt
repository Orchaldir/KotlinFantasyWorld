package app.demo

import ai.pathfinding.AStar
import ai.pathfinding.NotSearched
import ai.pathfinding.Path
import ai.pathfinding.PathfindingResult
import game.GameRenderer
import game.action.*
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.action.FINISH_TURN_REDUCER
import game.reducer.action.FOLLOW_PATH_REDUCER
import game.reducer.action.INIT_REDUCER
import game.reducer.action.MOVE_REDUCER
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.character.skill.SkillUsage
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
import game.rpg.time.getCurrentEntity
import game.rpg.time.getCurrentMovementPoints
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.ecs.EcsBuilder
import util.ecs.EcsState
import util.log.MessageLog
import util.log.MessageLogRenderer
import util.math.Direction.*
import util.math.rectangle.Chebyshev
import util.math.rectangle.DistanceCalculator
import util.math.rectangle.Size
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.redux.noFollowUps
import util.rendering.tile.UnicodeTile
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

private const val LOG_SIZE = 5
private const val STATUS_SIZE = 1

class MovementDemo : TileApplication(60, 45, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>

    private val mapRender = GameRenderer(0, LOG_SIZE, Size(size.x, size.y - LOG_SIZE - STATUS_SIZE))

    private val pathfinding = AStar<Boolean>()
    private var pathfindingResult: PathfindingResult = NotSearched

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Movement Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val speed = Skill("Speed")
        val skillManager = SkillManager(listOf(speed))
        val skillUsage = SkillUsage(speed = speed)

        val gameMap = GameMapBuilder(mapRender.area.size, Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .addRectangle(20, 10, 10, 10, Terrain.WALL)
            .addRectangle(40, 25, 10, 10, Terrain.WALL)
            .setTerrain(25, 19, Terrain.FLOOR)
            .build()

        val ecsState = with(EcsBuilder()) {
            addData(Chebyshev as DistanceCalculator)
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
            add(SimpleBody(gameMap.size.getIndex(10, 5), SOUTH) as Body)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            add(Player as Controller)
            add(Statistics(mapOf(speed to 6)))
            buildEntity()
            add(BigBody(gameMap.size.getIndex(10, 25), 4, NORTH) as Body)
            add(Graphic(UnicodeTile("D", Color.RED)))
            add(Player as Controller)
            add(Statistics(mapOf(speed to 4)))
            buildEntity()
            add(
                SnakeBody(List(20) { gameMap.size.getIndex(50, 5) }, EAST) as Body
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
                is FollowPath -> FOLLOW_PATH_REDUCER(state, action)
                is Init -> INIT_REDUCER(state, action)
                is Move -> MOVE_REDUCER(state, action)
                else -> noFollowUps(state)
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
        mapRender.renderPathfindingResult(tileRenderer, pathfindingResult, getCurrentMovementPoints(state))
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
        val entity = getCurrentEntity(store.getState())

        when (keyCode) {
            KeyCode.UP -> store.dispatch(Move(entity, NORTH))
            KeyCode.RIGHT -> store.dispatch(Move(entity, EAST))
            KeyCode.DOWN -> store.dispatch(Move(entity, SOUTH))
            KeyCode.LEFT -> store.dispatch(Move(entity, WEST))
            KeyCode.SPACE -> store.dispatch(FinishTurn(entity))
            KeyCode.ESCAPE -> exitProcess(0)
            else -> logger.info("onKeyReleased(): keyCode=$keyCode")
        }

        pathfindingResult = NotSearched
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y")
        pathfindingResult = updatePath(x, y)
        usePath(pathfindingResult)
        render(store.getState())
    }

    override fun onMouseMoved(x: Int, y: Int) {
        logger.info("onMouseMoved(): x=$x y=$y")
        pathfindingResult = updatePath(x, y)
        render(store.getState())
    }

    private fun updatePath(x: Int, y: Int): PathfindingResult {
        val state = store.getState()
        val entity = getCurrentEntity(state)
        val body = state.getStorage<Body>()[entity]!!
        val entitySize = getSize(body)

        return if (mapRender.area.isAreaInside(x, y, entitySize)) {
            val goal = mapRender.area.convert(x, y)
            val start = getPosition(body)
            val distanceCalculator = state.getData<DistanceCalculator>()
            val occupancyMap = state.getData<GameMap>().createOccupancyMap(distanceCalculator, entitySize, entity)

            pathfinding.find(occupancyMap, start, goal, entitySize)
        } else NotSearched
    }

    private fun usePath(result: PathfindingResult) {
        if (result is Path) {
            store.dispatch(FollowPath(getCurrentEntity(store.getState()), result))
            pathfindingResult = NotSearched
        }
    }
}

fun main() {
    Application.launch(MovementDemo::class.java)
}