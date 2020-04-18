package game.component

import game.rpg.character.Defense
import game.rpg.character.ability.Ability

data class Combat(val abilities: List<Ability>, val defense: Defense)