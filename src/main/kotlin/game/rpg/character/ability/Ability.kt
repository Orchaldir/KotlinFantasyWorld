package game.rpg.character.ability

import game.rpg.character.skill.Skill

sealed class Ability
data class MeleeAttack(val skill: Skill, val reach: Int, val effect: Effect) : Ability()