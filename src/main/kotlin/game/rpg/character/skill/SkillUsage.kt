package game.rpg.character.skill

private val UNDEFINED = Skill("UNDEFINED")

data class SkillUsage(val speed: Skill = UNDEFINED, val toughness: Skill = UNDEFINED)
