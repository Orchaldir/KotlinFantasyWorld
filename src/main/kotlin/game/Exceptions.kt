package game

import game.rpg.character.ability.AbilityCheckResult

data class InvalidAbilityUsageException(val error: AbilityCheckResult) : Exception()
data class NoActionPointsException(val entity: Int) : Exception()
data class NoMovementPointsException(val entity: Int) : Exception()