package game.reducer

import game.SufferDamageAction
import game.component.Health
import game.component.HealthState
import game.component.Statistics
import game.rpg.character.skill.Skill
import game.rpg.check.*
import mu.KotlinLogging
import util.ecs.EcsState
import util.ecs.storage.ComponentStorage
import util.redux.Reducer
import util.redux.random.RandomNumberState
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

fun createSufferDamageReducer(toughness: Skill): Reducer<SufferDamageAction, EcsState> = a@{ state, action ->
    val id = action.entity

    val healthStorage = state.getStorage<Health>()
    val health = healthStorage.getOrThrow(id)

    if (health.state == HealthState.DEAD) {
        logger.info("Entity $id is already dead!")
        return@a state
    }

    val statisticsStorage = state.getStorage<Statistics>()
    val statistics = statisticsStorage.getOrThrow(id)
    val toughnessRank = statistics.getRank(toughness)
    val difficulty = toughnessRank - health.penalty

    val rng = state.getData<RandomNumberState>().createGenerator()
    val checker = state.getData<Checker>()

    val result = checker.check(rng, action.damage.rank, difficulty)

    logger.info("entity=$id toughness=$toughnessRank $health result=$result")

    val updatedStorageMap: MutableMap<KClass<*>, ComponentStorage<*>> = mutableMapOf()
    val updatedDataMap: MutableMap<KClass<*>, Any> = mutableMapOf(RandomNumberState::class to rng.createState())

    val newHealth = updateHealth(health, result)

    if (health != newHealth) {
        logger.info("Change: $newHealth")
        val newHealthStorage = healthStorage.updateAndRemove(mapOf(id to newHealth))
        updatedStorageMap[Health::class] = newHealthStorage
    }

    state.copy(updatedStorageMap, updatedDataMap)
}

private fun updateHealth(health: Health, result: CheckResult): Health {
    return when (result) {
        CriticalSuccess -> reduceHealthState(health, 2)
        is Success -> reduceHealthState(
            health, if (result.rank >= 5) {
                2
            } else {
                1
            }
        )
        Draw -> increaseHealthPenalty(health)
        is Failure -> increaseHealthPenalty(health)
        CriticalFailure -> health
    }
}

private fun increaseHealthPenalty(health: Health) = health.copy(penalty = health.penalty + 1)

private fun reduceHealthState(health: Health, steps: Int): Health {
    val newHealthState = health.state.getWorse(steps)
    return health.copy(state = newHealthState, penalty = health.penalty + 1)
}
