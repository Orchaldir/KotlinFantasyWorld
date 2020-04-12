package game.component

import assertk.assertThat
import assertk.assertions.isSameAs
import game.rpg.character.skill.Skill
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val SKILL0 = Skill("A")
private val SKILL1 = Skill("B")
private val SKILL2 = Skill("C")
private val UNKNOWN = Skill("?")

private val skillMap = mapOf(SKILL0 to 1, SKILL1 to 2, SKILL2 to 3)
private val statistics = Statistics(skillMap)

class StatisticsTest {

    @Nested
    inner class GetRank {

        @Test
        fun `Get rank of known skills`() {
            assertThat(statistics.getRank(SKILL0)).isSameAs(1)
            assertThat(statistics.getRank(SKILL1)).isSameAs(2)
            assertThat(statistics.getRank(SKILL2)).isSameAs(3)
        }

        @Test
        fun `Get rank of unknown skill`() {
            assertThat(statistics.getRank(UNKNOWN)).isSameAs(0)
        }

    }

    @Test
    fun `Get skill map`() {
        assertThat(statistics.skillMap).isSameAs(skillMap)
    }

}