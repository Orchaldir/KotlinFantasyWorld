package game.rpg.time

import game.component.Combat
import game.component.Statistics
import game.rpg.character.skill.Skill
import util.ecs.EcsState

data class TurnData(
    val movementPoints: Int,
    val maxMovementPoints: Int,
    val actionsPerTurn: Int,
    val maxActionsPerTurn: Int
) {

    constructor(movementPoints: Int, actions: Int) : this(movementPoints, movementPoints, actions, actions)

    fun isFinished() = movementPoints <= 0 && actionsPerTurn <= 0

    fun reduceMovementPoints(): TurnData {
        require(movementPoints > 0) { "Can not reduce movement points below 0!" }
        return copy(movementPoints = movementPoints - 1)
    }

    fun reduceActions(): TurnData {
        require(actionsPerTurn > 0) { "Can not reduce actions below 0!" }
        return copy(actionsPerTurn = actionsPerTurn - 1)
    }

}

fun createTurnData(state: EcsState, timeSystem: TimeSystem, speed: Skill): TurnData {
    val entity = timeSystem.entities.first()

    val statisticsStorage = state.getOptionalStorage<Statistics>()
    val movementPoints = statisticsStorage?.get(entity)?.getRank(speed) ?: 0

    val combatStorage = state.getOptionalStorage<Combat>()
    val actions = if (combatStorage != null) 1 else 0

    return TurnData(movementPoints, actions)
}