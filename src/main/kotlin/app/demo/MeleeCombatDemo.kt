package app.demo

import game.CannotTargetSelf
import game.GameRenderer
import game.NoActionPointsException
import game.OutOfRangeException
import game.action.*
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.action.*
import game.reducer.event.ON_DAMAGE_REDUCER
import game.reducer.event.ON_DEATH_REDUCER
import game.rpg.Damage
import game.rpg.character.Defense
import game.rpg.character.ability.DamageEffect
import game.rpg.character.ability.MeleeAttack
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.character.skill.SkillUsage
import game.rpg.check.Checker
import game.rpg.time.TimeSystem
import game.rpg.time.TurnData
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
import util.math.Size
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.rendering.tile.UnicodeTile
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

private const val LOG_SIZE = 5
private const val STATUS_SIZE = 1

class MeleeCombatDemo : TileApplication(60, 45, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>
    private lateinit var mapRender: GameRenderer

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

        val meleeAttack = MeleeAttack(fighting, 1, DamageEffect(Damage(5)))
        val defense = Defense(fighting, 0)

        val gameMap = GameMapBuilder(Size(size.x, size.y - LOG_SIZE - STATUS_SIZE), Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .build()

        mapRender = GameRenderer(0, LOG_SIZE, gameMap.size)

        val ecsState = with(EcsBuilder()) {
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
            add(SimpleBody(gameMap.size.getIndex(10, 5)) as Body)
            add(Combat(listOf(meleeAttack), defense))
            add(Player as Controller)
            add(Graphic(UnicodeTile("@", Color.BLUE)))
            add(Health())
            add(Statistics(mapOf(fighting to 8, speed to 6, toughness to 6)))
            buildEntity()
            add(SimpleBody(gameMap.size.getIndex(15, 10)) as Body)
            add(Combat(listOf(meleeAttack), defense))
            add(AI as Controller)
            add(Graphic(UnicodeTile("O", Color.GREEN)))
            add(Health())
            add(Statistics(mapOf(fighting to 6, speed to 6, toughness to 8)))
            buildEntity()
            build()
        }

        val reducer: Reducer<Action, EcsState> = { state, action ->
            when (action) {
                is AddMessage -> ADD_MESSAGE_REDUCER(state, action)
                is FinishTurn -> FINISH_TURN_REDUCER(state, action)
                is Init -> INIT_REDUCER(state, action)
                is Move -> MOVE_REDUCER(state, action)
                is OnDamage -> ON_DAMAGE_REDUCER(state, action)
                is OnDeath -> ON_DEATH_REDUCER(state, action)
                is UseAbility -> USE_ABILITY_REDUCER(state, action)
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
        mapRender.renderMap(tileRenderer, map)
        mapRender.renderEntities(tileRenderer, state)

        val messageLogRenderer = MessageLogRenderer(0, 0, Size(size.x, LOG_SIZE))
        messageLogRenderer.render(tileRenderer, state.getData())

        val timeSystem = state.getData<TimeSystem>()
        val turnData = state.getData<TurnData>()
        val entity = timeSystem.getCurrent()
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
                "Actions=${turnData.actionPoints}"
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

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y")

        if (mapRender.area.isInside(x, y)) {
            val state = store.getState()
            val position = mapRender.area.convert(x, y)
            val target = state.getData<GameMap>().entities[position]

            if (target != null) {
                val entity = store.getState().getData<TimeSystem>().getCurrent()
                try {
                    store.dispatch(UseAbility(entity, 0, position))
                } catch (e: OutOfRangeException) {
                    store.dispatch(AddMessage(Message("Target is out of range!", Color.YELLOW)))
                } catch (e: NoActionPointsException) {
                    store.dispatch(AddMessage(Message("No Action points!", Color.YELLOW)))
                } catch (e: CannotTargetSelf) {
                    store.dispatch(AddMessage(Message("Cannot target self!", Color.YELLOW)))
                }
            }
        }
    }
}

fun main() {
    Application.launch(MeleeCombatDemo::class.java)
}