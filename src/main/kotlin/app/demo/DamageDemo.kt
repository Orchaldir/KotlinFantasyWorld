package app.demo

import game.GameRenderer
import game.action.Action
import game.action.Init
import game.action.OnDamage
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.action.INIT_REDUCER
import game.reducer.event.ON_DAMAGE_REDUCER
import game.rpg.Damage
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.character.skill.SkillUsage
import game.rpg.check.Checker
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
import util.math.Direction.WEST
import util.math.rectangle.Size
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.redux.noFollowUps
import util.rendering.tile.UnicodeTile
import kotlin.random.Random
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}
private const val MAP_X = 10

class DamageDemo : TileApplication(60, 40, 20, 20) {
    private lateinit var store: DefaultStore<Action, EcsState>

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Damage Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val gameMap = GameMapBuilder(Size(MAP_X, size.y), Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .build()

        val toughness = Skill("Toughness")
        val skillManager = SkillManager(listOf(toughness))
        val skillUsage = SkillUsage(toughness = toughness)

        val ecsState = with(EcsBuilder()) {
            addData(gameMap)
            addData(MessageLog())
            addData(skillManager)
            addData(skillUsage)
            addData(util.redux.random.init(Random, 100))
            addData(Checker(6))
            registerComponent<Body>()
            registerComponent<Graphic>()
            registerComponent<Statistics>()
            registerComponent<Health>()
            registerComponent<Text>()
            listOf("very weak", "weak", "average", "strong", "very strong").forEachIndexed { i, s ->
                add(SimpleBody(gameMap.size.getIndex(5, 5 + 5 * i), WEST) as Body)
                add(Graphic(UnicodeTile("O", Color.DARKGREEN)))
                add(Statistics(mapOf(toughness to (3 * i))))
                add(Health())
                add(Description("$s orc") as Text)
                buildEntity()
            }
            build()
        }

        val reducer: Reducer<Action, EcsState> = { state, action ->
            when (action) {
                is Init -> INIT_REDUCER(state, action)
                is OnDamage -> ON_DAMAGE_REDUCER(state, action)
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
        val mapRender = GameRenderer(map.size)
        mapRender.renderMap(tileRenderer, map)
        mapRender.renderEntities(tileRenderer, state)

        val messageLogRenderer = MessageLogRenderer(MAP_X + 1, 0, Size(size.x - MAP_X, size.y))
        messageLogRenderer.render(tileRenderer, state.getData())

        logger.info("render(): finished")
    }

    override fun onKeyReleased(keyCode: KeyCode) {
        when (keyCode) {
            KeyCode.ESCAPE -> exitProcess(0)
            else -> logger.info("onKeyReleased(): keyCode=$keyCode")
        }
    }

    override fun onTileClicked(x: Int, y: Int, button: MouseButton) {
        logger.info("onTileClicked(): x=$x y=$y")

        val state = store.getState()
        val entity = state.getData<GameMap>().getEntity(x, y)

        if (entity != null) {
            store.dispatch(OnDamage(entity, Damage(5)))
        }
    }
}

fun main() {
    Application.launch(DamageDemo::class.java)
}