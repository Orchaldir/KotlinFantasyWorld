package game.action

import game.rpg.Damage
import util.math.Direction

sealed class Action
data class FinishTurn(val entity: Int) : Action()
object Init : Action()
data class Move(val entity: Int, val direction: Direction) : Action()
data class UseAbility(val entity: Int, val ability: Int, val position: Int) : Action()

// events
data class OnDamage(val entity: Int, val damage: Damage) : Action()