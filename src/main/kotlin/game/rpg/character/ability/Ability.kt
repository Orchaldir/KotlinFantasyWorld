package game.rpg.character.ability

import game.rpg.character.skill.Skill

sealed class Ability
data class MeleeAttack(val skill: Skill, val reach: Int, val effect: Effect) : Ability()
data class RangedAttack(
    val skill: Skill,
    val shortRange: Int,
    val mediumRange: Int,
    val longRange: Int,
    val effect: Effect
) : Ability()