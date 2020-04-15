package app.demo

import game.InitAction
import game.SufferDamageAction
import game.component.*
import game.map.GameMap
import game.map.GameMapBuilder
import game.map.Terrain
import game.reducer.INIT_REDUCER
import game.reducer.createSufferDamageReducer
import game.renderEntities
import game.rpg.Damage
import game.rpg.character.skill.Skill
import game.rpg.character.skill.SkillManager
import game.rpg.check.Checker
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import mu.KotlinLogging
import util.app.TileApplication
import util.ecs.EcsBuilder
import util.ecs.EcsState
import util.redux.DefaultStore
import util.redux.Reducer
import util.redux.middleware.logAction
import util.rendering.tile.UnicodeTile
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

class DamageDemo : TileApplication(60, 40, 20, 20) {
    private lateinit var store: DefaultStore<Any, EcsState>

    override fun start(primaryStage: Stage) {
        init(primaryStage, "Damage Demo")
        create()
    }

    private fun create() {
        logger.info("create(): tiles={}", size.cells)

        val gameMap = GameMapBuilder(size, Terrain.FLOOR)
            .addBorder(Terrain.WALL)
            .build()

        val toughness = Skill("Toughness")
        val skillManager = SkillManager(listOf(toughness))


        val ecsState = with(EcsBuilder()) {
            addData(gameMap)
            addData(skillManager)
            addData(util.redux.random.init(Random, 100))
            addData(Checker(6))
            registerComponent<Body>()
            registerComponent<Graphic>()
            registerComponent<Statistics>()
            registerComponent<Health>()
            repeat(5) {
                add(SimpleBody(size.getIndex(5 + 5 * it, 5)) as Body)
                add(Graphic(UnicodeTile("O", Color.DARKGREEN)))
                add(Statistics(mapOf(toughness to (3 * it))))
                add(Health())
                buildEntity()
            }
            build()
        }

        val sufferDamageReducer = createSufferDamageReducer(toughness)

        val reducer: Reducer<Any, EcsState> = { state, action ->
            when (action) {
                is InitAction -> INIT_REDUCER(state, action)
                is SufferDamageAction -> sufferDamageReducer(state, action)
                else -> state
            }
        }

        store = DefaultStore(ecsState, reducer, listOf(::logAction))
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
    }

    override fun onTileClicked(x: Int, y: Int) {
        logger.info("onTileClicked(): x=$x y=$y")

        val state = store.getState()
        val entity = state.getData<GameMap>().entities[size.getIndex(x, y)]

        if (entity != null) {
            store.dispatch(SufferDamageAction(entity, Damage(5)))
        }
    }
}

fun main() {
    Application.launch(DamageDemo::class.java)
}