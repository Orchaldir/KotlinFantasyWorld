package game.rpg.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import game.component.Combat
import game.component.Statistics
import game.rpg.character.skill.Skill
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.ecs.EcsState
import util.ecs.getType
import util.ecs.storage.ComponentMap
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurnDataTest {

    @Test
    fun `Test constructor`() {
        assertThat(TurnData(6, 2)).isEqualTo(TurnData(6, 6, 2, 2))
    }

    @Nested
    inner class IsMovementFinished {

        @Test
        fun `Entity has no movement or actions left`() {
            assertTrue(TurnData(0, 0).isFinished())
            assertTrue(TurnData(-1, -1).isFinished())
        }

        @Test
        fun `Entity is not finished with more than 0 movement points`() {
            assertFalse(TurnData(1, 0).isFinished())
        }

        @Test
        fun `Entity has an action left`() {
            assertFalse(TurnData(0, 1).isFinished())
        }

        @Test
        fun `Entity has movement and an action left`() {
            assertFalse(TurnData(2, 1).isFinished())
        }

    }

    @Nested
    inner class ReduceMovementPoints {

        @Test
        fun `Reduce movement points`() {
            val data = TurnData(6, 1).reduceMovementPoints()
            assertThat(data).isEqualTo(TurnData(5, 6, 1, 1))
        }

        @Test
        fun `Reduce 0 movement points`() {
            assertFailsWith<IllegalArgumentException> { TurnData(0, 1).reduceMovementPoints() }
        }

    }

    @Nested
    inner class ReduceActions {

        @Test
        fun `Reduce actions`() {
            val data = TurnData(6, 2).reduceActions()
            assertThat(data).isEqualTo(TurnData(6, 6, 1, 2))
        }

        @Test
        fun `Reduce 0 movement points`() {
            assertFailsWith<IllegalArgumentException> { TurnData(1, 0).reduceActions() }
        }

    }

    @Nested
    inner class CreateTurnData {

        private val type = getType(Statistics::class)
        private val type1 = getType(Combat::class)
        private val entity = 3
        private val speed = Skill("Speed")

        @Test
        fun `Create turn data with speed`() {
            val statistics = Statistics(mapOf(speed to 4))
            val storage = ComponentMap("S", mapOf(entity to statistics))
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(type to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(4, 0))
        }

        @Test
        fun `Create turn data without speed`() {
            val statistics = Statistics(emptyMap())
            val storage = ComponentMap("S", mapOf(entity to statistics))
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(type to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(0, 0))
        }

        @Test
        fun `Create turn data without statistics`() {
            val storage = ComponentMap<Statistics>("S", emptyMap())
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(type to storage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(0, 0))
        }

        @Test
        fun `Create turn data with combat storage`() {
            val statisticsStorage = ComponentMap<Statistics>("S", emptyMap())
            val combatStorage = ComponentMap<Combat>("S", emptyMap())
            val timeSystem = TimeSystem(0, listOf(entity))
            val state = EcsState(storageMap = mapOf(type1 to combatStorage, type to statisticsStorage))

            val data = createTurnData(state, timeSystem, speed)

            assertThat(data).isEqualTo(TurnData(0, 1))
        }

    }
}