package game.rpg.character.skill

class Skill(
    val name: String
) : Comparable<Skill> {

    override fun compareTo(other: Skill) = name.compareTo(other.name)

}