package game

data class CannotTargetSelf(val entity: Int) : Exception()
data class NoActionPointsException(val entity: Int) : Exception()
data class NoMovementPointsException(val entity: Int) : Exception()
data class OutOfRangeException(val distance: Int) : Exception()