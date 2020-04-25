package game.rpg.character.ability

import game.component.Body
import game.component.calculateDistanceToPosition
import game.map.GameMap
import game.rpg.character.skill.Skill
import util.ecs.EcsState

sealed class Ability
data class MeleeAttack(val skill: Skill, val reach: Int, val effect: Effect) : Ability()
data class RangedAttack(
    val skill: Skill,
    val shortRange: Int,
    val mediumRange: Int,
    val longRange: Int,
    val effect: Effect
) : Ability()

sealed class AbilityCheckResult
object CannotTargetSelf : AbilityCheckResult()
object NoTarget : AbilityCheckResult()
data class OutOfRange(val target: Int, val position: Int, val distance: Int) : AbilityCheckResult()
data class ValidUsage(val target: Int, val position: Int, val distance: Int) : AbilityCheckResult()

fun checkAbility(state: EcsState, ability: Ability, entity: Int, position: Int): AbilityCheckResult {
    val map = state.getData<GameMap>()
    val target = map.entities[position] ?: return NoTarget

    if (entity == target) return CannotTargetSelf

    val body = state.getStorage<Body>()[entity] ?: return NoTarget
    val distance = calculateDistanceToPosition(map.size, body, position)

    return when (ability) {
        is MeleeAttack -> {
            if (distance > ability.reach) return OutOfRange(target, position, distance)
            else ValidUsage(target, position, distance)
        }
        is RangedAttack -> {
            if (distance > ability.longRange) return OutOfRange(target, position, distance)
            else ValidUsage(target, position, distance)
        }
    }
}