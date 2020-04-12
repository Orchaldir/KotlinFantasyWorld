package game.rpg.character.skill

class SkillManager(skills: List<Skill>) {

    private val skillMap = HashMap<String, Skill>().apply {
        skills.forEach { s -> this[s.name] = s }
    }

    operator fun get(name: String) = skillMap[name] ?: throw NoSuchElementException("Did not find skill '$name'!")

    fun getAllSkills() = skillMap.values.asSequence().sorted().toList()

}