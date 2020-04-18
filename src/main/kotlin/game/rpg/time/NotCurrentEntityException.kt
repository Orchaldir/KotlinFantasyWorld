package game.rpg.time

data class NotCurrentEntityException(val entity: Int, val current: Int) : Exception()