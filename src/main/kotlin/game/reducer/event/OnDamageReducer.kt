package game.reducer.event

import game.action.Action
import game.action.OnDamage
import game.action.OnDeath
import game.component.Health
import game.component.HealthState.DEAD
import game.component.getRank
import game.rpg.character.skill.SkillUsage
import game.rpg.check.*
import mu.KotlinLogging
import util.ecs.EcsState
import util.ecs.storage.ComponentStorage
import util.log.MessageLog
import util.log.addMessage
import util.log.inform
import util.log.warn
import util.redux.Reducer
import util.redux.noFollowUps
import util.redux.random.RandomNumberState

private val logger = KotlinLogging.logger {}

val ON_DAMAGE_REDUCER: Reducer<Action, EcsState> = a@{ state, action ->
    if (action !is OnDamage) throw IllegalArgumentException("Action must be OnDamage!")
    val id = action.entity

    val healthStorage = state.getStorage<Health>()
    val health = healthStorage.getOrThrow(id)

    if (health.state == DEAD) {
        return@a noFollowUps(addMessage(state, warn(state, "%s is already dead!", id)))
    }

    val skillUsage = state.getData<SkillUsage>()
    val toughnessRank = getRank(state, id, skillUsage.toughness)
    val difficulty = toughnessRank - health.penalty

    val rng = state.getData<RandomNumberState>().createGenerator()
    val checker = state.getData<Checker>()

    val result = checker.check(rng, action.damage.rank, difficulty)

    logger.info("entity=$id toughness=$toughnessRank $health result=$result")

    val updatedStorage = mutableListOf<ComponentStorage<*>>()
    val updatedData = mutableListOf<Any>(rng.createState())
    val events = mutableListOf<Action>()

    val newHealth = updateHealth(health, result)

    if (health != newHealth) {
        logger.info("Change to $newHealth")
        updatedStorage += healthStorage.updateAndRemove(mapOf(id to newHealth))

        if (newHealth.state == DEAD) {
            events.add(OnDeath(id))
        }

        updateMessageLog(state, health, newHealth, id, updatedData)
    } else {
        val messageLog = state.getData<MessageLog>()
        val message = inform(state, "%s suffers no damage", id)
        updatedData += messageLog.add(message)
    }

    Pair(state.copy(updatedStorage, updatedData), events)
}

private fun updateMessageLog(
    state: EcsState,
    health: Health,
    newHealth: Health,
    id: Int,
    updatedDataMap: MutableList<Any>
) {
    val messageLog = state.getData<MessageLog>()

    val message = if (health.state != newHealth.state) {
        inform(state, "%s is ${newHealth.state.toDisplayText()}", id)
    } else {
        inform(state, "%s looks slightly worse", id)
    }

    updatedDataMap += messageLog.add(message)
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
