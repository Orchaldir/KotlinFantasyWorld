package game

import util.math.Direction

object InitAction
data class MoveAction(val entity: Int, val direction: Direction)