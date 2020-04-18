package game.rpg.character.ability

import game.rpg.Damage

sealed class Effect
data class DamageEffect(val damage: Damage) : Effect()