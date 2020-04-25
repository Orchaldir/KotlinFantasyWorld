package game.rpg.time

import game.NoActionPointsException
import game.NoMovementPointsException
import game.component.Combat
import game.component.Statistics
import game.rpg.character.skill.Skill
import util.ecs.EcsState

data class TurnData(
    val movementPoints: Int,
    val maxMovementPoints: Int,
    val actionPoints: Int,
    val maxActionsPoints: Int
) {

    constructor(movementPoints: Int, actions: Int) : this(movementPoints, movementPoints, actions, actions)

    fun canAct() = actionPoints > 0

    fun canMove() = movementPoints > 0

    fun isFinished() = movementPoints <= 0 && actionPoints <= 0

    fun reduceMovementPoints(entity: Int): TurnData {
        if (movementPoints <= 0) throw NoMovementPointsException(entity)
        return copy(movementPoints = movementPoints - 1)
    }

    fun reduceActionPoints(entity: Int): TurnData {
        if (actionPoints <= 0) throw NoActionPointsException(entity)
        return copy(actionPoints = actionPoints - 1)
    }

}

fun createTurnData(state: EcsState, timeSystem: TimeSystem, speed: Skill): TurnData {
    val entity = timeSystem.getCurrent()

    val statisticsStorage = state.getOptionalStorage<Statistics>()
    val movementPoints = statisticsStorage?.get(entity)?.getRank(speed) ?: 0

    val combatStorage = state.getOptionalStorage<Combat>()
    val actions = if (combatStorage != null) 1 else 0

    return TurnData(movementPoints, actions)
}