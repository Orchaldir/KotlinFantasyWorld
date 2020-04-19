package game.component

import game.rpg.character.Defense
import game.rpg.character.ability.Ability

data class Combat(private val abilities: List<Ability>, val defense: Defense) {

    fun getAbility(entity: Int, index: Int): Ability {
        require(index < abilities.size) { "Can not access ability $index of entity $entity!" }
        return abilities[index]
    }

}