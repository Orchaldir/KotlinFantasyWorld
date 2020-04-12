package game

import game.rpg.Damage
import util.math.Direction

object InitAction
data class MoveAction(val entity: Int, val direction: Direction)
data class SufferDamageAction(val entity: Int, val damage: Damage)