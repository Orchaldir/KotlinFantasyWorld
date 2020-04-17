package game.rpg.time

import game.component.Statistics
import game.rpg.character.skill.Skill
import util.ecs.EcsState

data class TurnData(val movementPoints: Int, val maxMovementPoints: Int) {

    constructor(movementPoints: Int) : this(movementPoints, movementPoints)

    fun isFinished() = movementPoints <= 0

    fun reduceMovementPoints(): TurnData {
        require(movementPoints > 0) { "Can not reduce movement points below 0!" }
        return copy(movementPoints = movementPoints - 1)
    }

}

fun createTurnData(state: EcsState, timeSystem: TimeSystem, speed: Skill): TurnData {
    val entity = timeSystem.entities.first()
    val statisticsStorage = state.getStorage<Statistics>()
    val movementPoints = statisticsStorage[entity]?.getRank(speed) ?: 0

    return TurnData(movementPoints)
}