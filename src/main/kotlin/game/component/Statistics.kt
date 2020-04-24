package game.component

import game.rpg.character.skill.Skill
import util.ecs.EcsState

const val DEFAULT_RANK = 0

data class Statistics(
    val skillMap: Map<Skill, Int>
) {

    fun getRank(skill: Skill) = skillMap.getOrDefault(skill, DEFAULT_RANK)

}

fun getRank(state: EcsState, entity: Int, skill: Skill) =
    state.getStorage<Statistics>().get(entity)?.getRank(skill) ?: DEFAULT_RANK