package game.rpg.character.skill

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

private const val NAME0 = "A"
private const val NAME1 = "B"
private const val NAME2 = "C"
private const val NON_EXISTING = "?"

private val SKILL0 = Skill(NAME0)
private val SKILL1 = Skill(NAME1)
private val SKILL2 = Skill(NAME2)

private val manager = SkillManager(listOf(SKILL1, SKILL2, SKILL0))

class SkillManagerTest {

    @Nested
    inner class Get {

        @Test
        fun `Get existing skill`() {
            assertThat(manager[NAME0]).isSameAs(SKILL0)
            assertThat(manager[NAME1]).isSameAs(SKILL1)
            assertThat(manager[NAME2]).isSameAs(SKILL2)
        }

        @Test
        fun `Get non-existing skill`() {
            assertFailsWith<NoSuchElementException>("Did not find skill '$NON_EXISTING'!") { manager[NON_EXISTING] }
        }

    }

    @Test
    fun `Get all skills`() {
        assertThat(manager.getAllSkills()).containsExactly(SKILL0, SKILL1, SKILL2)
    }

}