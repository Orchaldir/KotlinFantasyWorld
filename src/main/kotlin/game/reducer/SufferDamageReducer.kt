package game.reducer

import game.SufferDamageAction
import game.component.Health
import game.component.HealthState
import game.component.Statistics
import game.rpg.character.skill.SkillUsage
import game.rpg.check.*
import javafx.scene.paint.Color
import mu.KotlinLogging
import util.ecs.EcsState
import util.ecs.storage.ComponentStorage
import util.log.Message
import util.log.MessageLog
import util.log.addMessage
import util.redux.Reducer
import util.redux.random.RandomNumberState
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

val SUFFER_DAMAGE_REDUCER: Reducer<SufferDamageAction, EcsState> = a@{ state, action ->
    val id = action.entity

    val healthStorage = state.getStorage<Health>()
    val health = healthStorage.getOrThrow(id)

    if (health.state == HealthState.DEAD) {
        return@a addMessage(state, Message("Entity $id is already dead!", Color.YELLOW))
    }

    val skillUsage = state.getData<SkillUsage>()
    val statisticsStorage = state.getStorage<Statistics>()
    val statistics = statisticsStorage.getOrThrow(id)
    val toughnessRank = statistics.getRank(skillUsage.toughness)
    val difficulty = toughnessRank - health.penalty

    val rng = state.getData<RandomNumberState>().createGenerator()
    val checker = state.getData<Checker>()

    val result = checker.check(rng, action.damage.rank, difficulty)

    logger.info("entity=$id toughness=$toughnessRank $health result=$result")

    val updatedStorageMap: MutableMap<KClass<*>, ComponentStorage<*>> = mutableMapOf()
    val updatedDataMap: MutableMap<KClass<*>, Any> = mutableMapOf(RandomNumberState::class to rng.createState())

    val newHealth = updateHealth(health, result)

    if (health != newHealth) {
        logger.info("Change to $newHealth")
        updatedStorageMap[Health::class] = healthStorage.updateAndRemove(mapOf(id to newHealth))

        updateMessageLog(state, health, newHealth, id, updatedDataMap)
    } else {
        val messageLog = state.getData<MessageLog>()
        val message = Message("Entity $id suffers no damage", Color.WHITE)
        updatedDataMap[MessageLog::class] = messageLog.add(message)
    }

    state.copy(updatedStorageMap, updatedDataMap)
}

private fun updateMessageLog(
    state: EcsState,
    health: Health,
    newHealth: Health,
    id: Int,
    updatedDataMap: MutableMap<KClass<*>, Any>
) {
    val messageLog = state.getData<MessageLog>()

    val message = if (health.state != newHealth.state) {
        Message("Entity $id is ${newHealth.state.toDisplayText()}", Color.WHITE)
    } else {
        Message("Entity $id looks slightly worse", Color.WHITE)
    }

    updatedDataMap[MessageLog::class] = messageLog.add(message)
}

private fun updateHealth(health: Health, result: CheckResult) = when (result) {
    is Success -> reduceHealthState(health, calculateSteps(result))
    Draw -> increaseHealthPenalty(health)
    is Failure -> handleFailure(result, health)
}

private fun handleFailure(result: Failure, health: Health) = if (result.rank >= 5) {
    health
} else {
    increaseHealthPenalty(health)
}

private fun calculateSteps(result: Success) = if (result.rank >= 5) {
    2
} else {
    1
}

private fun increaseHealthPenalty(health: Health) = health.copy(penalty = health.penalty + 1)

private fun reduceHealthState(health: Health, steps: Int): Health {
    val newHealthState = health.state.getWorse(steps)
    return health.copy(state = newHealthState, penalty = health.penalty + 1)
}
