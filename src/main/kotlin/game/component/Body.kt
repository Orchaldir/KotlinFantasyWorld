package game.component

sealed class Body
data class SimpleBody(val position: Int) : Body()
data class BigBody(val position: Int, val size: Int) : Body()
data class SnakeBody(val positions: List<Int>) : Body()