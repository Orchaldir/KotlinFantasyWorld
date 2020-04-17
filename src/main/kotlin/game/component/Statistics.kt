package game.component

import game.rpg.character.skill.Skill

const val DEFAULT_RANK = 0

data class Statistics(
    val skillMap: Map<Skill, Int>
) {

    fun getRank(skill: Skill) = skillMap.getOrDefault(skill, DEFAULT_RANK)

}