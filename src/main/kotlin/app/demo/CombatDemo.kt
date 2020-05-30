package app.demo

import ai.behavior.bt.Blackboard
import ai.behavior.bt.PerformAction
import ai.behavior.bt.Success
import ai.behavior.bt.composite.SequenceBehavior
import ai.pathfinding.AStar
import ai.pathfinding.NotSearched
import ai.pathfinding.Path
import ai.pathfinding.PathfindingResult
import game.GameRenderer
import game.InvalidAbilityUsageException
import game.action.*
import game.behavior.bt.FindGoalsBehavior
import game.behavior.bt.MoveToGoalBehavior
import game.behavior.bt.UseAbilityBehavior
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.action.*
import game.reducer.event.ON_DAMAGE_REDUCER
import game.reducer.event.ON_DEATH_REDUCER
import game.rpg.Damage
import game.rpg.character.Defense
import game.rpg.character.ability.*
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.character.skill.SkillUsage
import game.rpg.check.Checker
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
import util.log.Message
import util.log.MessageLog
import util.log.MessageLogRenderer
import util.math.Direction.*
import util.math.rectangle.Chebyshev
import util.math.rectangle.DistanceCalculator
import util.math.rectangle.Size
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.rendering.tile.ImageTile
import kotlin.random.Random
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

private const val LOG_SIZE = 5
private const val STATUS_SIZE = 1

