package game.rpg.time

import game.component.Statistics
import game.rpg.character.skill.Skill
import util.ecs.EcsState

data class TurnData(val movementPoints: Int, val maxMovementPoints: Int) {
    constructor(movementPoints: Int) : this(movementPoints, movementPoints)

    fun reduceMovementPoints() = copy(movementPoints = movementPoints - 1)
}

fun createTurnData(state: EcsState, timeSystem: TimeSystem, speed: Skill): TurnData {
    val entity = timeSystem.entities.first()
    val statisticsStorage = state.getStorage<Statistics>()
    val movementPoints = statisticsStorage[entity]?.getRank(speed) ?: 0

    return TurnData(movementPoints)
}