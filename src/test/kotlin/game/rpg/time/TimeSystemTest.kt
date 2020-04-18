package game.rpg.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

const val ENTITY0 = 1
const val ENTITY1 = 2
const val ENTITY2 = 3
const val ENTITY3 = 4

class TimeSystemTest {

    @Test
    fun `Add 1 entity while 1 is finished`() {
        val system = TimeSystem(1, listOf(ENTITY0), listOf(ENTITY1))

        assertThat(system.add(ENTITY2)).isEqualTo(TimeSystem(1, listOf(ENTITY0, ENTITY2), listOf(ENTITY1)))
    }

    @Test
    fun `Add entities`() {
        val system = TimeSystem(2, listOf(ENTITY0), listOf(ENTITY1))

        assertThat(system.add(listOf(ENTITY2, ENTITY3)))
            .isEqualTo(TimeSystem(2, listOf(ENTITY0, ENTITY2, ENTITY3), listOf(ENTITY1)))
    }

    @Nested
    inner class Remove {

        @Test
        fun `Remove not existing entity`() {
            val system = TimeSystem(1, listOf(ENTITY0), listOf(ENTITY1))

            assertThat(system.remove(ENTITY2)).isEqualTo(TimeSystem(1, listOf(ENTITY0), listOf(ENTITY1)))
        }

        @Test
        fun `Remove entity`() {
            val system = TimeSystem(1, listOf(ENTITY0), listOf(ENTITY1))

            assertThat(system.remove(ENTITY0)).isEqualTo(TimeSystem(1, finished = listOf(ENTITY1)))
        }

        @Test
        fun `Remove finished entity`() {
            val system = TimeSystem(1, listOf(ENTITY0), listOf(ENTITY1))

            assertThat(system.remove(ENTITY1)).isEqualTo(TimeSystem(1, entities = listOf(ENTITY0)))
        }
    }

    @Test
    fun `Get current entity`() {
        val system = TimeSystem(1, listOf(ENTITY0, ENTITY1), listOf(ENTITY2))

        assertThat(system.getCurrent()).isEqualTo(ENTITY0)
    }

    @Nested
    inner class FinishTurn {

        @Test
        fun `Finish turn`() {
            val system = TimeSystem(1, listOf(ENTITY1, ENTITY2), listOf(ENTITY0))

            assertThat(system.finishTurn(ENTITY1)).isEqualTo(TimeSystem(1, listOf(ENTITY2), listOf(ENTITY0, ENTITY1)))
        }

        @Test
        fun `Finish turn of last entity`() {
            val system = TimeSystem(1, listOf(ENTITY1), listOf(ENTITY0))

            assertThat(system.finishTurn(ENTITY1)).isEqualTo(TimeSystem(2, listOf(ENTITY0, ENTITY1), emptyList()))
        }

        @Test
        fun `Finish turn of non-existing entity`() {
            val system = TimeSystem(1, listOf(ENTITY1, ENTITY2), listOf(ENTITY0))

            assertFailsWith<IllegalArgumentException> { system.finishTurn(ENTITY3) }
        }

        @Test
        fun `Finish turn of finished entity`() {
            val system = TimeSystem(1, listOf(ENTITY1, ENTITY2), listOf(ENTITY0))

            assertFailsWith<IllegalArgumentException> { system.finishTurn(ENTITY0) }
        }

        @Test
        fun `Finish turn of other entity`() {
            val system = TimeSystem(1, listOf(ENTITY1, ENTITY2), listOf(ENTITY0))

            assertFailsWith<IllegalArgumentException> { system.finishTurn(ENTITY2) }
        }
    }
}