class CombatDemo : TileApplication(60, 45, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>
    private lateinit var mapRender: GameRenderer

    private val pathfinding = AStar<Boolean>()
    private var pathfindingResult: PathfindingResult = NotSearched

    private val behavior = SequenceBehavior(FindGoalsBehavior(), MoveToGoalBehavior(), UseAbilityBehavior())

    private var selectedAbility: Int? = null
    private var abilityCheckResult: AbilityCheckResult = NoTarget

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Combat Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val paladinImage = renderer.loadImage("tiles\\paladin.png")
        val skeletonImage = renderer.loadImage("tiles\\skeleton.png")

        val divineMagic = Skill("Divine Magic")
        val fighting = Skill("Fighting")
        val speed = Skill("Speed")
        val toughness = Skill("Toughness")
        val skillManager = SkillManager(listOf(divineMagic, fighting, speed, toughness))
        val skillUsage = SkillUsage(speed = speed, toughness = toughness)

        val meleeAttack = MeleeAttack(fighting, 1, DamageEffect(Damage(10)))
        val holyBolt = RangedAttack(divineMagic, 5, 10, 20, DamageEffect(Damage(5)))
        val defense = Defense(fighting, 0)

        val gameMap = GameMapBuilder(Size(size.x, size.y - LOG_SIZE - STATUS_SIZE), Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .build()

        mapRender = GameRenderer(0, LOG_SIZE, gameMap.size)

        val ecsState = with(EcsBuilder()) {
            addData(Chebyshev as DistanceCalculator)
            addData(gameMap)
            addData(MessageLog())
            addData(skillManager)
            addData(skillUsage)
            addData(TimeSystem())
            addData(util.redux.random.init(Random, 100))
            addData(Checker(6))
            registerComponent<Body>()
            registerComponent<Combat>()
            registerComponent<Controller>()
            registerComponent<Graphic>()
            registerComponent<Health>()
            registerComponent<Statistics>()
            registerComponent<Text>()
            add(BigBody(gameMap.size.getIndex(10, 5), 2, SOUTH) as Body)
            add(Combat(listOf(meleeAttack, holyBolt), defense))
            add(Player as Controller)
            add(Graphic(ImageTile(paladinImage)))
            add(Health())
            add(Statistics(mapOf(divineMagic to 5, fighting to 8, speed to 8, toughness to 6)))
            add(Description("Paladin") as Text)
            buildEntity()
            add(BigBody(gameMap.size.getIndex(15, 10), 2, WEST) as Body)
            add(Combat(listOf(meleeAttack), defense))
            add(AI as Controller)
            add(Graphic(ImageTile(skeletonImage)))
            add(Health())
            add(Statistics(mapOf(fighting to 6, speed to 6, toughness to 8)))
            add(Description("Skeleton") as Text)
            buildEntity()
            build()
        }

        val reducer: Reducer<Action, EcsState> = { state, action ->
            when (action) {
                is AddMessage -> ADD_MESSAGE_REDUCER(state, action)
                is FinishTurn -> FINISH_TURN_REDUCER(state, action)
                is FollowPath -> FOLLOW_PATH_REDUCER(state, action)
                is Init -> INIT_REDUCER(state, action)
                is Move -> MOVE_REDUCER(state, action)
                is OnDamage -> ON_DAMAGE_REDUCER(state, action)
                is OnDeath -> ON_DEATH_REDUCER(state, action)
                is UseAbility -> USE_ABILITY_REDUCER(state, action)
            }
        }

        store = DefaultStore(ecsState, reducer, listOf(::logAction))
        store.subscribe(this::update)
        store.dispatch(Init)
    }

    private fun update(state: EcsState) {
        val entity = getCurrentEntity(state)

        val controller = state.getStorage<Controller>()[entity]

        if (controller is AI) {
            logger.info("AI for entity $entity: {}", getText(state, entity))
            val blackboard = Blackboard()
            blackboard.put("self", entity)
            blackboard.put("ability", 0)
            blackboard.put("target", 0)

            when (val status = behavior.execute(state, blackboard)) {
                is PerformAction -> store.dispatch(status.action)
                is Success -> store.dispatch(FinishTurn(entity))
                else -> logger.info("Result of behavior is $status")
            }
        } else {
            render(state)
        }
    }

    private fun render(state: EcsState) {
        logger.debug("render()")

        renderer.clear()

        val map = state.getData<GameMap>()
        renderAction(state, selectedAbility)
        mapRender.renderMap(tileRenderer, map)
        mapRender.renderEntities(tileRenderer, state, true)

        val messageLogRenderer = MessageLogRenderer(0, 0, Size(size.x, LOG_SIZE))
        messageLogRenderer.render(tileRenderer, state.getData())

        val timeSystem = state.getData<TimeSystem>()
        val turnData = state.getData<TurnData>()
        val entity = timeSystem.getCurrent()
        val health = state.getStorage<Health>().getOrThrow(entity)

        tileRenderer.renderText(
            getStatusText(timeSystem, health, turnData),
            Color.WHITE,
            0,
            size.y - 1
        )

        logger.debug("render(): finished")
    }

    private fun renderAction(state: EcsState, selectedAbility: Int?) {
        if (selectedAbility == null) {
            val movementPoints = getCurrentMovementPoints(state)
            mapRender.renderPathfindingResult(tileRenderer, pathfindingResult, movementPoints)
        } else renderAbility(abilityCheckResult)
    }

    private fun renderAbility(result: AbilityCheckResult) {
        when (result) {
            is NoActionPoints -> mapRender.renderError(tileRenderer, result.position)
            is OutOfRange -> mapRender.renderError(tileRenderer, result.position)
            is ValidUsage -> mapRender.renderSuccess(tileRenderer, result.position)
        }
    }

    private fun getStatusText(
        timeSystem: TimeSystem,
        health: Health,
        turnData: TurnData
    ): String {
        return "Turn=${timeSystem.turn} " +
                "Health=${health.state.toDisplayText()} " +
                "Movement=${turnData.movementPoints} " +
                "Actions=${turnData.actionPoints}"
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        val state = store.getState()
        val entity = getCurrentEntity(state)

        pathfindingResult = NotSearched

        when (keyCode) {
            KeyCode.UP -> store.dispatch(Move(entity, NORTH))
            KeyCode.RIGHT -> store.dispatch(Move(entity, EAST))
            KeyCode.DOWN -> store.dispatch(Move(entity, SOUTH))
            KeyCode.LEFT -> store.dispatch(Move(entity, WEST))
            KeyCode.SPACE -> {
                selectedAbility = null
                store.dispatch(FinishTurn(entity))
            }
            KeyCode.DIGIT1 -> selectedAbility = getAbilityIndexOrNull(state, entity, 0)
            KeyCode.DIGIT2 -> selectedAbility = getAbilityIndexOrNull(state, entity, 1)
            KeyCode.DIGIT3 -> selectedAbility = getAbilityIndexOrNull(state, entity, 2)
            KeyCode.ESCAPE -> exitProcess(0)
            else -> logger.debug("onKeyReleased(): keyCode=$keyCode")
        }
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.debug("onTileClicked(): x=$x y=$y")

        if (mapRender.area.isInside(x, y)) {
            val state = store.getState()
            val position = mapRender.area.convert(x, y)
            val target = state.getData<GameMap>().entities[position]

            if (target != null && selectedAbility != null) {
                abilityCheckResult = NoTarget
                val entity = getCurrentEntity(state)
                try {
                    store.dispatch(UseAbility(entity, selectedAbility!!, position))
                } catch (e: InvalidAbilityUsageException) {
                    store.dispatch(AddMessage(Message(e.error.getText(), Color.YELLOW)))
                }
            } else {
                pathfindingResult = updatePath(x, y)
                usePath(pathfindingResult)
            }
        } else {
            abilityCheckResult = NoTarget
            pathfindingResult = NotSearched
        }
    }

    override fun onMouseMoved(x: Int, y: Int) {
        logger.debug("onMouseMoved(): x=$x y=$y")

        if (selectedAbility == null) {
            pathfindingResult = updatePath(x, y)
        } else {
            abilityCheckResult = checkAbility(selectedAbility!!, x, y)
        }

        render(store.getState())
    }

    private fun checkAbility(index: Int, x: Int, y: Int): AbilityCheckResult {
        val state = store.getState()
        val entity = getCurrentEntity(state)
        val ability = getAbility(state, entity, index)

        return if (mapRender.area.isInside(x, y)) {
            checkAbility(state, ability, entity, mapRender.area.convert(x, y))
        } else NoTarget
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
            val gameMap = state.getData<GameMap>()
            val goalEntity = gameMap.entities[goal]
            val occupancyMap = gameMap.createOccupancyMap(distanceCalculator, entitySize, entity)

            if (goalEntity == null) {
                pathfinding.find(occupancyMap, start, goal, entitySize)
            } else {
                val bodySize = getSize(body)
                val goalBody = state.getStorage<Body>()[goalEntity]!!
                val goals = getPositionsAround(distanceCalculator, gameMap.size, goalBody, bodySize, 1)
                pathfinding.find(occupancyMap, start, goals, entitySize)
            }
        } else NotSearched
    }

    private fun usePath(result: PathfindingResult) {
        if (result is Path) {
            pathfindingResult = NotSearched
            store.dispatch(FollowPath(getCurrentEntity(store.getState()), result))
        }
    }
}

fun main() {
    Application.launch(CombatDemo::class.java)
}