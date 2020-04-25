package game.component

import game.rpg.character.Defense
import game.rpg.character.ability.Ability
import util.ecs.EcsState

data class Combat(private val abilities: List<Ability>, val defense: Defense) {

    fun getAbility(entity: Int, index: Int): Ability {
        require(index < abilities.size) { "Can not access ability $index of entity $entity!" }
        return abilities[index]
    }

    fun getAbilityOrNull(index: Int) = abilities.getOrNull(index)

}

fun getAbility(state: EcsState, entity: Int, index: Int) =
    state.getStorage<Combat>().getOrThrow(entity).getAbility(entity, index)

fun getAbilityOrNull(state: EcsState, entity: Int, index: Int) =
    state.getStorage<Combat>().get(entity)?.getAbilityOrNull(index)

fun getDefense(state: EcsState, entity: Int) =
    state.getStorage<Combat>().getOrThrow(entity).defense