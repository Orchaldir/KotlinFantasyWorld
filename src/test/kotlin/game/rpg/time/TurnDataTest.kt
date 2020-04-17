package game.rpg.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import game.component.Statistics
import game.rpg.character.skill.Skill
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.ecs.EcsState
import util.ecs.storage.ComponentMap
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurnDataTest {

    @Test
    fun `Test constructor with only movement points`() {
        assertThat(TurnData(6)).isEqualTo(TurnData(6, 6))
    }

    @Nested
    inner class IsFinished {

        @Test
        fun `Entity is finished with 0 movement points`() {
            assertTrue(TurnData(0).isFinished())
            assertTrue(TurnData(-1).isFinished())
        }

        @Test
        fun `Entity is not finished with more than 0 movement points`() {
            assertFalse(TurnData(1).isFinished())
        }

    }

    @Nested
    inner class ReduceMovementPoints {

        @Test
        fun `Reduce movement points`() {
            val data = TurnData(6).reduceMovementPoints()
            assertThat(data).isEqualTo(TurnData(5, 6))
        }

        @Test
        fun `Reduce 0 movement points`() {
            assertFailsWith<IllegalArgumentException> { TurnData(0).reduceMovementPoints() }
        }

    }

    @Nested
    inner class CreateTurnData {

        private val entity = 3
        private val speed = Skill("Speed")

        @Test
        fun `Create turn data with speed`() {
            val statistics = Statistics(mapOf(speed to 4))
            val storage = ComponentMap("S", mapOf(entity to statistics))
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(Statistics::class to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(4))
        }

        @Test
        fun `Create turn data without speed`() {
            val statistics = Statistics(emptyMap())
            val storage = ComponentMap("S", mapOf(entity to statistics))
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(Statistics::class to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(0))
        }

        @Test
        fun `Create turn data without statistics`() {
            val storage = ComponentMap<Statistics>("S", emptyMap())
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(Statistics::class to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(0))
        }

    }
}