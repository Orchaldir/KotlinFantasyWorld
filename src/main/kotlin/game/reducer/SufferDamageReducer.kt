package game.reducer

import game.SufferDamageAction
import game.component.Health
import game.component.Statistics
import game.rpg.character.skill.Skill
import game.rpg.check.*
import util.ecs.EcsState
import util.ecs.storage.ComponentStorage
import util.redux.Reducer
import util.redux.random.RandomNumberState
import kotlin.reflect.KClass

fun createSufferDamageReducer(toughness: Skill): Reducer<SufferDamageAction, EcsState> = { state, action ->
    val healthStorage = state.getStorage<Health>()
    val health = healthStorage.getOrThrow(action.entity)

    val statisticsStorage = state.getStorage<Statistics>()
    val statistics = statisticsStorage.getOrThrow(action.entity)
    val toughnessRank = statistics.getRank(toughness)
    val difficulty = toughnessRank - health.penalty

    val rng = state.getData<RandomNumberState>().createGenerator()
    val checker = state.getData<Checker>()

    val result = checker.check(rng, action.damage.rank, difficulty)

    val updatedStorageMap: MutableMap<KClass<*>, ComponentStorage<*>> = mutableMapOf()
    val updatedDataMap: MutableMap<KClass<*>, Any> = mutableMapOf(RandomNumberState::class to rng.createState())

    val newHealth = updateHealth(health, result)

    if (health != newHealth) {
        val newHealthStorage = healthStorage.updateAndRemove(mapOf(action.entity to newHealth))
        updatedStorageMap[Health::class] = newHealthStorage
    }

    state.copy(updatedStorageMap, updatedDataMap)
}

private fun updateHealth(health: Health, result: CheckResult): Health {
    return when (result) {
        CriticalSuccess -> reduceHealthState(health, 2)
        is Success -> reduceHealthState(health, 1)
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
